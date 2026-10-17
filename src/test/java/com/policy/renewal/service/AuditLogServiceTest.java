package com.policy.renewal.service;

import com.policy.renewal.model.AuditLog;
import com.policy.renewal.repository.AuditLogRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * AuditLogServiceTest — unit tests for the centralized system audit logging service.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogService auditLogService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("logSuccess uses authenticated user from SecurityContext")
    void testLogSuccessWithAuthenticatedUser() {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "superadmin", "n/a", List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

        AuditLog log = auditLogService.logSuccess(
                AuditLog.ACTION_POLICY_CREATED,
                AuditLog.RESOURCE_POLICY,
                "POL-9999",
                "Created test policy");

        assertNotNull(log);
        assertEquals("superadmin", log.getPerformedBy());
        assertEquals(AuditLog.ACTION_POLICY_CREATED, log.getAction());
        assertEquals(AuditLog.RESOURCE_POLICY, log.getResourceType());
        assertEquals("POL-9999", log.getResourceId());
        assertEquals(AuditLog.STATUS_SUCCESS, log.getStatus());
        assertEquals("Created test policy", log.getDetails());
    }

    @Test
    @DisplayName("logSuccess defaults performedBy to SYSTEM when SecurityContext is empty")
    void testLogSuccessDefaultsToSystem() {
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

        AuditLog log = auditLogService.logSuccess(
                AuditLog.ACTION_BACKUP_CREATED,
                AuditLog.RESOURCE_DATABASE,
                "backup.zip",
                "Automated backup");

        assertNotNull(log);
        assertEquals("SYSTEM", log.getPerformedBy());
        assertEquals(AuditLog.STATUS_SUCCESS, log.getStatus());
    }

    @Test
    @DisplayName("logFailure records status FAILURE and error details")
    void testLogFailure() {
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

        AuditLog log = auditLogService.logFailure(
                AuditLog.ACTION_BACKUP_CREATED,
                AuditLog.RESOURCE_DATABASE,
                "backup.zip",
                "Disk full error");

        assertNotNull(log);
        assertEquals(AuditLog.STATUS_FAILURE, log.getStatus());
        assertEquals("Disk full error", log.getDetails());
    }

    @Test
    @DisplayName("log safely truncates details string longer than 2000 characters")
    void testLogTruncatesOverlyLongDetails() {
        String hugeDetails = "A".repeat(2500);
        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> inv.getArgument(0));

        AuditLog log = auditLogService.log(
                AuditLog.ACTION_POLICY_UPDATED,
                AuditLog.RESOURCE_POLICY,
                "POL-1001",
                "admin",
                AuditLog.STATUS_SUCCESS,
                hugeDetails);

        assertNotNull(log);
        assertTrue(log.getDetails().length() <= 2000);
        assertTrue(log.getDetails().endsWith("..."));
    }

    @Test
    @DisplayName("log catches exceptions from repository and fails safe without throwing")
    void testLogFailsafeOnException() {
        when(auditLogRepository.save(any(AuditLog.class))).thenThrow(new RuntimeException("DB Connection timeout"));

        assertDoesNotThrow(() -> {
            AuditLog result = auditLogService.logSuccess("TEST", "TEST", "1", "Details");
            assertNull(result);
        });
    }

    @Test
    @DisplayName("query methods delegate cleanly to repository")
    void testQueryMethods() {
        AuditLog sample = new AuditLog("TEST", "TEST", "1", "admin", "SUCCESS", "details");
        when(auditLogRepository.findAllByOrderByTimestampDesc()).thenReturn(List.of(sample));
        when(auditLogRepository.findTop100ByOrderByTimestampDesc()).thenReturn(List.of(sample));
        when(auditLogRepository.findByResourceTypeOrderByTimestampDesc("POLICY")).thenReturn(List.of(sample));
        when(auditLogRepository.findByActionOrderByTimestampDesc("POLICY_CREATED")).thenReturn(List.of(sample));

        assertEquals(1, auditLogService.getAllAuditLogs().size());
        assertEquals(1, auditLogService.getRecentAuditLogs().size());
        assertEquals(1, auditLogService.getLogsByResourceType("policy").size());
        assertEquals(1, auditLogService.getLogsByAction("policy_created").size());
    }
}
