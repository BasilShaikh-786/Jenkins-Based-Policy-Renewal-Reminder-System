package com.policy.renewal.dto;

import java.time.LocalDateTime;

/**
 * BackupVerificationResult — DTO containing the health and integrity report of a verified backup.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
public class BackupVerificationResult {

    private String fileName;
    private boolean valid;
    private long policyCount;
    private long userCount;
    private long reminderRecordCount;
    private long auditLogCount;
    private LocalDateTime verifiedAt;
    private String message;
    private String errorMessage;

    public BackupVerificationResult() {
        this.verifiedAt = LocalDateTime.now();
    }

    public static BackupVerificationResult success(String fileName, long policyCount, long userCount,
                                                   long reminderRecordCount, long auditLogCount, String message) {
        BackupVerificationResult res = new BackupVerificationResult();
        res.setFileName(fileName);
        res.setValid(true);
        res.setPolicyCount(policyCount);
        res.setUserCount(userCount);
        res.setReminderRecordCount(reminderRecordCount);
        res.setAuditLogCount(auditLogCount);
        res.setMessage(message);
        return res;
    }

    public static BackupVerificationResult failure(String fileName, String errorMessage) {
        BackupVerificationResult res = new BackupVerificationResult();
        res.setFileName(fileName);
        res.setValid(false);
        res.setErrorMessage(errorMessage);
        res.setMessage("Verification failed: " + errorMessage);
        return res;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public long getPolicyCount() {
        return policyCount;
    }

    public void setPolicyCount(long policyCount) {
        this.policyCount = policyCount;
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public long getReminderRecordCount() {
        return reminderRecordCount;
    }

    public void setReminderRecordCount(long reminderRecordCount) {
        this.reminderRecordCount = reminderRecordCount;
    }

    public long getAuditLogCount() {
        return auditLogCount;
    }

    public void setAuditLogCount(long auditLogCount) {
        this.auditLogCount = auditLogCount;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
