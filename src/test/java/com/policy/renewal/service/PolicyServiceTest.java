package com.policy.renewal.service;

import com.policy.renewal.model.Policy;
import com.policy.renewal.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * PolicyServiceTest — unit tests verifying all SRS business rules (BR-01 through BR-07).
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyService policyService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(policyService, "renewalAlertDays", 30);
    }

    private Policy createSamplePolicy(String policyNumber, LocalDate start, LocalDate expiry) {
        Policy policy = new Policy();
        policy.setPolicyNumber(policyNumber);
        policy.setCustomerName("Test Customer");
        policy.setCustomerEmail("test@example.com");
        policy.setPolicyType("Health");
        policy.setStartDate(start);
        policy.setExpiryDate(expiry);
        policy.setPremiumAmount(new BigDecimal("5000.00"));
        return policy;
    }

    @Test
    @DisplayName("BR-04: Expiry date before start date must throw IllegalArgumentException")
    void testExpiryDateBeforeStartDateThrowsException() {
        LocalDate start = LocalDate.now();
        LocalDate expiry = start.minusDays(5);
        Policy policy = createSamplePolicy("POL-TEST-01", start, expiry);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            policyService.save(policy);
        });

        assertTrue(ex.getMessage().contains("earlier than the start date"));
        verify(policyRepository, never()).save(any(Policy.class));
    }

    @Test
    @DisplayName("BR-01: Duplicate policy number for a new policy must throw IllegalArgumentException")
    void testDuplicatePolicyNumberThrowsException() {
        LocalDate start = LocalDate.now();
        LocalDate expiry = start.plusYears(1);
        Policy policy = createSamplePolicy("POL-DUPLICATE", start, expiry);

        Policy existingPolicy = createSamplePolicy("POL-DUPLICATE", start, expiry);
        existingPolicy.setId(99L);

        when(policyRepository.findByPolicyNumber("POL-DUPLICATE"))
                .thenReturn(Optional.of(existingPolicy));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            policyService.save(policy);
        });

        assertTrue(ex.getMessage().contains("already in use"));
        verify(policyRepository, never()).save(any(Policy.class));
    }

    @Test
    @DisplayName("BR-01: Updating an existing policy with its own policy number should succeed")
    void testUpdateExistingPolicyWithSameNumberSucceeds() {
        LocalDate start = LocalDate.now();
        LocalDate expiry = start.plusYears(1);
        Policy policy = createSamplePolicy("POL-SELF", start, expiry);
        policy.setId(10L);

        when(policyRepository.findByPolicyNumber("POL-SELF"))
                .thenReturn(Optional.of(policy));
        when(policyRepository.save(policy)).thenReturn(policy);

        Policy saved = policyService.save(policy);
        assertNotNull(saved);
        assertEquals("POL-SELF", saved.getPolicyNumber());
        verify(policyRepository, times(1)).save(policy);
    }

    @Test
    @DisplayName("BR-03: Status derivation returns RENEWED when renewalStatus is 'RENEWED'")
    void testStatusDerivationRenewed() {
        Policy policy = createSamplePolicy("POL-01", LocalDate.now().minusMonths(6), LocalDate.now().minusDays(10));
        policy.setRenewalStatus(PolicyService.STATUS_RENEWED);

        String status = policyService.getDerivedStatus(policy);
        assertEquals(PolicyService.STATUS_RENEWED, status);
    }

    @Test
    @DisplayName("BR-03: Status derivation returns EXPIRED when past expiry date and not renewed")
    void testStatusDerivationExpired() {
        Policy policy = createSamplePolicy("POL-02", LocalDate.now().minusMonths(6), LocalDate.now().minusDays(1));

        String status = policyService.getDerivedStatus(policy);
        assertEquals(PolicyService.STATUS_EXPIRED, status);
    }

    @Test
    @DisplayName("BR-03: Status derivation returns EXPIRING_SOON when within alert window (30 days)")
    void testStatusDerivationExpiringSoon() {
        Policy policy = createSamplePolicy("POL-03", LocalDate.now().minusMonths(11), LocalDate.now().plusDays(15));

        String status = policyService.getDerivedStatus(policy);
        assertEquals(PolicyService.STATUS_EXPIRING_SOON, status);
    }

    @Test
    @DisplayName("BR-03: Status derivation returns ACTIVE when expiry date is beyond alert window")
    void testStatusDerivationActive() {
        Policy policy = createSamplePolicy("POL-04", LocalDate.now(), LocalDate.now().plusDays(60));

        String status = policyService.getDerivedStatus(policy);
        assertEquals(PolicyService.STATUS_ACTIVE, status);
    }

    @Test
    @DisplayName("FR-11: markAsRenewed updates renewalStatus and persists entity")
    void testMarkAsRenewed() {
        Policy policy = createSamplePolicy("POL-RENEW", LocalDate.now().minusMonths(11), LocalDate.now().plusDays(5));
        policy.setId(5L);

        when(policyRepository.findById(5L)).thenReturn(Optional.of(policy));

        policyService.markAsRenewed(5L);

        assertEquals(PolicyService.STATUS_RENEWED, policy.getRenewalStatus());
        verify(policyRepository, times(1)).save(policy);
    }

    @Test
    @DisplayName("FR-11: markAsRenewed with invalid ID throws IllegalArgumentException")
    void testMarkAsRenewedInvalidIdThrows() {
        when(policyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            policyService.markAsRenewed(999L);
        });
    }

    @Test
    @DisplayName("Delete policy by ID removes the record from database")
    void testDeleteByIdSuccess() {
        Policy policy = createSamplePolicy("POL-DEL", LocalDate.now().minusMonths(6), LocalDate.now().plusMonths(6));
        policy.setId(5L);

        when(policyRepository.findById(5L)).thenReturn(Optional.of(policy));

        policyService.deleteById(5L);

        verify(policyRepository, times(1)).delete(policy);
    }

    @Test
    @DisplayName("Delete policy by invalid ID throws IllegalArgumentException")
    void testDeleteByIdNotFoundThrows() {
        when(policyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            policyService.deleteById(999L);
        });
        verify(policyRepository, never()).delete(any(Policy.class));
    }

    @Test
    @DisplayName("Search finds policy matching customer email")
    void testSearchByCustomerEmail() {
        Policy policy = createSamplePolicy("POL-EMAIL", LocalDate.now(), LocalDate.now().plusYears(1));
        policy.setId(7L);
        policy.setCustomerEmail("special.client@example.com");

        when(policyRepository.findByCustomerNameContainingIgnoreCaseOrderByExpiryDateAsc("special.client"))
                .thenReturn(new java.util.ArrayList<>());
        when(policyRepository.findByPolicyNumberContainingIgnoreCase("special.client"))
                .thenReturn(new java.util.ArrayList<>());
        when(policyRepository.findByCustomerEmailContainingIgnoreCase("special.client"))
                .thenReturn(new java.util.ArrayList<>(List.of(policy)));

        List<Policy> results = policyService.search("special.client");

        assertEquals(1, results.size());
        assertEquals("POL-EMAIL", results.get(0).getPolicyNumber());
    }

    @Test
    @DisplayName("Filter policies correctly filters by derived status and expiry date range")
    void testFilterPoliciesByDateRangeAndStatus() {
        Policy p1 = createSamplePolicy("POL-1", LocalDate.now().minusMonths(6), LocalDate.now().plusDays(10)); // Expiring soon
        Policy p2 = createSamplePolicy("POL-2", LocalDate.now().minusMonths(6), LocalDate.now().plusDays(60)); // Active
        Policy p3 = createSamplePolicy("POL-3", LocalDate.now().minusMonths(6), LocalDate.now().plusDays(120)); // Active

        List<Policy> all = List.of(p1, p2, p3);

        // Filter by date range (between day 30 and day 90)
        LocalDate from = LocalDate.now().plusDays(30);
        LocalDate to = LocalDate.now().plusDays(90);
        List<Policy> dateFiltered = policyService.filterPolicies(all, null, from, to);
        assertEquals(1, dateFiltered.size());
        assertEquals("POL-2", dateFiltered.get(0).getPolicyNumber());

        // Filter by status ACTIVE
        List<Policy> activeFiltered = policyService.filterPolicies(all, "ACTIVE", null, null);
        assertEquals(2, activeFiltered.size());

        // Filter by status EXPIRING_SOON
        List<Policy> expiringFiltered = policyService.filterPolicies(all, "EXPIRING_SOON", null, null);
        assertEquals(1, expiringFiltered.size());
        assertEquals("POL-1", expiringFiltered.get(0).getPolicyNumber());
    }

    @Test
    @DisplayName("Dashboard stats computes all metrics including 7-day and 30-day windows")
    void testGetDashboardStatsComputesAllMetrics() {
        when(policyRepository.count()).thenReturn(10L);
        when(policyRepository.countByRenewalStatus(PolicyService.STATUS_RENEWED)).thenReturn(2L);
        when(policyRepository.countExpired(any(LocalDate.class))).thenReturn(1L);
        when(policyRepository.countExpiringSoon(any(LocalDate.class), any(LocalDate.class))).thenReturn(3L);
        when(policyRepository.countActive(any(LocalDate.class))).thenReturn(4L);

        Map<String, Long> stats = policyService.getDashboardStats();

        assertEquals(10L, stats.get("total"));
        assertEquals(2L, stats.get("renewed"));
        assertEquals(1L, stats.get("expired"));
        assertEquals(3L, stats.get("expiringSoon"));
        assertEquals(3L, stats.get("expiring7Days"));
        assertEquals(3L, stats.get("expiring30Days"));
        assertEquals(4L, stats.get("active"));
    }

    @Test
    @DisplayName("Issue 2: Saving a 12-month policy with consistent dates succeeds")
    void testSaveConsistent12MonthPolicySucceeds() {
        LocalDate start = LocalDate.of(2024, 12, 19);
        LocalDate expiry = LocalDate.of(2025, 12, 18);
        Policy policy = createSamplePolicy("POL-CONSISTENT-12", start, expiry);
        policy.setCoverageDurationValue(12);
        policy.setCoverageDurationUnit("MONTHS");

        when(policyRepository.findByPolicyNumber("POL-CONSISTENT-12")).thenReturn(Optional.empty());
        when(policyRepository.save(any(Policy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Policy saved = policyService.save(policy);
        assertNotNull(saved);
        assertTrue(saved.isCoveragePeriodConsistent());
        assertFalse(saved.isHistoricalTermReviewRequired());
    }

    @Test
    @DisplayName("Issue 2: Saving a 12-month policy spanning 22 months throws IllegalArgumentException")
    void testSaveInconsistentCoverageDurationThrowsException() {
        LocalDate start = LocalDate.of(2024, 12, 19);
        LocalDate expiry = LocalDate.of(2026, 10, 17); // 22 months span
        Policy policy = createSamplePolicy("POL-INCONSISTENT-22", start, expiry);
        policy.setCoverageDurationValue(12);
        policy.setCoverageDurationUnit("MONTHS");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            policyService.save(policy);
        });

        assertTrue(ex.getMessage().contains("Coverage dates are inconsistent with requested duration"));
        verify(policyRepository, never()).save(any(Policy.class));
    }

    @Test
    @DisplayName("Issue 2: markAsRenewed preserves original inception date in customerSinceDate")
    void testMarkAsRenewedPreservesCustomerSinceDate() {
        LocalDate inception = LocalDate.of(2024, 12, 19);
        LocalDate expiry = LocalDate.of(2025, 12, 18);
        Policy policy = createSamplePolicy("POL-RENEW-INCEPTION", inception, expiry);
        policy.setId(10L);
        policy.setCustomerSinceDate(null); // Initially null

        when(policyRepository.findById(10L)).thenReturn(Optional.of(policy));
        when(policyRepository.save(any(Policy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        policyService.markAsRenewed(10L);

        assertEquals(PolicyService.STATUS_RENEWED, policy.getRenewalStatus());
        assertEquals(inception, policy.getCustomerSinceDate(), "Original inception date must be preserved in customerSinceDate");
    }

    @Test
    @DisplayName("Issue 2: Historical record with 22 months span and null duration is flagged for review")
    void testHistoricalRecordFlaggedForReview() {
        LocalDate start = LocalDate.of(2024, 12, 19);
        LocalDate expiry = LocalDate.of(2026, 10, 17);
        Policy policy = createSamplePolicy("POL-HISTORICAL", start, expiry);
        policy.setCoverageDurationValue(null);
        policy.setRenewalStatus("RENEWED");

        assertTrue(policy.isHistoricalTermReviewRequired());
        assertTrue(policy.getCoveragePeriodReviewReason().contains("months"));
        assertTrue(policy.getCoveragePeriodReviewReason().contains("renewal history"));
    }

    @Test
    @DisplayName("Task 1: Short-term renewed policy (e.g. 13 days) is NOT falsely flagged for review and shows derived days")
    void testShortTermRenewedPolicyNotFlaggedForReview() {
        LocalDate start = LocalDate.of(2026, 10, 26);
        LocalDate expiry = LocalDate.of(2026, 11, 7); // 13 days term
        Policy policy = createSamplePolicy("POL-232", start, expiry);
        policy.setCoverageDurationValue(null);
        policy.setRenewalStatus("RENEWED");

        assertFalse(policy.isHistoricalTermReviewRequired(), "13-day term policy must NOT be falsely flagged for multi-term review");
        assertEquals("13 DAYS (Derived from Term Dates)", policy.getFormattedCoverageDuration());
        assertNull(policy.getEffectiveCustomerSinceDate(), "Unrecorded inception date on renewed policy must be null");

        String tenure = policyService.calculateCustomerRelationshipPeriod(policy);
        assertEquals("Unavailable — original inception date not recorded", tenure);
    }

    @Test
    @DisplayName("Task 1: Demo policy POL-1005 (Renewed Home with separated inception) evaluates correctly")
    void testDemoPolicyRenewedEvaluation() {
        LocalDate inception = LocalDate.of(2024, 11, 15);
        LocalDate start = LocalDate.of(2025, 11, 15);
        LocalDate expiry = LocalDate.of(2026, 11, 15);
        Policy policy = createSamplePolicy("POL-1005", start, expiry);
        policy.setCustomerSinceDate(inception);
        policy.setCoverageDurationValue(12);
        policy.setCoverageDurationUnit("MONTHS");
        policy.setRenewalStatus("RENEWED");

        assertFalse(policy.isHistoricalTermReviewRequired());
        assertEquals("12 MONTHS", policy.getFormattedCoverageDuration());
        assertEquals(inception, policy.getEffectiveCustomerSinceDate());
    }
}
