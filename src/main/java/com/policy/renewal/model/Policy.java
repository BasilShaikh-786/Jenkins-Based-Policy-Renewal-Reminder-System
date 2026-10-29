package com.policy.renewal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Policy — JPA entity representing a single insurance policy record.
 *
 * <p>This class maps to the POLICY table in the H2 database. Each field corresponds
 * to a column in that table. Spring Data JPA (via Hibernate) automatically creates
 * the table when the application starts.
 *
 * <p>Design note on renewalStatus:
 * Only the value "RENEWED" is stored explicitly. All other statuses (ACTIVE,
 * EXPIRING_SOON, EXPIRED) are derived at runtime by the service layer based on
 * the expiryDate and today's date. This prevents stale status data.
 * See: docs/week-03/database-design.md, Business Rule BR-03.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Entity
@Table(name = "POLICY")
public class Policy {

    // =========================================================================
    // Primary Key
    // =========================================================================

    /**
     * Auto-generated numeric primary key. The database assigns this value;
     * it is never set by application code or the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    // =========================================================================
    // Business Key
    // =========================================================================

    /**
     * Unique policy identifier assigned by the insurance office (e.g., "POL-001").
     * This is the "business key" — meaningful to users, unlike the numeric ID.
     * Must be unique across all records (enforced at database and service level).
     */
    @NotBlank(message = "Policy number is required.")
    @Size(max = 50, message = "Policy number must not exceed 50 characters.")
    @Column(name = "POLICY_NUMBER", nullable = false, unique = true, length = 50)
    private String policyNumber;

    // =========================================================================
    // Customer Information
    // =========================================================================

    /**
     * Full name of the insurance policyholder (required).
     */
    @NotBlank(message = "Customer name is required.")
    @Size(max = 150, message = "Customer name must not exceed 150 characters.")
    @Column(name = "CUSTOMER_NAME", nullable = false, length = 150)
    private String customerName;

    /**
     * Email address of the policyholder (optional). Validated for format
     * if provided. Reserved for future notification feature.
     */
    @Email(message = "Please enter a valid email address.")
    @Size(max = 200, message = "Email address must not exceed 200 characters.")
    @Column(name = "CUSTOMER_EMAIL", length = 200)
    private String customerEmail;

    // =========================================================================
    // Policy Information
    // =========================================================================

    /**
     * Type of insurance policy. One of: Health, Life, Vehicle, Home, Travel.
     * Enforced by application logic; stored as a plain string for flexibility.
     */
    @NotBlank(message = "Policy type is required.")
    @Size(max = 50, message = "Policy type must not exceed 50 characters.")
    @Column(name = "POLICY_TYPE", nullable = false, length = 50)
    private String policyType;

    /**
     * Date on which the policy coverage begins (required).
     */
    @NotNull(message = "Start date is required.")
    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;

    /**
     * Date on which the policy coverage ends (required).
     * Must not be before the startDate — enforced in the service layer.
     */
    @NotNull(message = "Expiry date is required.")
    @Column(name = "EXPIRY_DATE", nullable = false)
    private LocalDate expiryDate;

    /**
     * Annual premium amount in the local currency (required).
     * Must be zero or greater. Stored as BigDecimal to avoid floating-point errors.
     * Precision 10, scale 2 allows values up to 99,999,999.99.
     */
    @NotNull(message = "Premium amount is required.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Premium amount must be zero or greater.")
    @Column(name = "PREMIUM_AMOUNT", nullable = false, precision = 10, scale = 2)
    private BigDecimal premiumAmount;

    // =========================================================================
    // Requirement 1.A: Customer Relationship Period (Historical Policy Tenure)
    // =========================================================================

    /**
     * Inception date representing when the customer originally established their
     * relationship with the insurer/policy. If null, defaults to the policy startDate.
     *
     * <p>CRITICAL INVARIANT (Requirement 1.A):
     * This field is purely historical. It MUST NOT be used to calculate the amount
     * the customer needs to pay for a new policy.
     */
    @Column(name = "CUSTOMER_SINCE_DATE")
    private LocalDate customerSinceDate;

    // =========================================================================
    // Requirement 1.B: Insurance Coverage / Purchase Period
    // =========================================================================

