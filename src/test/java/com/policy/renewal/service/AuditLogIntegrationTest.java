package com.policy.renewal.service;

import com.policy.renewal.model.AuditLog;
import com.policy.renewal.model.Policy;
import com.policy.renewal.model.Role;
import com.policy.renewal.model.User;
import com.policy.renewal.repository.AuditLogRepository;
import com.policy.renewal.repository.PolicyRepository;
import com.policy.renewal.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AuditLogIntegrationTest — verifies end-to-end audit logging persistence across policy and user services.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@SpringBootTest
class AuditLogIntegrationTest {

    @Autowired
    private PolicyService policyService;

    @Autowired
    private UserService userService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Saving a new policy automatically records a POLICY_CREATED audit log entry")
    @WithMockUser(username = "auditadmin", roles = {"ADMIN"})
    void testPolicyCreationAuditLog() {
        Policy policy = new Policy();
        policy.setPolicyNumber("POL-AUDIT-01");
        policy.setCustomerName("Audit Test Customer");
        policy.setCustomerEmail("audit@example.com");
        policy.setPolicyType("Health");
        policy.setStartDate(LocalDate.now());
        policy.setExpiryDate(LocalDate.now().plusMonths(6));
        policy.setPremiumAmount(new BigDecimal("12000.00"));

        Policy saved = policyService.save(policy);
        assertNotNull(saved.getId());

        List<AuditLog> logs = auditLogRepository.findByActionOrderByTimestampDesc(AuditLog.ACTION_POLICY_CREATED);
        assertFalse(logs.isEmpty());

        AuditLog log = logs.stream()
                .filter(l -> "POL-AUDIT-01".equals(l.getResourceId()))
                .findFirst()
                .orElse(null);

        assertNotNull(log, "Expected audit log for POL-AUDIT-01 creation");
        assertEquals("auditadmin", log.getPerformedBy());
        assertEquals(AuditLog.RESOURCE_POLICY, log.getResourceType());
        assertEquals(AuditLog.STATUS_SUCCESS, log.getStatus());

        // Cleanup
        policyRepository.delete(saved);
    }

    @Test
    @DisplayName("Updating an existing policy records a POLICY_UPDATED audit log entry")
    @WithMockUser(username = "editoradmin", roles = {"ADMIN"})
    void testPolicyUpdateAuditLog() {
        Policy policy = new Policy();
        policy.setPolicyNumber("POL-AUDIT-02");
        policy.setCustomerName("Before Update Customer");
        policy.setCustomerEmail("before@example.com");
        policy.setPolicyType("Vehicle");
        policy.setStartDate(LocalDate.now());
        policy.setExpiryDate(LocalDate.now().plusYears(1));
        policy.setPremiumAmount(new BigDecimal("5000.00"));
        Policy saved = policyService.save(policy);

        saved.setCustomerName("After Update Customer");
        Policy updated = policyService.save(saved);

        List<AuditLog> updateLogs = auditLogRepository.findByActionOrderByTimestampDesc(AuditLog.ACTION_POLICY_UPDATED);
        assertFalse(updateLogs.isEmpty());

        AuditLog log = updateLogs.stream()
                .filter(l -> "POL-AUDIT-02".equals(l.getResourceId()))
                .findFirst()
                .orElse(null);

        assertNotNull(log, "Expected audit log for POL-AUDIT-02 update");
        assertEquals("editoradmin", log.getPerformedBy());

        // Cleanup
        policyRepository.delete(updated);
    }

    @Test
    @DisplayName("Marking a policy as renewed records a POLICY_RENEWED audit log entry")
    @WithMockUser(username = "renewadmin", roles = {"ADMIN"})
    void testPolicyRenewedAuditLog() {
        Policy policy = new Policy();
        policy.setPolicyNumber("POL-AUDIT-03");
        policy.setCustomerName("Renew Customer");
        policy.setPolicyType("Life");
        policy.setStartDate(LocalDate.now());
        policy.setExpiryDate(LocalDate.now().plusMonths(3));
        policy.setPremiumAmount(new BigDecimal("8000.00"));
        Policy saved = policyService.save(policy);

        policyService.markAsRenewed(saved.getId());

        List<AuditLog> renewLogs = auditLogRepository.findByActionOrderByTimestampDesc(AuditLog.ACTION_POLICY_RENEWED);
        AuditLog log = renewLogs.stream()
                .filter(l -> "POL-AUDIT-03".equals(l.getResourceId()))
                .findFirst()
                .orElse(null);

        assertNotNull(log, "Expected audit log for POL-AUDIT-03 renewal");
        assertEquals("renewadmin", log.getPerformedBy());

        // Cleanup
        policyRepository.delete(saved);
    }

    @Test
    @DisplayName("Deleting a policy records a POLICY_DELETED audit log entry")
    @WithMockUser(username = "deladmin", roles = {"ADMIN"})
    void testPolicyDeletionAuditLog() {
        Policy policy = new Policy();
        policy.setPolicyNumber("POL-AUDIT-04");
        policy.setCustomerName("Delete Customer");
        policy.setPolicyType("Life");
        policy.setStartDate(LocalDate.now());
        policy.setExpiryDate(LocalDate.now().plusMonths(2));
        policy.setPremiumAmount(new BigDecimal("4000.00"));
        Policy saved = policyService.save(policy);

        policyService.deleteById(saved.getId());

        List<AuditLog> delLogs = auditLogRepository.findByActionOrderByTimestampDesc(AuditLog.ACTION_POLICY_DELETED);
        AuditLog log = delLogs.stream()
                .filter(l -> "POL-AUDIT-04".equals(l.getResourceId()))
                .findFirst()
                .orElse(null);

        assertNotNull(log, "Expected audit log for POL-AUDIT-04 deletion");
        assertEquals("deladmin", log.getPerformedBy());
    }

    @Test
    @DisplayName("Registering a user records a USER_REGISTERED audit log entry")
    @WithMockUser(username = "useradmin", roles = {"ADMIN"})
    void testUserRegistrationAuditLog() {
        User user = userService.registerUser(
                "audituser1", "SecurePass@123", "Audit User", "audituser@example.com", Role.STAFF);
        assertNotNull(user);

        List<AuditLog> userLogs = auditLogRepository.findByActionOrderByTimestampDesc(AuditLog.ACTION_USER_REGISTERED);
        AuditLog log = userLogs.stream()
                .filter(l -> "audituser1".equals(l.getResourceId()))
                .findFirst()
                .orElse(null);

        assertNotNull(log, "Expected audit log for audituser1 registration");
        assertEquals("useradmin", log.getPerformedBy());
        assertEquals(AuditLog.RESOURCE_USER, log.getResourceType());

        // Cleanup
        userRepository.delete(user);
    }
}
