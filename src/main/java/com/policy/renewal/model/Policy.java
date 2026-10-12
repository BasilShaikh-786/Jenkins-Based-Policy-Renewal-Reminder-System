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