    /**
     * Duration value requested by the customer or administrator for this policy coverage.
     * Example: 1, 3, 6, 12, 24, 36.
     */
    @Positive(message = "Coverage duration must be a positive number.")
    @Column(name = "COVERAGE_DURATION_VALUE")
    private Integer coverageDurationValue;

    /**
     * Duration unit for the requested coverage period.
     * Supported units: "MONTHS", "YEARS", "DAYS". Default: "MONTHS".
     */
    @Column(name = "COVERAGE_DURATION_UNIT", length = 20)
    private String coverageDurationUnit = "MONTHS";

    // =========================================================================
    // Insurance Pricing Breakdown & Metadata
    // =========================================================================

    /**
     * Sum insured / coverage amount associated with this policy (e.g. ₹5,00,000.00).
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "Sum insured must be zero or positive.")
    @Column(name = "SUM_INSURED", precision = 12, scale = 2)
    private BigDecimal sumInsured;

    /**
     * Specific insurance product name (e.g. "Optima Secure", "Saral Jeevan Bima").
     */
    @Column(name = "PRODUCT_NAME", length = 150)
    private String productName;

    /**
     * Insurer providing the product (e.g. "HDFC ERGO General Insurance", "LIC").
     */
    @Column(name = "REFERENCE_INSURER", length = 150)
    private String referenceInsurer;

    /**
     * Pricing calculation basis (e.g. "ANNUAL", "DAILY_RATE", "SINGLE_TRIP").
     */
    @Column(name = "PRICING_BASIS", length = 50)
    private String pricingBasis = "ANNUAL";

    /**
     * Base net premium before statutory taxes (GST).
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "Base premium must be zero or positive.")
    @Column(name = "BASE_PREMIUM", precision = 10, scale = 2)
    private BigDecimal basePremium;

    /**
     * Applicable statutory tax amount (e.g. 18% GST).
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "Tax amount must be zero or positive.")
    @Column(name = "TAX_AMOUNT", precision = 10, scale = 2)
    private BigDecimal taxAmount;

    /**
     * Pricing status: VERIFIED_QUOTE, PUBLISHED_REFERENCE, or ADMIN_ESTIMATE.
     */
    @Column(name = "PRICE_STATUS", length = 50)
    private String priceStatus = "PUBLISHED_REFERENCE";

    /**
     * Official source URL supporting the pricing rule.
     */
    @Column(name = "PRICING_SOURCE_URL", length = 500)
    private String pricingSourceUrl;

    /**
     * Documented underwriting and customer assumptions.
     */
    @Column(name = "PRICING_ASSUMPTIONS", length = 500)
    private String pricingAssumptions;

    // =========================================================================
    // Renewal Status
    // =========================================================================

    /**
     * Stored renewal status. Only "RENEWED" is stored here; all other statuses
     * (ACTIVE, EXPIRING_SOON, EXPIRED) are derived at runtime by PolicyService.
     * This field is null for policies that have not been explicitly renewed.
     */
    @Column(name = "RENEWAL_STATUS", length = 20)
    private String renewalStatus;

    // =========================================================================
    // Optional and Audit Fields
    // =========================================================================

    /**
     * Free-text notes about this policy. Written by the agent. Optional.
     */
    @Size(max = 500, message = "Notes must not exceed 500 characters.")
    @Column(name = "NOTES", length = 500)
    private String notes;

    /**
     * Timestamp recording when this policy record was first created.
     * Set automatically by the @PrePersist lifecycle callback.
     * Not editable by the user.
     */
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp recording the most recent time this policy record was updated.
     * Updated automatically by the @PreUpdate lifecycle callback.
     */
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    // =========================================================================
    // JPA Lifecycle Callbacks
    // =========================================================================

    /**
     * Called automatically by JPA before the entity is saved for the first time.
     * Sets both createdAt and updatedAt to the current timestamp.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Called automatically by JPA before the entity is updated.
     * Updates the updatedAt timestamp to reflect the current modification time.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // =========================================================================
    // Constructors
    // =========================================================================

    /** Default no-argument constructor required by JPA. */
    public Policy() {
    }

