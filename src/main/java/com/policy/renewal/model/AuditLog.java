package com.policy.renewal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * AuditLog — JPA entity recording system audit events for Milestone 5.
 *
 * <p>Persists an immutable audit trail of administrative operations, policy lifecycle changes,
 * user management activities, backup jobs, and security events.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Entity
@Table(name = "AUDIT_LOG", indexes = {
        @Index(name = "idx_audit_timestamp", columnList = "TIMESTAMP"),
        @Index(name = "idx_audit_resource", columnList = "RESOURCE_TYPE, RESOURCE_ID"),
        @Index(name = "idx_audit_action", columnList = "ACTION")
})
public class AuditLog {

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILURE = "FAILURE";

    public static final String RESOURCE_POLICY   = "POLICY";
    public static final String RESOURCE_USER     = "USER";
    public static final String RESOURCE_DATABASE = "DATABASE";
    public static final String RESOURCE_REMINDER = "REMINDER";
    public static final String RESOURCE_SECURITY = "SECURITY";

    public static final String ACTION_POLICY_CREATED      = "POLICY_CREATED";
    public static final String ACTION_POLICY_UPDATED      = "POLICY_UPDATED";
    public static final String ACTION_POLICY_DELETED      = "POLICY_DELETED";
    public static final String ACTION_POLICY_RENEWED      = "POLICY_RENEWED";
    public static final String ACTION_USER_REGISTERED     = "USER_REGISTERED";
    public static final String ACTION_ADMIN_PASSWORD_RESET = "ADMIN_PASSWORD_RESET";
    public static final String ACTION_BACKUP_CREATED      = "BACKUP_CREATED";
    public static final String ACTION_BACKUP_VERIFIED     = "BACKUP_VERIFIED";
    public static final String ACTION_BACKUP_RESTORED     = "BACKUP_RESTORED";
    public static final String ACTION_REMINDERS_TRIGGERED  = "REMINDERS_TRIGGERED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TIMESTAMP", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "ACTION", nullable = false, length = 50)
    private String action;

    @Column(name = "RESOURCE_TYPE", nullable = false, length = 50)
    private String resourceType;

    @Column(name = "RESOURCE_ID", length = 100)
    private String resourceId;

    @Column(name = "PERFORMED_BY", nullable = false, length = 100)
    private String performedBy;

    @Column(name = "STATUS", nullable = false, length = 20)
    private String status;

    @Column(name = "DETAILS", length = 2000)
    private String details;

    @PrePersist
    protected void onCreate() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
    }

    public AuditLog() {
    }

    public AuditLog(String action, String resourceType, String resourceId,
                    String performedBy, String status, String details) {
        this.action = action;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.performedBy = performedBy != null && !performedBy.isBlank() ? performedBy : "SYSTEM";
        this.status = status != null && !status.isBlank() ? status : STATUS_SUCCESS;
        this.details = details != null && details.length() > 2000 ? details.substring(0, 1997) + "..." : details;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details != null && details.length() > 2000 ? details.substring(0, 1997) + "..." : details;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", action='" + action + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", performedBy='" + performedBy + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
