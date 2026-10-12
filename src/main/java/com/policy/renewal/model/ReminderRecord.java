package com.policy.renewal.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ReminderRecord — JPA entity representing an execution log of a policy renewal reminder.
 *
 * <p>Persists audit history for each reminder attempt. Tracks the policy reference,
 * the targeted reminder window (e.g., 30, 7, 1 day), the policy's expiry date (to scope
 * the reminder to the current renewal cycle), execution timestamp, delivery status,
 * notification channel, and any failure information.
 *
 * <p>Duplicate prevention rule:
 * For a given policy, reminder window, and policy expiry date, no duplicate successful
 * reminder is triggered within the same renewal cycle.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Entity
@Table(name = "REMINDER_RECORD")
public class ReminderRecord {

    public static final String STATUS_SIMULATED = "SIMULATED";
    public static final String STATUS_SENT      = "SENT";
    public static final String STATUS_FAILED    = "FAILED";

    public static final String CHANNEL_SIMULATION = "EMAIL_SIMULATION";
    public static final String CHANNEL_EMAIL      = "EMAIL";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "POLICY_ID", nullable = false)
    private Policy policy;

    @Column(name = "POLICY_NUMBER", nullable = false, length = 50)
    private String policyNumber;

    @Column(name = "RECIPIENT_EMAIL", length = 200)
    private String recipientEmail;

    /**
     * The reminder window threshold in days (e.g. 30, 7, or 1 day before expiration).
     */
    @Column(name = "REMINDER_WINDOW", nullable = false)
    private int reminderWindow;

    /**
     * The target policy's expiration date at the time the reminder was generated.
     * This establishes the renewal cycle identifier so renewed policies in future cycles
     * can receive fresh reminders.
     */
    @Column(name = "POLICY_EXPIRY_DATE", nullable = false)
    private LocalDate policyExpiryDate;

    @Column(name = "PROCESSED_AT", nullable = false)
    private LocalDateTime processedAt;

    /**
     * Outcome of the reminder operation: "SIMULATED", "SENT", or "FAILED".
     */
    @Column(name = "STATUS", nullable = false, length = 30)
    private String status;

    @Column(name = "CHANNEL", nullable = false, length = 50)
    private String channel;

    @Column(name = "ERROR_MESSAGE", length = 1000)
    private String errorMessage;

    @Column(name = "NOTES", length = 1000)
    private String notes;

    @PrePersist
    protected void onCreate() {
        if (this.processedAt == null) {
            this.processedAt = LocalDateTime.now();
        }
    }

    public ReminderRecord() {
    }

    public ReminderRecord(Policy policy, int reminderWindow, LocalDate policyExpiryDate,
                          String status, String channel, String notes) {
        this.policy = policy;
        this.policyNumber = policy != null ? policy.getPolicyNumber() : null;
        this.recipientEmail = policy != null ? policy.getCustomerEmail() : null;
        this.reminderWindow = reminderWindow;
        this.policyExpiryDate = policyExpiryDate;
        this.status = status;
        this.channel = channel;
        this.notes = notes;
        this.processedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
        if (policy != null) {
            this.policyNumber = policy.getPolicyNumber();
            this.recipientEmail = policy.getCustomerEmail();
        }
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public int getReminderWindow() {
        return reminderWindow;
    }

    public void setReminderWindow(int reminderWindow) {
        this.reminderWindow = reminderWindow;
    }

    public LocalDate getPolicyExpiryDate() {
        return policyExpiryDate;
    }

    public void setPolicyExpiryDate(LocalDate policyExpiryDate) {
        this.policyExpiryDate = policyExpiryDate;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "ReminderRecord{" +
                "id=" + id +
                ", policyNumber='" + policyNumber + '\'' +
                ", reminderWindow=" + reminderWindow +
                ", policyExpiryDate=" + policyExpiryDate +
                ", status='" + status + '\'' +
                ", channel='" + channel + '\'' +
                ", processedAt=" + processedAt +
                '}';
    }
}
