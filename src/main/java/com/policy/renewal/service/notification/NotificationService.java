package com.policy.renewal.service.notification;

import com.policy.renewal.model.Policy;

/**
 * NotificationService — interface abstraction for sending policy renewal reminders.
 *
 * <p>Decouples reminder engine scheduling and business logic from the underlying
 * notification delivery mechanism (simulation vs SMTP/email).
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
public interface NotificationService {

    /**
     * Dispatches or simulates a renewal reminder notification for the given policy
     * and reminder threshold window.
     *
     * @param policy             the policy requiring reminder
     * @param reminderWindowDays the reminder window threshold in days (e.g. 30, 7, 1)
     * @return the result of the notification dispatch attempt
     */
    NotificationResult sendRenewalReminder(Policy policy, int reminderWindowDays);

    /**
     * Indicates whether notifications are running in safe dry-run/simulation mode.
     *
     * @return true if simulation mode is active
     */
    boolean isSimulationMode();
}
