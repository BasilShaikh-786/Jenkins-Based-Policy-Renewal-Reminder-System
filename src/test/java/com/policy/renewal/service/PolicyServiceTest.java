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
}
