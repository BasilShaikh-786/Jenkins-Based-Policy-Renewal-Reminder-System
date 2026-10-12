package com.policy.renewal.service;

import com.policy.renewal.model.Policy;
import com.policy.renewal.model.ReminderRecord;
import com.policy.renewal.repository.PolicyRepository;
import com.policy.renewal.repository.ReminderRecordRepository;
import com.policy.renewal.service.notification.NotificationResult;
import com.policy.renewal.service.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * PolicyReminderService — core engine for identifying policies approaching expiry
 * and coordinating renewal reminders.
 *
 * <p>Enforces business rules for renewal notifications:
 * <ul>
 *   <li>Evaluates policies against configurable discrete reminder windows (e.g. 30, 7, 1 day)</li>
 *   <li>Excludes expired policies (expiry date before evaluation date)</li>
 *   <li>Excludes already renewed policies (status RENEWED)</li>
 *   <li>Handles missing/null expiry dates safely without exceptions</li>
 *   <li>Prevents duplicate reminders for the same policy and window within the same renewal cycle</li>
 *   <li>Persists complete reminder history (including failures and simulation records)</li>
 *   <li>Does not mark reminders as successful unless notification dispatch actually succeeds</li>
 * </ul>
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Service
@Transactional
public class PolicyReminderService {

    private static final Logger log = LoggerFactory.getLogger(PolicyReminderService.class);

    private static final List<String> SUCCESSFUL_STATUSES = List.of(
            ReminderRecord.STATUS_SIMULATED,
            ReminderRecord.STATUS_SENT
    );

    private final PolicyRepository policyRepository;
    private final ReminderRecordRepository reminderRecordRepository;
    private final NotificationService notificationService;
    private Clock clock;

    @Value("${reminder.windows:30,7,1}")
    private String reminderWindowsConfig = "30,7,1";

    /**
     * Default constructor for Spring Boot injection.
     */
    @org.springframework.beans.factory.annotation.Autowired
    public PolicyReminderService(PolicyRepository policyRepository,
                                 ReminderRecordRepository reminderRecordRepository,
                                 NotificationService notificationService,
                                 Clock clock) {
        this.policyRepository = policyRepository;
        this.reminderRecordRepository = reminderRecordRepository;
        this.notificationService = notificationService;
        this.clock = clock != null ? clock : Clock.systemDefaultZone();
    }

    /**
     * Overloaded constructor for unit testing without full Spring context.
     */
    public PolicyReminderService(PolicyRepository policyRepository,
                                 ReminderRecordRepository reminderRecordRepository,
                                 NotificationService notificationService) {
        this(policyRepository, reminderRecordRepository, notificationService, Clock.systemDefaultZone());
    }

    public void setClock(Clock clock) {
        if (clock != null) {
            this.clock = clock;
        }
    }

    public void setReminderWindowsConfig(String reminderWindowsConfig) {
        this.reminderWindowsConfig = reminderWindowsConfig;
    }

    /**
     * Parses the configured comma-separated reminder windows into a list of integers.
     *
     * @return list of configured reminder windows in days (e.g., [30, 7, 1])
     */
    public List<Integer> getConfiguredWindows() {
        if (reminderWindowsConfig == null || reminderWindowsConfig.isBlank()) {
            return List.of(30, 7, 1);
        }
        List<Integer> windows = new ArrayList<>();
        for (String part : reminderWindowsConfig.split(",")) {
            try {
                int w = Integer.parseInt(part.trim());
                if (w > 0) {
                    windows.add(w);
                }
            } catch (NumberFormatException ignored) {
                // Ignore malformed entries
            }
        }
        return windows.isEmpty() ? List.of(30, 7, 1) : windows;
    }

    /**
     * Processes all reminder windows for the current system date.
     * Can be invoked by the scheduled job, Jenkins pipeline, or admin trigger.
     *
     * @return execution report summary
     */
    public ReminderRunReport processAllReminders() {
        LocalDate today = LocalDate.now(clock);
        return processAllReminders(today);
    }

    /**
     * Processes all configured reminder windows for a specific evaluation date.
     *
     * @param evaluationDate the date to evaluate policy expiration against
     * @return execution report summary
     */
    public ReminderRunReport processAllReminders(LocalDate evaluationDate) {
        log.info("[REMINDER-ENGINE] Commencing policy reminder evaluation for date: {}", evaluationDate);
        ReminderRunReport report = new ReminderRunReport(evaluationDate);

        List<Integer> windows = getConfiguredWindows();
        for (int windowDays : windows) {
            processWindow(windowDays, evaluationDate, report);
        }

        log.info("[REMINDER-ENGINE] Completed run. Summary: {}", report.getSummary());
        return report;
    }

