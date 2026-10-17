package com.policy.renewal.repository;

import com.policy.renewal.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AuditLogRepository — Spring Data JPA repository for AUDIT_LOG table.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findAllByOrderByTimestampDesc();

    List<AuditLog> findTop100ByOrderByTimestampDesc();

    List<AuditLog> findByResourceTypeOrderByTimestampDesc(String resourceType);

    List<AuditLog> findByActionOrderByTimestampDesc(String action);

    List<AuditLog> findByPerformedByOrderByTimestampDesc(String performedBy);
}
