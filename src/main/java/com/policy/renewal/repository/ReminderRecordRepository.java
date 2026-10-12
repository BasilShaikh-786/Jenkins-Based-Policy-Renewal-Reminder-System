package com.policy.renewal.repository;

import com.policy.renewal.model.ReminderRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

/**
 * ReminderRecordRepository — data access layer for reminder execution records.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Repository
public interface ReminderRecordRepository extends JpaRepository<ReminderRecord, Long> {

    /**
     * Checks if a reminder record already exists for the specified policy, reminder window,
     * renewal cycle expiry date, and set of matching statuses (e.g. SENT, SIMULATED).
     * Used to prevent sending duplicate notifications for the same cycle.
     *
     * @param policyId         the ID of the policy
     * @param reminderWindow   the reminder window in days (e.g. 30, 7, 1)
     * @param policyExpiryDate the expiry date defining this renewal cycle
     * @param statuses         collection of statuses to match (e.g. SIMULATED, SENT)
     * @return true if an existing reminder matches
     */
    boolean existsByPolicyIdAndReminderWindowAndPolicyExpiryDateAndStatusIn(
            Long policyId,
            int reminderWindow,
            LocalDate policyExpiryDate,
            Collection<String> statuses
    );

    /**
     * Finds all reminder records for a given policy, sorted with most recent first.
     *
     * @param policyId the ID of the policy
     * @return list of reminder records
     */
    List<ReminderRecord> findByPolicyIdOrderByProcessedAtDesc(Long policyId);

    /**
     * Finds all reminder records across the system, ordered newest to oldest.
     *
     * @return list of reminder records
     */
    List<ReminderRecord> findAllByOrderByProcessedAtDesc();

    /**
     * Finds the most recent reminder executions (e.g. top 10 for dashboard preview).
     *
     * @return top 10 recent reminder records
     */
    List<ReminderRecord> findTop10ByOrderByProcessedAtDesc();

    /**
     * Counts the total number of reminders by status.
     *
     * @param status the status to count ("SIMULATED", "SENT", "FAILED")
     * @return count
     */
    long countByStatus(String status);
}