    /**
     * Processes reminders for a single discrete window.
     *
     * @param windowDays     the window threshold (e.g., 30, 7, 1)
     * @param evaluationDate the evaluation date
     * @param report         the report to append results to
     */
    private void processWindow(int windowDays, LocalDate evaluationDate, ReminderRunReport report) {
        LocalDate targetExpiryDate = evaluationDate.plusDays(windowDays);
        List<Policy> matchingPolicies = policyRepository.findPoliciesExpiringOn(targetExpiryDate);

        log.info("[REMINDER-ENGINE] Evaluating window [{} days] for target expiry [{}]. Candidates found: {}",
                windowDays, targetExpiryDate, matchingPolicies.size());

        for (Policy policy : matchingPolicies) {
            report.incrementTotalChecked();

            // Validate policy eligibility
            if (!isPolicyEligible(policy, evaluationDate)) {
                continue;
            }

            // Check duplicate reminder prevention within this renewal cycle
            boolean alreadyReminded = hasSuccessfulReminderBeenSent(
                    policy.getId(),
                    windowDays,
                    policy.getExpiryDate()
            );

            if (alreadyReminded) {
                log.info("[DUPLICATE-PREVENTED] Reminder already sent for policy {} (window: {} days, cycle: {}). Skipping.",
                        policy.getPolicyNumber(), windowDays, policy.getExpiryDate());
                report.incrementDuplicateSkipped();
                continue;
            }

            // Policy is confirmed eligible
            report.incrementEligible();

            // Dispatch notification (simulation or real)
            NotificationResult result = notificationService.sendRenewalReminder(policy, windowDays);

            // Persist reminder audit record
            ReminderRecord record = new ReminderRecord();
            record.setPolicy(policy);
            record.setPolicyNumber(policy.getPolicyNumber());
            record.setRecipientEmail(policy.getCustomerEmail());
            record.setReminderWindow(windowDays);
            record.setPolicyExpiryDate(policy.getExpiryDate());
            record.setProcessedAt(LocalDateTime.now(clock));
            record.setStatus(result.getStatus());
            record.setChannel(result.getChannel());
            record.setNotes(result.getMessage());
            record.setErrorMessage(result.getErrorMessage());

            ReminderRecord savedRecord = reminderRecordRepository.save(record);
            report.addRecord(savedRecord);
        }
    }

    /**
     * Determines whether a policy is eligible for reminder processing on a given date.
     * Checks for null expiry dates, already expired status, and explicitly renewed status.
     *
     * @param policy         the policy to evaluate
     * @param evaluationDate the evaluation date
     * @return true if policy is eligible
     */
    public boolean isPolicyEligible(Policy policy, LocalDate evaluationDate) {
        if (policy == null) {
            return false;
        }
        // Missing expiry date
        if (policy.getExpiryDate() == null) {
            log.warn("[REMINDER-ELIGIBILITY] Policy {} has no expiry date. Skipping.", policy.getPolicyNumber());
            return false;
        }
        // Already renewed
        if (PolicyService.STATUS_RENEWED.equalsIgnoreCase(policy.getRenewalStatus())) {
            log.debug("[REMINDER-ELIGIBILITY] Policy {} is already RENEWED. Skipping.", policy.getPolicyNumber());
            return false;
        }
        // Already expired
        if (policy.getExpiryDate().isBefore(evaluationDate)) {
            log.debug("[REMINDER-ELIGIBILITY] Policy {} is already EXPIRED (expiry: {} < eval: {}). Skipping.",
                    policy.getPolicyNumber(), policy.getExpiryDate(), evaluationDate);
            return false;
        }
        return true;
    }

    /**
     * Matches a policy to any of the configured discrete reminder windows.
     * Returns the matching window in days if due, or empty if outside configured windows.
     *
     * @param policy         the policy
     * @param evaluationDate the evaluation date
     * @return matching window in days, or Optional.empty()
     */
    public Optional<Integer> determineMatchingWindow(Policy policy, LocalDate evaluationDate) {
        if (!isPolicyEligible(policy, evaluationDate)) {
            return Optional.empty();
        }
        long daysUntilExpiry = ChronoUnit.DAYS.between(evaluationDate, policy.getExpiryDate());
        List<Integer> windows = getConfiguredWindows();
        if (windows.contains((int) daysUntilExpiry)) {
            return Optional.of((int) daysUntilExpiry);
        }
        return Optional.empty();
    }

    /**
     * Checks if a successful reminder was already dispatched for the specified policy,
     * reminder window, and cycle expiry date.
     *
     * @param policyId         the ID of the policy
     * @param reminderWindow   the reminder window threshold
     * @param policyExpiryDate the expiry date defining the renewal cycle
     * @return true if a successful reminder record already exists
     */
    @Transactional(readOnly = true)
    public boolean hasSuccessfulReminderBeenSent(Long policyId, int reminderWindow, LocalDate policyExpiryDate) {
        if (policyId == null || policyExpiryDate == null) {
            return false;
        }
        return reminderRecordRepository.existsByPolicyIdAndReminderWindowAndPolicyExpiryDateAndStatusIn(
                policyId,
                reminderWindow,
                policyExpiryDate,
                SUCCESSFUL_STATUSES
        );
    }

    /**
     * Retrieves all reminder records for a policy.
     *
     * @param policyId the policy ID
     * @return list of reminder records
     */
    @Transactional(readOnly = true)
    public List<ReminderRecord> getReminderHistoryForPolicy(Long policyId) {
        return reminderRecordRepository.findByPolicyIdOrderByProcessedAtDesc(policyId);
    }

    /**
     * Retrieves the top 10 most recent reminder logs across all policies.
     *
     * @return recent reminder records
     */
    @Transactional(readOnly = true)
    public List<ReminderRecord> getRecentReminders() {
        return reminderRecordRepository.findTop10ByOrderByProcessedAtDesc();
    }

    /**
     * Retrieves total counts of reminders by status.
     *
     * @return map of status to count
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getReminderStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", reminderRecordRepository.count());
        stats.put("simulated", reminderRecordRepository.countByStatus(ReminderRecord.STATUS_SIMULATED));
        stats.put("sent", reminderRecordRepository.countByStatus(ReminderRecord.STATUS_SENT));
        stats.put("failed", reminderRecordRepository.countByStatus(ReminderRecord.STATUS_FAILED));
        return stats;
    }
}
