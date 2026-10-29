package com.policy.renewal.service;

import com.policy.renewal.model.Policy;
import com.policy.renewal.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * PolicyService — all business logic for the Policy Renewal Reminder System.
 *
 * <p>This service class is the heart of the application. All business rules defined
 * in the SRS (Business Rules BR-01 through BR-07) are enforced here. The controller
 * layer calls this service; the controller itself contains no business logic.
 *
 * <p>Key responsibilities:
 * <ul>
 *   <li>Saving and retrieving policy records (via the repository)</li>
 *   <li>Enforcing the duplicate policy number rule (BR-01)</li>
 *   <li>Enforcing the date order rule (BR-04)</li>
 *   <li>Deriving renewal status from dates (BR-03)</li>
 *   <li>Computing dashboard statistics (BR-06)</li>
 *   <li>Finding upcoming renewals (FR-09)</li>
 * </ul>
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Service
@Transactional
public class PolicyService {

    // =========================================================================
    // Constants for derived statuses (used as display labels)
    // =========================================================================

    public static final String STATUS_RENEWED       = "RENEWED";
    public static final String STATUS_EXPIRED       = "EXPIRED";
    public static final String STATUS_EXPIRING_SOON = "EXPIRING_SOON";
    public static final String STATUS_ACTIVE        = "ACTIVE";

    // =========================================================================
    // Dependencies
    // =========================================================================

    private final PolicyRepository policyRepository;
    private Clock clock = Clock.systemDefaultZone();
    private AuditLogService auditLogService;

    /**
     * Alert window: number of days before expiry at which a policy is considered
     * "Expiring Soon". Configured via application.properties (renewal.alert.days).
     * Defaults to 30 if not configured.
     */
    @Value("${renewal.alert.days:30}")
    private int renewalAlertDays;

    /**
     * Single-argument constructor for backwards compatibility and standalone testing.
     *
     * @param policyRepository the repository for POLICY database operations
     */
    public PolicyService(PolicyRepository policyRepository) {
        this(policyRepository, Clock.systemDefaultZone(), null);
    }

    /**
     * Constructor injection with Clock bean for deterministic date handling.
     *
     * @param policyRepository the repository for POLICY database operations
     * @param clock the clock instance to determine current time
     */
    public PolicyService(PolicyRepository policyRepository, Clock clock) {
        this(policyRepository, clock, null);
    }

    /**
     * Full constructor injection with Clock bean and optional AuditLogService.
     *
     * @param policyRepository the repository for POLICY database operations
     * @param clock the clock instance to determine current time
     * @param auditLogService optional service for persisting audit log entries
     */
    @org.springframework.beans.factory.annotation.Autowired
    public PolicyService(PolicyRepository policyRepository, Clock clock,
                         @org.springframework.beans.factory.annotation.Autowired(required = false) AuditLogService auditLogService) {
        this.policyRepository = policyRepository;
        this.clock = clock != null ? clock : Clock.systemDefaultZone();
        this.auditLogService = auditLogService;
    }

    public void setClock(Clock clock) {
        if (clock != null) {
            this.clock = clock;
        }
    }

