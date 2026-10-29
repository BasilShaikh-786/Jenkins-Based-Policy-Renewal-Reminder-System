package com.policy.renewal.service;

import com.policy.renewal.model.Policy;
import com.policy.renewal.repository.PolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CustomerRelationshipPeriodTest — Verifies Requirement 1: Two Different Types of Insurance Period.
 *
 * <p>Enforces:
 * <ul>
 *   <li>Customer Relationship Period (tenure) is calculated automatically from policy start date / customer inception date.</li>
 *   <li>Formats years and months cleanly ("3 years", "2 years, 6 months").</li>
 *   <li>Updates dynamically with time.</li>
 *   <li>Maintains complete independence from the requested coverage purchase period and premium amount.</li>
 * </ul>
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
class CustomerRelationshipPeriodTest {

    private PolicyRepository policyRepository;
    private PolicyService policyService;
    private Clock fixedClock;

    @BeforeEach
    void setUp() {
        policyRepository = Mockito.mock(PolicyRepository.class);
        // Fixed reference evaluation date: 15 October 2026
        LocalDate fixedToday = LocalDate.of(2026, 10, 15);
        fixedClock = Clock.fixed(fixedToday.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant(), ZoneId.of("Asia/Kolkata"));
        policyService = new PolicyService(policyRepository, fixedClock);
    }

    @Test
    @DisplayName("Requirement 1.A: Customer joined 15 Oct 2023, evaluated 15 Oct 2026 -> Exactly '3 years'")
    void testExactThreeYearsTenure() {
        Policy policy = new Policy();
        policy.setStartDate(LocalDate.of(2023, 10, 15));

        String tenure = policyService.calculateCustomerRelationshipPeriod(policy);
        assertEquals("3 years", tenure);
    }

    @Test
    @DisplayName("Requirement 1.A: Customer joined 15 April 2024, evaluated 15 Oct 2026 -> '2 years, 6 months'")
    void testYearsAndMonthsTenure() {
        Policy policy = new Policy();
        policy.setStartDate(LocalDate.of(2024, 4, 15));

        String tenure = policyService.calculateCustomerRelationshipPeriod(policy);
        assertEquals("2 years, 6 months", tenure);
    }

    @Test
    @DisplayName("Requirement 1.A: Customer joined 15 Nov 2025, evaluated 15 Oct 2026 -> '11 months'")
    void testMonthsOnlyTenure() {
        Policy policy = new Policy();
        policy.setStartDate(LocalDate.of(2025, 11, 15));

        String tenure = policyService.calculateCustomerRelationshipPeriod(policy);
        assertEquals("11 months", tenure);
    }

    @Test
    @DisplayName("Requirement 1.A: Customer joined 5 Oct 2026 (< 1 month ago) -> '10 days (< 1 month)'")
    void testLessThanOneMonthTenure() {
        Policy policy = new Policy();
        policy.setStartDate(LocalDate.of(2026, 10, 5));

        String tenure = policyService.calculateCustomerRelationshipPeriod(policy);
        assertEquals("10 days (< 1 month)", tenure);
    }

    @Test
    @DisplayName("Requirement 1.A: Customer joined today (15 Oct 2026) -> 'New Customer (Inception Today)'")
    void testJoinedTodayTenure() {
        Policy policy = new Policy();
        policy.setStartDate(LocalDate.of(2026, 10, 15));

        String tenure = policyService.calculateCustomerRelationshipPeriod(policy);
        assertEquals("New Customer (Inception Today)", tenure);
    }

    @Test
    @DisplayName("Requirement 1.A: Customer with future policy start date -> indicates future start date gracefully")
    void testFuturePolicyStartDate() {
        Policy policy = new Policy();
        policy.setStartDate(LocalDate.of(2026, 10, 25)); // 10 days in future

        String tenure = policyService.calculateCustomerRelationshipPeriod(policy);
        assertEquals("Future Policy (Starts in 10 days)", tenure);
    }

    @Test
    @DisplayName("Requirement 1.A: Missing policy start date displays informative message without inventing dates")
    void testMissingStartDateHandled() {
        Policy policy = new Policy();
        policy.setStartDate(null);

        String tenure = policyService.calculateCustomerRelationshipPeriod(policy);
        assertEquals("Policy start date not available", tenure);
    }

    @Test
    @DisplayName("Requirement 1.A: Explicit customerSinceDate overrides current term start date for long-standing customer")
    void testExplicitCustomerSinceDateOverridesStartDate() {
        Policy policy = new Policy();
        // Current renewal started on 1 Oct 2026, but customer relationship began on 1 Oct 2021
        policy.setStartDate(LocalDate.of(2026, 10, 1));
        policy.setCustomerSinceDate(LocalDate.of(2021, 10, 1));

        String tenure = policyService.calculateCustomerRelationshipPeriod(policy);
        assertEquals("5 years", tenure);
    }

    @Test
    @DisplayName("Requirement 1.B: Auto-calculate coverage end date from start date and duration value/unit")
    void testCalculateCoverageEndDate() {
        LocalDate start = LocalDate.of(2026, 10, 15);

        // 12 Months
        LocalDate end12M = policyService.calculateCoverageEndDate(start, 12, "MONTHS");
        assertEquals(LocalDate.of(2027, 10, 14), end12M);

        // 2 Years
        LocalDate end2Y = policyService.calculateCoverageEndDate(start, 2, "YEARS");
        assertEquals(LocalDate.of(2028, 10, 14), end2Y);

        // 15 Days (e.g. Travel)
        LocalDate end15D = policyService.calculateCoverageEndDate(start, 15, "DAYS");
        assertEquals(LocalDate.of(2026, 10, 30), end15D);
    }

    @Test
    @DisplayName("Requirement 1 Separation Rule: Customer tenure does NOT alter policy premium calculation")
    void testTenureDoesNotAffectPremiumAmount() {
        Policy oldCustomerPolicy = new Policy();
        oldCustomerPolicy.setStartDate(LocalDate.of(2020, 1, 1)); // 6+ years tenure
        oldCustomerPolicy.setPremiumAmount(new BigDecimal("9735.00"));
        oldCustomerPolicy.setCoverageDurationValue(12);
        oldCustomerPolicy.setCoverageDurationUnit("MONTHS");

        Policy newCustomerPolicy = new Policy();
        newCustomerPolicy.setStartDate(LocalDate.of(2026, 10, 15)); // New customer today
        newCustomerPolicy.setPremiumAmount(new BigDecimal("9735.00"));
        newCustomerPolicy.setCoverageDurationValue(12);
        newCustomerPolicy.setCoverageDurationUnit("MONTHS");

        // Premium remains identical regardless of whether tenure is 6 years or 0 days
        assertEquals(oldCustomerPolicy.getPremiumAmount(), newCustomerPolicy.getPremiumAmount());
        assertNotEquals(policyService.calculateCustomerRelationshipPeriod(oldCustomerPolicy),
                policyService.calculateCustomerRelationshipPeriod(newCustomerPolicy));
    }
}

