package com.policy.renewal.service;

import com.policy.renewal.model.Policy;
import com.policy.renewal.model.ReminderRecord;
import com.policy.renewal.repository.PolicyRepository;
import com.policy.renewal.repository.ReminderRecordRepository;
import com.policy.renewal.service.notification.NotificationResult;
import com.policy.renewal.service.notification.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * PolicyReminderServiceTest — unit tests verifying all expiry reminder engine business rules.
 *
 * <p>Uses a fixed Clock (evaluation date: 2026-10-15) for deterministic date evaluation.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@ExtendWith(MockitoExtension.class)
class PolicyReminderServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private ReminderRecordRepository reminderRecordRepository;

    @Mock
    private NotificationService notificationService;

    private PolicyReminderService reminderService;

    // Fixed test evaluation date: 2026-10-15
    private final LocalDate evaluationDate = LocalDate.of(2026, 10, 15);
    private final ZoneId zoneId = ZoneId.systemDefault();
    private final Clock fixedClock = Clock.fixed(
            evaluationDate.atStartOfDay(zoneId).toInstant(),
            zoneId
    );

    @BeforeEach
    void setUp() {
        reminderService = new PolicyReminderService(
                policyRepository,
                reminderRecordRepository,
                notificationService,
                fixedClock
        );
        reminderService.setReminderWindowsConfig("30,7,1");
    }

    private Policy createTestPolicy(Long id, String number, LocalDate expiry, String renewalStatus) {
        Policy policy = new Policy();
        policy.setId(id);
        policy.setPolicyNumber(number);
        policy.setCustomerName("Customer " + number);
        policy.setCustomerEmail("client." + number.toLowerCase() + "@example.com");
        policy.setPolicyType("Health");
        policy.setStartDate(expiry != null ? expiry.minusYears(1) : LocalDate.of(2025, 1, 1));
        policy.setExpiryDate(expiry);
        policy.setPremiumAmount(new BigDecimal("7500.00"));
        policy.setRenewalStatus(renewalStatus);
        return policy;
    }

    // =========================================================================
    // Discrete Reminder Windows: 30, 7, and 1 Day
    // =========================================================================

    @Test
    @DisplayName("Policy expiring in 30 days is matched to the 30-day window and processed")
    void testPolicyExpiringIn30Days() {
        LocalDate expiry30 = evaluationDate.plusDays(30); // 2026-11-14
        Policy policy = createTestPolicy(1L, "POL-30D", expiry30, null);

        // Window matching check
        Optional<Integer> matchedWindow = reminderService.determineMatchingWindow(policy, evaluationDate);
        assertTrue(matchedWindow.isPresent());
        assertEquals(30, matchedWindow.get());

        // Batch execution check
        when(policyRepository.findPoliciesExpiringOn(expiry30)).thenReturn(List.of(policy));
        when(policyRepository.findPoliciesExpiringOn(evaluationDate.plusDays(7))).thenReturn(Collections.emptyList());
        when(policyRepository.findPoliciesExpiringOn(evaluationDate.plusDays(1))).thenReturn(Collections.emptyList());

        when(reminderRecordRepository.existsByPolicyIdAndReminderWindowAndPolicyExpiryDateAndStatusIn(
                eq(1L), eq(30), eq(expiry30), anyCollection())).thenReturn(false);

        when(notificationService.sendRenewalReminder(policy, 30))
                .thenReturn(NotificationResult.simulated("[SIMULATION] POL-30D"));

        when(reminderRecordRepository.save(any(ReminderRecord.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ReminderRunReport report = reminderService.processAllReminders(evaluationDate);

        assertEquals(1, report.getEligibleCount());
        assertEquals(1, report.getSimulatedCount());
        assertEquals(0, report.getFailedCount());
        assertEquals(0, report.getDuplicateSkippedCount());

        ArgumentCaptor<ReminderRecord> captor = ArgumentCaptor.forClass(ReminderRecord.class);
        verify(reminderRecordRepository).save(captor.capture());
        ReminderRecord saved = captor.getValue();
        assertEquals("POL-30D", saved.getPolicyNumber());
        assertEquals(30, saved.getReminderWindow());
        assertEquals(ReminderRecord.STATUS_SIMULATED, saved.getStatus());
        assertEquals(ReminderRecord.CHANNEL_SIMULATION, saved.getChannel());
    }

    @Test
    @DisplayName("Policy expiring in 7 days is matched to the 7-day window and processed")
    void testPolicyExpiringIn7Days() {
        LocalDate expiry7 = evaluationDate.plusDays(7); // 2026-10-22
        Policy policy = createTestPolicy(2L, "POL-7D", expiry7, null);

        Optional<Integer> matchedWindow = reminderService.determineMatchingWindow(policy, evaluationDate);
        assertTrue(matchedWindow.isPresent());
        assertEquals(7, matchedWindow.get());

        when(policyRepository.findPoliciesExpiringOn(evaluationDate.plusDays(30))).thenReturn(Collections.emptyList());
        when(policyRepository.findPoliciesExpiringOn(expiry7)).thenReturn(List.of(policy));
        when(policyRepository.findPoliciesExpiringOn(evaluationDate.plusDays(1))).thenReturn(Collections.emptyList());

        when(reminderRecordRepository.existsByPolicyIdAndReminderWindowAndPolicyExpiryDateAndStatusIn(
                eq(2L), eq(7), eq(expiry7), anyCollection())).thenReturn(false);

        when(notificationService.sendRenewalReminder(policy, 7))
                .thenReturn(NotificationResult.simulated("[SIMULATION] POL-7D"));
        when(reminderRecordRepository.save(any(ReminderRecord.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ReminderRunReport report = reminderService.processAllReminders(evaluationDate);

        assertEquals(1, report.getEligibleCount());
        assertEquals(1, report.getSimulatedCount());
    }

    @Test
    @DisplayName("Policy expiring in 1 day is matched to the 1-day window and processed")
    void testPolicyExpiringIn1Day() {
        LocalDate expiry1 = evaluationDate.plusDays(1); // 2026-10-16
        Policy policy = createTestPolicy(3L, "POL-1D", expiry1, null);

        Optional<Integer> matchedWindow = reminderService.determineMatchingWindow(policy, evaluationDate);
        assertTrue(matchedWindow.isPresent());
        assertEquals(1, matchedWindow.get());

        when(policyRepository.findPoliciesExpiringOn(evaluationDate.plusDays(30))).thenReturn(Collections.emptyList());
        when(policyRepository.findPoliciesExpiringOn(evaluationDate.plusDays(7))).thenReturn(Collections.emptyList());
        when(policyRepository.findPoliciesExpiringOn(expiry1)).thenReturn(List.of(policy));

        when(reminderRecordRepository.existsByPolicyIdAndReminderWindowAndPolicyExpiryDateAndStatusIn(
                eq(3L), eq(1), eq(expiry1), anyCollection())).thenReturn(false);

        when(notificationService.sendRenewalReminder(policy, 1))
                .thenReturn(NotificationResult.simulated("[SIMULATION] POL-1D"));
        when(reminderRecordRepository.save(any(ReminderRecord.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ReminderRunReport report = reminderService.processAllReminders(evaluationDate);

        assertEquals(1, report.getEligibleCount());
        assertEquals(1, report.getSimulatedCount());
    }

    // =========================================================================
    // Excluded / Non-eligible Policies
    // =========================================================================

    @Test
    @DisplayName("Already expired policy is not eligible for reminders")
    void testAlreadyExpiredPolicyIsNotEligible() {
        LocalDate pastExpiry = evaluationDate.minusDays(5);
        Policy expiredPolicy = createTestPolicy(4L, "POL-EXPIRED", pastExpiry, null);

        assertFalse(reminderService.isPolicyEligible(expiredPolicy, evaluationDate));
        assertTrue(reminderService.determineMatchingWindow(expiredPolicy, evaluationDate).isEmpty());
    }

    @Test
    @DisplayName("Policy already marked as RENEWED is not eligible for reminders")
    void testAlreadyRenewedPolicyIsNotEligible() {
        LocalDate expiry7 = evaluationDate.plusDays(7);
        Policy renewedPolicy = createTestPolicy(5L, "POL-RENEWED", expiry7, PolicyService.STATUS_RENEWED);

        assertFalse(reminderService.isPolicyEligible(renewedPolicy, evaluationDate));
        assertTrue(reminderService.determineMatchingWindow(renewedPolicy, evaluationDate).isEmpty());
    }

    @Test
    @DisplayName("Policy outside discrete reminder windows (e.g. 15 days or 45 days) does not match")
    void testPolicyOutsideReminderWindows() {
        Policy policy15 = createTestPolicy(6L, "POL-15D", evaluationDate.plusDays(15), null);
        Policy policy45 = createTestPolicy(7L, "POL-45D", evaluationDate.plusDays(45), null);

        assertTrue(reminderService.isPolicyEligible(policy15, evaluationDate));
        assertTrue(reminderService.determineMatchingWindow(policy15, evaluationDate).isEmpty());

        assertTrue(reminderService.isPolicyEligible(policy45, evaluationDate));
        assertTrue(reminderService.determineMatchingWindow(policy45, evaluationDate).isEmpty());
    }

    @Test
    @DisplayName("Policy with missing/null expiry date is handled safely without throwing exceptions")
    void testPolicyWithMissingExpiryDateHandledSafely() {
        Policy missingDatePolicy = createTestPolicy(8L, "POL-NO-DATE", null, null);

        assertFalse(reminderService.isPolicyEligible(missingDatePolicy, evaluationDate));
        assertTrue(reminderService.determineMatchingWindow(missingDatePolicy, evaluationDate).isEmpty());
    }

    // =========================================================================
    // Duplicate Reminder Prevention
    // =========================================================================

    @Test
    @DisplayName("Duplicate reminder prevention: previously simulated reminder in same cycle skips new reminder")
    void testDuplicateReminderPrevention() {
        LocalDate expiry7 = evaluationDate.plusDays(7);
        Policy policy = createTestPolicy(9L, "POL-DUP", expiry7, null);

        when(policyRepository.findPoliciesExpiringOn(evaluationDate.plusDays(30))).thenReturn(Collections.emptyList());
        when(policyRepository.findPoliciesExpiringOn(expiry7)).thenReturn(List.of(policy));
        when(policyRepository.findPoliciesExpiringOn(evaluationDate.plusDays(1))).thenReturn(Collections.emptyList());

        // Previous successful reminder exists
        when(reminderRecordRepository.existsByPolicyIdAndReminderWindowAndPolicyExpiryDateAndStatusIn(
                eq(9L), eq(7), eq(expiry7), anyCollection())).thenReturn(true);

        ReminderRunReport report = reminderService.processAllReminders(evaluationDate);

        assertEquals(0, report.getEligibleCount());
        assertEquals(0, report.getSimulatedCount());
        assertEquals(1, report.getDuplicateSkippedCount());

        verify(notificationService, never()).sendRenewalReminder(any(), anyInt());
        verify(reminderRecordRepository, never()).save(any(ReminderRecord.class));
    }

    // =========================================================================
    // Safe Dry-Run and Notification Failure Handling
    // =========================================================================

    @Test
    @DisplayName("Notification failure does not mark reminder as sent; records FAILED status")
    void testNotificationFailureRecordsFailedStatus() {
        LocalDate expiry1 = evaluationDate.plusDays(1);
        Policy policy = createTestPolicy(10L, "POL-FAIL", expiry1, null);

        when(policyRepository.findPoliciesExpiringOn(evaluationDate.plusDays(30))).thenReturn(Collections.emptyList());
        when(policyRepository.findPoliciesExpiringOn(evaluationDate.plusDays(7))).thenReturn(Collections.emptyList());
        when(policyRepository.findPoliciesExpiringOn(expiry1)).thenReturn(List.of(policy));

        when(reminderRecordRepository.existsByPolicyIdAndReminderWindowAndPolicyExpiryDateAndStatusIn(
                eq(10L), eq(1), eq(expiry1), anyCollection())).thenReturn(false);

        // Simulation fails (e.g., missing customer email)
        when(notificationService.sendRenewalReminder(policy, 1))
                .thenReturn(NotificationResult.failure(ReminderRecord.CHANNEL_SIMULATION, "Customer email missing"));
        when(reminderRecordRepository.save(any(ReminderRecord.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ReminderRunReport report = reminderService.processAllReminders(evaluationDate);

        assertEquals(1, report.getEligibleCount());
        assertEquals(0, report.getSimulatedCount());
        assertEquals(1, report.getFailedCount());

        ArgumentCaptor<ReminderRecord> captor = ArgumentCaptor.forClass(ReminderRecord.class);
        verify(reminderRecordRepository).save(captor.capture());
        ReminderRecord saved = captor.getValue();
        assertEquals(ReminderRecord.STATUS_FAILED, saved.getStatus());
        assertEquals("Customer email missing", saved.getErrorMessage());
    }
}
