package com.policy.renewal.service;

import com.policy.renewal.model.ReminderRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ReminderRunReport — execution summary report for a policy reminder processing cycle.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
public class ReminderRunReport {

    private final LocalDate evaluationDate;
    private int totalChecked;
    private int eligibleCount;
    private int simulatedCount;
    private int sentCount;
    private int failedCount;
    private int duplicateSkippedCount;
    private final List<ReminderRecord> records = new ArrayList<>();

    public ReminderRunReport(LocalDate evaluationDate) {
        this.evaluationDate = evaluationDate;
    }

    public void incrementTotalChecked() {
        this.totalChecked++;
    }

    public void incrementEligible() {
        this.eligibleCount++;
    }

    public void incrementDuplicateSkipped() {
        this.duplicateSkippedCount++;
    }

    public void addRecord(ReminderRecord record) {
        this.records.add(record);
        if (ReminderRecord.STATUS_SIMULATED.equals(record.getStatus())) {
            this.simulatedCount++;
        } else if (ReminderRecord.STATUS_SENT.equals(record.getStatus())) {
            this.sentCount++;
        } else if (ReminderRecord.STATUS_FAILED.equals(record.getStatus())) {
            this.failedCount++;
        }
    }

    public LocalDate getEvaluationDate() {
        return evaluationDate;
    }

    public int getTotalChecked() {
        return totalChecked;
    }

    public int getEligibleCount() {
        return eligibleCount;
    }

    public int getSimulatedCount() {
        return simulatedCount;
    }

    public int getSentCount() {
        return sentCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public int getDuplicateSkippedCount() {
        return duplicateSkippedCount;
    }

    public int getProcessedCount() {
        return simulatedCount + sentCount + failedCount;
    }

    public List<ReminderRecord> getRecords() {
        return Collections.unmodifiableList(records);
    }

    public String getSummary() {
        return String.format(
                "Evaluation Date: %s | Eligible: %d | Simulated: %d | Sent: %d | Failed: %d | Skipped (Duplicate): %d",
                evaluationDate, eligibleCount, simulatedCount, sentCount, failedCount, duplicateSkippedCount
        );
    }

    @Override
    public String toString() {
        return "ReminderRunReport{" + getSummary() + '}';
    }
}