    public void setAuditLogService(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    // =========================================================================
    // Read operations
    // =========================================================================

    /**
     * Returns all policies sorted by expiry date ascending.
     * Used by FR-02 (View All Policies).
     *
     * @return list of all policies, sorted soonest-expiring first
     */
    @Transactional(readOnly = true)
    public List<Policy> getAllPolicies() {
        return policyRepository.findAllByOrderByExpiryDateAsc();
    }

    /**
     * Returns a single policy by its numeric ID.
     * Used by FR-03 (View Policy Details) and FR-04 (Edit Policy).
     *
     * @param id the primary key of the policy
     * @return an Optional containing the policy if found, or empty
     */
    @Transactional(readOnly = true)
    public Optional<Policy> findById(Long id) {
        return policyRepository.findById(id);
    }

    // =========================================================================
    // Create and Update operations
    // =========================================================================

    /**
     * Saves a new or updated policy to the database.
     *
     * <p>Business rules enforced:
     * <ul>
     *   <li>BR-04: expiryDate must not be before startDate</li>
     *   <li>BR-01: policyNumber must be unique (checked before saving)</li>
     * </ul>
     *
     * <p>Note: Field-level validation (@NotBlank, @NotNull, etc.) is enforced by
     * Spring's bean validation framework before this method is called. This method
     * enforces cross-field and uniqueness rules.
     *
     * @param policy the policy to save
     * @return the saved policy with its generated ID populated
     * @throws IllegalArgumentException if a business rule is violated
     */
    public Policy save(Policy policy) {
        // BR-04: date order validation
        if (policy.getStartDate() != null && policy.getExpiryDate() != null
                && policy.getExpiryDate().isBefore(policy.getStartDate())) {
            throw new IllegalArgumentException(
                    "Expiry date must not be earlier than the start date.");
        }

        // Requirement 1.B & 4: Requested coverage duration validation
        if (policy.getCoverageDurationValue() != null && policy.getCoverageDurationValue() <= 0) {
            throw new IllegalArgumentException("Coverage duration must be a positive number.");
        }
        if (policy.getCoverageDurationUnit() == null || policy.getCoverageDurationUnit().isBlank()) {
            policy.setCoverageDurationUnit("MONTHS");
        }

        // Validate consistency between current coverage dates and requested duration (Requirement 6)
        if (policy.getStartDate() != null && policy.getExpiryDate() != null
                && policy.getCoverageDurationValue() != null && !policy.isCoveragePeriodConsistent()) {
            LocalDate expected = calculateCoverageEndDate(policy.getStartDate(), policy.getCoverageDurationValue(), policy.getCoverageDurationUnit());
            throw new IllegalArgumentException(
                    "Coverage dates are inconsistent with requested duration (" +
                    policy.getCoverageDurationValue() + " " + policy.getCoverageDurationUnit() + "). " +
                    "A " + policy.getCoverageDurationValue() + " " + policy.getCoverageDurationUnit() +
                    " term starting on " + policy.getStartDate() + " should expire on or around " + expected +
                    ", but " + policy.getExpiryDate() + " was entered (" + policy.getActualCoverageMonths() + " months span). " +
                    "For renewed policies, enter the start date of the current coverage term as Start Date, and keep the original inception date under Customer Since Date.");
        }

        // Derive coverage duration accurately if not explicitly provided
        if (policy.getCoverageDurationValue() == null && policy.getStartDate() != null && policy.getExpiryDate() != null) {
            long days = policy.getActualCoverageDays();
            if (days < 30) {
                policy.setCoverageDurationValue((int) days);
                policy.setCoverageDurationUnit("DAYS");
            } else {
                long months = policy.getActualCoverageMonths();
                policy.setCoverageDurationValue(months > 0 ? (int) months : 12);
                policy.setCoverageDurationUnit("MONTHS");
            }
        }

        // Requirement 1.A: For a brand new policy without prior history, inception date matches policy start date
        if (policy.getId() == null && policy.getCustomerSinceDate() == null && policy.getStartDate() != null) {
            policy.setCustomerSinceDate(policy.getStartDate());
        }

        // Maintain pricing breakdown consistency
        if (policy.getBasePremium() == null && policy.getPremiumAmount() != null) {
            // Default 18% GST allocation if not explicitly populated
            java.math.BigDecimal total = policy.getPremiumAmount();
            java.math.BigDecimal base = total.divide(new java.math.BigDecimal("1.18"), 2, java.math.RoundingMode.HALF_UP);
            policy.setBasePremium(base);
            policy.setTaxAmount(total.subtract(base));
        }

        // BR-01: duplicate policy number check
        // When creating a new policy, id is null. When updating, we exclude the
        // current record from the uniqueness check (a policy can "keep" its own number).
        Optional<Policy> existing = policyRepository.findByPolicyNumber(policy.getPolicyNumber());
        if (existing.isPresent() && !existing.get().getId().equals(policy.getId())) {
            throw new IllegalArgumentException(
                    "Policy number '" + policy.getPolicyNumber() + "' is already in use.");
        }

        boolean isNew = (policy.getId() == null);
        Policy saved = policyRepository.save(policy);
        if (auditLogService != null) {
            String action = isNew ? com.policy.renewal.model.AuditLog.ACTION_POLICY_CREATED : com.policy.renewal.model.AuditLog.ACTION_POLICY_UPDATED;
            auditLogService.logSuccess(action, com.policy.renewal.model.AuditLog.RESOURCE_POLICY, saved.getPolicyNumber(),
                    (isNew ? "Created" : "Updated") + " policy " + saved.getPolicyNumber() + " for " + saved.getCustomerName());
        }
        return saved;
    }

    // =========================================================================
    // Requirement 1: Insurance Period Calculations
    // =========================================================================

    /**
     * Requirement 1.A — Customer Relationship Period (Historical Policy Tenure)
     *
     * <p>Calculates the duration for which the customer has been associated with the policy/insurer,
     * computed from the customer inception date (or policy start date) to the current date.
     *
     * <p>CRITICAL INVARIANT:
     * This is a historical duration. It must NEVER be used to calculate the amount the customer
     * needs to pay for a new policy.
     *
     * @param policy the policy record
     * @return a user-friendly formatted duration string, e.g. "3 years" or "2 years, 6 months"
     */
    public String calculateCustomerRelationshipPeriod(Policy policy) {
        if (policy == null) {
            return "Not Available";
        }
        LocalDate inceptionDate = policy.getCustomerSinceDate();
        if (inceptionDate == null) {
            if (STATUS_RENEWED.equals(policy.getRenewalStatus())) {
                return "Unavailable — original inception date not recorded";
            }
            inceptionDate = policy.getStartDate();
        }

        if (inceptionDate == null) {
            return "Policy start date not available";
        }

        LocalDate today = LocalDate.now(clock);
        if (today.isBefore(inceptionDate)) {
            long days = java.time.temporal.ChronoUnit.DAYS.between(today, inceptionDate);
            return "Future Policy (Starts in " + days + " days)";
        }

        java.time.Period period = java.time.Period.between(inceptionDate, today);
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();

        if (years > 0 && months > 0) {
            return years + (years == 1 ? " year, " : " years, ") + months + (months == 1 ? " month" : " months");
        } else if (years > 0) {
            return years + (years == 1 ? " year" : " years");
        } else if (months > 0) {
            return months + (months == 1 ? " month" : " months");
        } else if (days > 0) {
            return days + (days == 1 ? " day" : " days") + " (< 1 month)";
        } else {
            return "New Customer (Inception Today)";
        }
    }

    /**
     * Requirement 1.B — Calculates coverage end date according to the requested coverage duration.
     *
     * @param startDate coverage inception date
     * @param durationValue positive number of duration units
     * @param durationUnit duration unit (MONTHS, YEARS, DAYS)
     * @return calculated expiry date
     */
    public LocalDate calculateCoverageEndDate(LocalDate startDate, Integer durationValue, String durationUnit) {
        if (startDate == null || durationValue == null || durationValue <= 0) {
            return null;
        }
        String unit = (durationUnit != null ? durationUnit.trim().toUpperCase() : "MONTHS");
        if (unit.startsWith("DAY")) {
            return startDate.plusDays(durationValue);
        } else if (unit.startsWith("YEAR")) {
            return startDate.plusYears(durationValue).minusDays(1);
        } else {
            return startDate.plusMonths(durationValue).minusDays(1);
        }
    }

    /**
     * Marks an existing policy as Renewed.
     *
     * <p>Setting renewalStatus to "RENEWED" means the status derivation algorithm
     * (BR-03) will always return "Renewed" for this policy, regardless of its
     * expiry date. This implements FR-11 and prevents the policy from being
     * incorrectly classified as Expired (NFR-10).
     *
     * @param id the ID of the policy to mark as renewed
     * @throws IllegalArgumentException if no policy with the given ID exists
     */
    public void markAsRenewed(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Policy not found with ID: " + id));

        // Requirement 5: Preserve original inception date before marking as renewed
        if (policy.getCustomerSinceDate() == null && policy.getStartDate() != null) {
            policy.setCustomerSinceDate(policy.getStartDate());
        }

        policy.setRenewalStatus(STATUS_RENEWED);
        Policy saved = policyRepository.save(policy);
        if (auditLogService != null) {
            auditLogService.logSuccess(com.policy.renewal.model.AuditLog.ACTION_POLICY_RENEWED,
                    com.policy.renewal.model.AuditLog.RESOURCE_POLICY, saved.getPolicyNumber(),
                    "Marked policy " + saved.getPolicyNumber() + " as renewed.");
        }
    }

    /**
     * Deletes a policy by its numeric ID.
     *
     * @param id the primary key ID of the policy to delete
     * @throws IllegalArgumentException if no policy with the given ID exists
     */
    public void deleteById(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Policy not found with ID: " + id));
        String policyNumber = policy.getPolicyNumber();
        String customerName = policy.getCustomerName();
        policyRepository.delete(policy);
        if (auditLogService != null) {
            auditLogService.logSuccess(com.policy.renewal.model.AuditLog.ACTION_POLICY_DELETED,
                    com.policy.renewal.model.AuditLog.RESOURCE_POLICY, policyNumber,
                    "Deleted policy " + policyNumber + " (Customer: " + customerName + ")");
        }
    }

    // =========================================================================
    // Status derivation — BR-03 (NFR-10: implemented in ONE place only)
    // =========================================================================

    /**
     * Derives the display status of a policy based on its stored renewalStatus
     * field and the relationship between its expiryDate and today's date.
     *
     * <p>Algorithm (priority order, as defined in BR-03):
     * <ol>
     *   <li>If stored renewalStatus is "RENEWED" → return "RENEWED"</li>
     *   <li>If expiryDate is before today → return "EXPIRED"</li>
     *   <li>If expiryDate is within the alert window → return "EXPIRING_SOON"</li>
     *   <li>Otherwise → return "ACTIVE"</li>
     * </ol>
     *
     * <p>This method is the ONLY place in the codebase where this algorithm lives.
     * It must not be duplicated in controllers, templates or repositories.
     *
     * @param policy the policy whose status is to be derived
     * @return one of: "RENEWED", "EXPIRED", "EXPIRING_SOON", "ACTIVE"
     */
    public String getDerivedStatus(Policy policy) {
        if (policy == null) {
            return STATUS_ACTIVE;
        }
        if (STATUS_RENEWED.equals(policy.getRenewalStatus())) {
            return STATUS_RENEWED;
        }
        if (policy.getExpiryDate() == null) {
            return STATUS_ACTIVE;
        }
        LocalDate today = LocalDate.now(clock);
        if (policy.getExpiryDate().isBefore(today)) {
            return STATUS_EXPIRED;
        }
        LocalDate alertCutoff = today.plusDays(renewalAlertDays);
        if (!policy.getExpiryDate().isAfter(alertCutoff)) {
            return STATUS_EXPIRING_SOON;
        }
        return STATUS_ACTIVE;
    }

    // =========================================================================
    // Upcoming renewals — FR-09
    // =========================================================================

    /**
     * Returns policies expiring within the configured alert window that have not
     * been marked as Renewed.
     *
     * @return list of upcoming-renewal policies, sorted by expiry date ascending
     */
    @Transactional(readOnly = true)
    public List<Policy> getUpcomingRenewals() {
        return getUpcomingRenewals(renewalAlertDays);
    }

    /**
     * Returns policies expiring within the specified days window that have not
     * been marked as Renewed.
     *
     * @param days number of days ahead to search
     * @return list of upcoming-renewal policies, sorted by expiry date ascending
     */
    @Transactional(readOnly = true)
    public List<Policy> getUpcomingRenewals(int days) {
        LocalDate today = LocalDate.now(clock);
        LocalDate cutoff = today.plusDays(days);
        return policyRepository.findUpcomingRenewals(today, cutoff);
    }

    // =========================================================================
    // Search & Filter — FR-06, FR-07, FR-08
    // =========================================================================

    /**
     * Searches for policies matching the given search term in customer name,
     * policy number, or customer email. The search is case-insensitive and partial.
     *
     * @param searchTerm the text entered by the user
     * @return list of matching policies; empty list if no matches
     */
    @Transactional(readOnly = true)
    public List<Policy> search(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return getAllPolicies();
        }
        // Search customer name, policy number, and customer email
        List<Policy> byName = policyRepository
                .findByCustomerNameContainingIgnoreCaseOrderByExpiryDateAsc(searchTerm.trim());
        List<Policy> byNumber = policyRepository
                .findByPolicyNumberContainingIgnoreCase(searchTerm.trim());
        List<Policy> byEmail = policyRepository
                .findByCustomerEmailContainingIgnoreCase(searchTerm.trim());

        // Merge results without duplicates
        for (Policy p : byNumber) {
            if (byName.stream().noneMatch(existing -> existing.getId().equals(p.getId()))) {
                byName.add(p);
            }
        }
        for (Policy p : byEmail) {
            if (byName.stream().noneMatch(existing -> existing.getId().equals(p.getId()))) {
                byName.add(p);
            }
        }
        return byName;
    }

