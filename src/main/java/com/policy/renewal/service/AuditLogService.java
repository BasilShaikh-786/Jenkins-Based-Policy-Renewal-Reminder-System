package com.policy.renewal.service;

import com.policy.renewal.model.AuditLog;
import com.policy.renewal.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * AuditLogService — manages system audit logging operations for Milestone 5.
 *
 * <p>Guarantees:
 * <ul>
 *   <li>Persists an immutable audit trail of actions to the {@code AUDIT_LOG} database table.</li>
 *   <li>Automatically identifies the active authenticated user from {@link SecurityContextHolder}.</li>
 *   <li>Falls back gracefully to "SYSTEM" when executed from background tasks, CLI, or test runners.</li>
 *   <li>Uses {@link Propagation#REQUIRES_NEW} where appropriate so that audit logs are saved independently.</li>
 *   <li>Fails safe: logging exceptions are caught and recorded to application logs without failing the business operation.</li>
 * </ul>
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    /**
     * Records a new audit log event.
     *
     * @param action       action name (e.g., POLICY_CREATED, BACKUP_CREATED)
     * @param resourceType category (e.g., POLICY, USER, DATABASE)
     * @param resourceId   identifier of the affected resource (e.g., policy number, username)
     * @param performedBy  username or principal that triggered the action; if blank, current principal is used
     * @param status       SUCCESS or FAILURE
     * @param details      description or metadata of the action
     * @return the saved {@link AuditLog}, or {@code null} if an unhandled logging error occurred
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditLog log(String action, String resourceType, String resourceId,
                        String performedBy, String status, String details) {
        try {
            String actor = (performedBy != null && !performedBy.isBlank()) ? performedBy : getCurrentPrincipal();
            AuditLog entry = new AuditLog(action, resourceType, resourceId, actor, status, details);
            AuditLog saved = auditLogRepository.save(entry);
            log.info("[AUDIT] [{}] [{}] [{}] Actor: {} | Status: {} | Details: {}",
                    action, resourceType, resourceId, actor, status, details);
            return saved;
        } catch (Exception e) {
            log.error("[AUDIT-ERROR] Failed to persist audit log entry for action '{}': {}", action, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Convenience method to record a successful action with automatic principal resolution.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditLog logSuccess(String action, String resourceType, String resourceId, String details) {
        return log(action, resourceType, resourceId, getCurrentPrincipal(), AuditLog.STATUS_SUCCESS, details);
    }

    /**
     * Convenience method to record a failed action with automatic principal resolution.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditLog logFailure(String action, String resourceType, String resourceId, String errorDetails) {
        return log(action, resourceType, resourceId, getCurrentPrincipal(), AuditLog.STATUS_FAILURE, errorDetails);
    }

    /**
     * Retrieves all audit logs ordered by timestamp descending.
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }

    /**
     * Retrieves the most recent 100 audit logs.
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getRecentAuditLogs() {
        return auditLogRepository.findTop100ByOrderByTimestampDesc();
    }

    /**
     * Retrieves audit logs for a specific resource type.
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getLogsByResourceType(String resourceType) {
        if (resourceType == null || resourceType.isBlank()) {
            return Collections.emptyList();
        }
        return auditLogRepository.findByResourceTypeOrderByTimestampDesc(resourceType.trim().toUpperCase());
    }

    /**
     * Retrieves audit logs for a specific action.
     */
    @Transactional(readOnly = true)
    public List<AuditLog> getLogsByAction(String action) {
        if (action == null || action.isBlank()) {
            return Collections.emptyList();
        }
        return auditLogRepository.findByActionOrderByTimestampDesc(action.trim().toUpperCase());
    }

    /**
     * Resolves the current authenticated user's name from Spring Security.
     *
     * @return username of the active user, or "SYSTEM" if unauthenticated/background
     */
    public String getCurrentPrincipal() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
                String name = auth.getName();
                if (name != null && !name.isBlank()) {
                    return name;
                }
            }
        } catch (Exception ignored) {
        }
        return "SYSTEM";
    }
}