    // =========================================================================
    // Getters and Setters
    // =========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public BigDecimal getPremiumAmount() {
        return premiumAmount;
    }

    public void setPremiumAmount(BigDecimal premiumAmount) {
        this.premiumAmount = premiumAmount;
    }

    public String getRenewalStatus() {
        return renewalStatus;
    }

    public void setRenewalStatus(String renewalStatus) {
        this.renewalStatus = renewalStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Getters and Setters for Requirement 1 & Requirement 2

    public LocalDate getCustomerSinceDate() {
        return customerSinceDate;
    }

    public void setCustomerSinceDate(LocalDate customerSinceDate) {
        this.customerSinceDate = customerSinceDate;
    }

    public LocalDate getEffectiveCustomerSinceDate() {
        if (customerSinceDate != null) {
            return customerSinceDate;
        }
        if ("RENEWED".equals(renewalStatus)) {
            return null; // For renewed historical records where original inception date was not explicitly recorded
        }
        return startDate;
    }

    public Integer getCoverageDurationValue() {
        return coverageDurationValue;
    }

    public void setCoverageDurationValue(Integer coverageDurationValue) {
        this.coverageDurationValue = coverageDurationValue;
    }

    public String getCoverageDurationUnit() {
        return coverageDurationUnit;
    }

    public void setCoverageDurationUnit(String coverageDurationUnit) {
        this.coverageDurationUnit = coverageDurationUnit;
    }

    /**
     * Indicates whether the displayed coverage duration is derived from term dates
     * rather than explicitly stored in the database.
     *
     * @return true if coverage duration is derived
     */
    public boolean isCoverageDurationDerived() {
        return (coverageDurationValue == null || coverageDurationValue <= 0) && startDate != null && expiryDate != null;
    }

    /**
     * Calculates the actual elapsed coverage period in full months between current term start and expiry.
     *
     * @return elapsed months (0 or positive)
     */
    public long getActualCoverageMonths() {
        if (startDate == null || expiryDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.MONTHS.between(startDate, expiryDate.plusDays(1));
    }

    /**
     * Calculates the actual elapsed coverage period in days between current term start and expiry.
     *
     * @return elapsed days (0 or positive)
     */
    public long getActualCoverageDays() {
        if (startDate == null || expiryDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, expiryDate) + 1;
    }

    /**
     * Checks whether the current term dates (startDate, expiryDate) mathematically match
     * the requested coverage duration value and unit.
     *
     * @return true if dates and duration are consistent within tolerance
     */
    public boolean isCoveragePeriodConsistent() {
        if (startDate == null || expiryDate == null) {
            return false;
        }
        if (coverageDurationValue == null || coverageDurationValue <= 0) {
            return true;
        }
        String unit = (coverageDurationUnit != null ? coverageDurationUnit.trim().toUpperCase() : "MONTHS");
        if (unit.startsWith("DAY")) {
            long daysDiff = Math.abs(getActualCoverageDays() - coverageDurationValue);
            return daysDiff <= 3;
        }
        LocalDate expectedEnd;
        if (unit.startsWith("YEAR")) {
            expectedEnd = startDate.plusYears(coverageDurationValue).minusDays(1);
        } else {
            expectedEnd = startDate.plusMonths(coverageDurationValue).minusDays(1);
        }

        long diffDays = Math.abs(java.time.temporal.ChronoUnit.DAYS.between(expectedEnd, expiryDate));
        long diffAnniversary = Math.abs(java.time.temporal.ChronoUnit.DAYS.between(expectedEnd.plusDays(1), expiryDate));
        return diffDays <= 3 || diffAnniversary <= 3;
    }

    /**
     * Flags historical or inconsistent records for administrative review (Requirement 9).
     *
     * @return true if review is required
     */
    public boolean isHistoricalTermReviewRequired() {
        if (startDate == null || expiryDate == null) {
            return true;
        }
        if (coverageDurationValue != null && coverageDurationValue > 0) {
            return !isCoveragePeriodConsistent();
        }
        // If coverageDurationValue was not recorded:
        // A renewed policy spanning multiple terms (> 400 days, e.g. 22-month span like POL-1122)
        // genuinely conflates original inception with a renewal term
        if ("RENEWED".equals(renewalStatus) && getActualCoverageDays() > 400) {
            return true;
        }
        return false;
    }

    /**
     * Detailed explanation of why historical review is required for this record.
     *
     * @return human-readable explanation
     */
    public String getCoveragePeriodReviewReason() {
        if (startDate == null || expiryDate == null) {
            return "Start date or expiry date is not recorded.";
        }
        long months = getActualCoverageMonths();
        long days = getActualCoverageDays();
        if (coverageDurationValue != null && coverageDurationValue > 0 && !isCoveragePeriodConsistent()) {
            return "Current coverage term dates span " + months + " months (" + days + " days), which contradicts the requested duration of "
                    + coverageDurationValue + " " + (coverageDurationUnit != null ? coverageDurationUnit : "MONTHS") + ".";
        }
        if ("RENEWED".equals(renewalStatus) && days > 400) {
            return "Current coverage term dates span " + months + " months (" + days + " days), but requested duration was not recorded. This policy has renewal history. The recorded Start Date ("
                    + startDate + ") reflects the original policy inception, while the Expiry Date (" + expiryDate
                    + ") reflects a subsequent renewal term. Review required: separate the current coverage term start date from the original customer inception date.";
        }
        return null;
    }

    /**
     * User-facing formatted duration label that never displays misleading default values
     * and clearly distinguishes derived durations from explicitly requested values.
     *
     * @return formatted duration label
     */
    public String getFormattedCoverageDuration() {
        if (coverageDurationValue != null && coverageDurationValue > 0) {
            String unit = (coverageDurationUnit != null && !coverageDurationUnit.isBlank())
                    ? coverageDurationUnit.trim().toUpperCase() : "MONTHS";
            return coverageDurationValue + " " + unit;
        }
        if (startDate == null || expiryDate == null) {
            return "Not Recorded";
        }
        long totalDays = getActualCoverageDays();
        if (totalDays <= 0) {
            return "Invalid Dates";
        }
        if (totalDays < 30) {
            return totalDays + " DAYS (Derived from Term Dates)";
        }
        long months = getActualCoverageMonths();
        if (months >= 12 && (totalDays % 365 <= 3 || totalDays % 365 >= 362)) {
            long years = (totalDays + 15) / 365;
            return years + (years == 1 ? " YEAR" : " YEARS") + " (Derived from Term Dates)";
        }
        if (months > 0) {
            return months + " MONTHS (Derived from Term Dates)";
        }
        return totalDays + " DAYS (Derived from Term Dates)";
    }

    public BigDecimal getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(BigDecimal sumInsured) {
        this.sumInsured = sumInsured;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getReferenceInsurer() {
        return referenceInsurer;
    }

    public void setReferenceInsurer(String referenceInsurer) {
        this.referenceInsurer = referenceInsurer;
    }

    public String getPricingBasis() {
        return pricingBasis;
    }

    public void setPricingBasis(String pricingBasis) {
        this.pricingBasis = pricingBasis;
    }

    public BigDecimal getBasePremium() {
        return basePremium;
    }

    public void setBasePremium(BigDecimal basePremium) {
        this.basePremium = basePremium;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getPriceStatus() {
        return priceStatus;
    }

    public void setPriceStatus(String priceStatus) {
        this.priceStatus = priceStatus;
    }

    public String getPricingSourceUrl() {
        return pricingSourceUrl;
    }

    public void setPricingSourceUrl(String pricingSourceUrl) {
        this.pricingSourceUrl = pricingSourceUrl;
    }

    public String getPricingAssumptions() {
        return pricingAssumptions;
    }

    public void setPricingAssumptions(String pricingAssumptions) {
        this.pricingAssumptions = pricingAssumptions;
    }

    // =========================================================================
    // toString (for debugging; does not expose sensitive data)
    // =========================================================================

    @Override
    public String toString() {
        return "Policy{" +
                "id=" + id +
                ", policyNumber='" + policyNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", policyType='" + policyType + '\'' +
                ", expiryDate=" + expiryDate +
                ", renewalStatus='" + renewalStatus + '\'' +
                '}';
    }
}
