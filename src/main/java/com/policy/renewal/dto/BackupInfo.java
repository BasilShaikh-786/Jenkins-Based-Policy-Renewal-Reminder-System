package com.policy.renewal.dto;

import java.time.LocalDateTime;

/**
 * BackupInfo — metadata transfer object describing a database backup archive.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
public class BackupInfo {

    private String fileName;
    private String filePath;
    private long sizeBytes;
    private String formattedSize;
    private LocalDateTime createdAt;
    private String backupType; // ZIP, SQL

    public BackupInfo() {
    }

    public BackupInfo(String fileName, String filePath, long sizeBytes, LocalDateTime createdAt, String backupType) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.sizeBytes = sizeBytes;
        this.createdAt = createdAt;
        this.backupType = backupType;
        this.formattedSize = formatBytes(sizeBytes);
    }

    public static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char unit = "KMGTPE".charAt(exp - 1);
        return String.format("%.2f %sB", bytes / Math.pow(1024, exp), unit);
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(long sizeBytes) {
        this.sizeBytes = sizeBytes;
        this.formattedSize = formatBytes(sizeBytes);
    }

    public String getFormattedSize() {
        return formattedSize;
    }

    public void setFormattedSize(String formattedSize) {
        this.formattedSize = formattedSize;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getBackupType() {
        return backupType;
    }

    public void setBackupType(String backupType) {
        this.backupType = backupType;
    }
}