    /**
     * Filters a list of policies by derived status and expiry date range.
     *
     * @param policies   input list of policies
     * @param status     derived status filter (ACTIVE, EXPIRING_SOON, EXPIRED, RENEWED), or null/empty/ALL
     * @param expiryFrom minimum expiry date (inclusive), or null
     * @param expiryTo   maximum expiry date (inclusive), or null
     * @return filtered list of policies
     */
    public List<Policy> filterPolicies(List<Policy> policies, String status, LocalDate expiryFrom, LocalDate expiryTo) {
        if (policies == null) {
            return List.of();
        }
        return policies.stream()
                .filter(p -> {
                    if (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) {
                        if (!getDerivedStatus(p).equalsIgnoreCase(status.trim())) {
                            return false;
                        }
                    }
                    if (expiryFrom != null && p.getExpiryDate() != null && p.getExpiryDate().isBefore(expiryFrom)) {
                        return false;
                    }
                    if (expiryTo != null && p.getExpiryDate() != null && p.getExpiryDate().isAfter(expiryTo)) {
                        return false;
                    }
                    return true;
                })
                .toList();
    }

    // =========================================================================
    // Dashboard statistics — FR-12 (BR-06)
    // =========================================================================

    /**
     * Computes dashboard statistics from live database data.
     * All counts are computed at the moment this method is called; they are never
     * cached or pre-computed (implementing BR-06).
     *
     * @return a map containing: total, active, expiringSoon, expired, renewed
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getDashboardStats() {
        LocalDate today = LocalDate.now(clock);
        LocalDate cutoff = today.plusDays(renewalAlertDays);

        Map<String, Long> stats = new HashMap<>();
        stats.put("total",          policyRepository.count());
        stats.put("renewed",        policyRepository.countByRenewalStatus(STATUS_RENEWED));
        stats.put("expired",        policyRepository.countExpired(today));
        stats.put("expiringSoon",   policyRepository.countExpiringSoon(today, cutoff));
        stats.put("expiring7Days",  policyRepository.countExpiringSoon(today, today.plusDays(7)));
        stats.put("expiring30Days", policyRepository.countExpiringSoon(today, today.plusDays(30)));
        stats.put("active",         policyRepository.countActive(cutoff));
        return stats;
    }

    /**
     * Current system evaluation date using the configured clock.
     *
     * @return current evaluation date
     */
    public LocalDate getToday() {
        return LocalDate.now(clock);
    }

    // =========================================================================
    // Alert window accessor (useful for display in templates)
    // =========================================================================

    /**
     * Returns the configured renewal alert window in days.
     * Used by templates to display "Upcoming Renewals (Next N Days)".
     *
     * @return the alert window in days
     */
    public int getRenewalAlertDays() {
        return renewalAlertDays;
    }
}
