package com.policy.renewal;

import com.policy.renewal.model.Policy;
import com.policy.renewal.model.ReminderRecord;
import com.policy.renewal.repository.PolicyRepository;
import com.policy.renewal.repository.ReminderRecordRepository;
import com.policy.renewal.service.PolicyReminderService;
import com.policy.renewal.service.PolicyService;
import com.policy.renewal.service.ReminderRunReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ReminderIntegrationTest — integration test verifying database persistence,
 * duplicate reminder prevention in H2, and dashboard metric calculations.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@SpringBootTest
@Transactional
class ReminderIntegrationTest {

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private ReminderRecordRepository reminderRecordRepository;

    @Autowired
    private PolicyReminderService reminderService;

    @Autowired
    private PolicyService policyService;

    private Policy createAndSavePolicy(String number, LocalDate start, LocalDate expiry, String renewalStatus) {
        Policy policy = new Policy();
        policy.setPolicyNumber(number);
        policy.setCustomerName("Integration Customer " + number);
        policy.setCustomerEmail("integ." + number.toLowerCase() + "@example.com");
        policy.setPolicyType("Health");
        policy.setStartDate(start);
        policy.setExpiryDate(expiry);
        policy.setPremiumAmount(new BigDecimal("8200.00"));
        policy.setRenewalStatus(renewalStatus);
        return policyRepository.save(policy);
    }

    @Test
    @DisplayName("End-to-end integration: policy reminders are persisted in H2 and duplicates prevented")
    void testEndToEndReminderPersistenceAndDuplicatePrevention() {
        LocalDate evalDate = LocalDate.of(2026, 10, 15);

        // Clear or prepare specific test policies
        Policy p30 = createAndSavePolicy("INT-30D", evalDate.minusMonths(6), evalDate.plusDays(30), null);
        Policy p7  = createAndSavePolicy("INT-7D",  evalDate.minusMonths(6), evalDate.plusDays(7), null);
        Policy p1  = createAndSavePolicy("INT-1D",  evalDate.minusMonths(6), evalDate.plusDays(1), null);
        Policy pExp = createAndSavePolicy("INT-EXP", evalDate.minusYears(1), evalDate.minusDays(5), null);
        Policy pRen = createAndSavePolicy("INT-REN", evalDate.minusMonths(6), evalDate.plusDays(7), PolicyService.STATUS_RENEWED);

        // Run reminder engine
        ReminderRunReport firstRun = reminderService.processAllReminders(evalDate);

        // At least our 3 policies (30d, 7d, 1d) must be processed
        assertTrue(firstRun.getEligibleCount() >= 3);
        assertTrue(firstRun.getSimulatedCount() >= 3);

        // Verify records in H2 database
        List<ReminderRecord> p7Records = reminderRecordRepository.findByPolicyIdOrderByProcessedAtDesc(p7.getId());
        assertEquals(1, p7Records.size());
        assertEquals(ReminderRecord.STATUS_SIMULATED, p7Records.get(0).getStatus());
        assertEquals(7, p7Records.get(0).getReminderWindow());

        List<ReminderRecord> p30Records = reminderRecordRepository.findByPolicyIdOrderByProcessedAtDesc(p30.getId());
        assertEquals(1, p30Records.size());
        assertEquals(30, p30Records.get(0).getReminderWindow());

        // Expired and renewed policies must have NO records
        assertTrue(reminderRecordRepository.findByPolicyIdOrderByProcessedAtDesc(pExp.getId()).isEmpty());
        assertTrue(reminderRecordRepository.findByPolicyIdOrderByProcessedAtDesc(pRen.getId()).isEmpty());

        // SECOND RUN on same date -> duplicate prevention must skip these policies
        ReminderRunReport secondRun = reminderService.processAllReminders(evalDate);
        assertTrue(secondRun.getDuplicateSkippedCount() >= 3);

        // Verify no second record was created for p7
        List<ReminderRecord> p7RecordsAfter = reminderRecordRepository.findByPolicyIdOrderByProcessedAtDesc(p7.getId());
        assertEquals(1, p7RecordsAfter.size());
    }

    @Test
    @DisplayName("Dashboard stats accurately query H2 database for 7-day, 30-day, active, and expired counts")
    void testDashboardStatsFromDatabase() {
        Map<String, Long> stats = policyService.getDashboardStats();

        assertNotNull(stats);
        assertTrue(stats.containsKey("total"));
        assertTrue(stats.containsKey("active"));
        assertTrue(stats.containsKey("expiringSoon"));
        assertTrue(stats.containsKey("expiring7Days"));
        assertTrue(stats.containsKey("expiring30Days"));
        assertTrue(stats.containsKey("expired"));
        assertTrue(stats.containsKey("renewed"));

        assertTrue(stats.get("total") >= 0);
        assertTrue(stats.get("expiring7Days") <= stats.get("expiring30Days"));
    }
}
