package com.policy.renewal.service;

import com.policy.renewal.model.Policy;
import com.policy.renewal.repository.PolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DemoPolicyDataInitializer — Idempotent data seeder for 10 realistic demonstration policies.
 *
 * <p>Compliance rules (Task 2 Master Prompt):
 * <ul>
 *   <li>Evaluated relative to demonstration baseline: 29 October 2026.</li>
 *   <li>Checks for duplicate policy numbers before inserting records.</li>
 *   <li>Never deletes, resets, recreates, or overwrites existing database policies.</li>
 *   <li>Classification mapping: "Home & Property" -> "Home", "Travel Shield" -> "Travel".</li>
 *   <li>Maintains separation between historical inception (customerSinceDate) and active term dates.</li>
 *   <li>Preserves POL-1005 renewal workflow demonstration with explicit RENEWED status.</li>
 * </ul>
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Component
@Order(25)
public class DemoPolicyDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoPolicyDataInitializer.class);

    private final PolicyRepository policyRepository;

    public DemoPolicyDataInitializer(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    @Override
    public void run(String... args) {
        log.info("[DEMO-SEED] Checking database for 10 realistic demo policies...");

        List<Policy> demoPolicies = buildDemoPolicies();
        int insertedCount = 0;
        int skippedCount = 0;

        for (Policy demo : demoPolicies) {
            if (policyRepository.findByPolicyNumber(demo.getPolicyNumber()).isPresent()) {
                log.info("[DEMO-SEED] Demo policy {} already exists in database. Skipping insertion.", demo.getPolicyNumber());
                skippedCount++;
            } else {
                policyRepository.save(demo);
                log.info("[DEMO-SEED] Inserted demo policy {} ({}) successfully.", demo.getPolicyNumber(), demo.getPolicyType());
                insertedCount++;
            }
        }

        log.info("[DEMO-SEED] Demo policy verification complete. Inserted: {}, Skipped (Already existed): {}",
                insertedCount, skippedCount);
    }

    /**
     * Builds the 10 demo policies with verified scenarios based on 29 October 2026.
     *
     * @return list of configured demo policies
     */
    public static List<Policy> buildDemoPolicies() {
        List<Policy> list = new ArrayList<>();

        // 1. POL-1001 — Health Insurance (~30 days remaining)
        Policy p1 = new Policy();
        p1.setPolicyNumber("POL-1001");
        p1.setCustomerName("Ramesh Kumar");
        p1.setCustomerEmail("ramesh.kumar@example.com");
        p1.setPolicyType("Health");
        p1.setCustomerSinceDate(LocalDate.of(2025, 10, 29));
        p1.setStartDate(LocalDate.of(2025, 10, 29));
        p1.setExpiryDate(LocalDate.of(2026, 11, 28));
        p1.setPremiumAmount(new BigDecimal("12000.00"));
        p1.setCoverageDurationValue(13);
        p1.setCoverageDurationUnit("MONTHS");
        p1.setProductName("Complete Health Guard");
        p1.setReferenceInsurer("HDFC ERGO General Insurance");
        p1.setNotes("Demo Scenario 1: Health policy with approximately 30 days remaining (Within 30-day reminder window).");
        list.add(p1);

        // 2. POL-1002 — Vehicle / Auto Insurance (~7 days remaining)
        Policy p2 = new Policy();
        p2.setPolicyNumber("POL-1002");
        p2.setCustomerName("Priya Sharma");
        p2.setCustomerEmail("priya.sharma@example.com");
        p2.setPolicyType("Vehicle");
        p2.setCustomerSinceDate(LocalDate.of(2025, 11, 5));
        p2.setStartDate(LocalDate.of(2025, 11, 5));
        p2.setExpiryDate(LocalDate.of(2026, 11, 5));
        p2.setPremiumAmount(new BigDecimal("8500.00"));
        p2.setCoverageDurationValue(12);
        p2.setCoverageDurationUnit("MONTHS");
        p2.setProductName("Motor Comprehensive Cover");
        p2.setReferenceInsurer("Bajaj Allianz General Insurance");
        p2.setNotes("Demo Scenario 2: Vehicle policy with approximately 7 days remaining (Urgent 7-day reminder notice).");
        list.add(p2);

        // 3. POL-1003 — Life Insurance (~1 day remaining)
        Policy p3 = new Policy();
        p3.setPolicyNumber("POL-1003");
        p3.setCustomerName("Anand Verma");
        p3.setCustomerEmail("anand.verma@example.com");
        p3.setPolicyType("Life");
        p3.setCustomerSinceDate(LocalDate.of(2025, 10, 30));
        p3.setStartDate(LocalDate.of(2025, 10, 30));
        p3.setExpiryDate(LocalDate.of(2026, 10, 30));
        p3.setPremiumAmount(new BigDecimal("18000.00"));
        p3.setCoverageDurationValue(12);
        p3.setCoverageDurationUnit("MONTHS");
        p3.setProductName("Saral Jeevan Bima Term Life");
        p3.setReferenceInsurer("Life Insurance Corporation of India (LIC)");
        p3.setNotes("Demo Scenario 3: Life policy with approximately 1 day remaining (Critical 1-day expiry warning).");
        list.add(p3);

        // 4. POL-1004 — Health Insurance (Expired policy)
        Policy p4 = new Policy();
        p4.setPolicyNumber("POL-1004");
        p4.setCustomerName("Sunita Patel");
        p4.setCustomerEmail("sunita.patel@example.com");
        p4.setPolicyType("Health");
        p4.setCustomerSinceDate(LocalDate.of(2025, 10, 20));
        p4.setStartDate(LocalDate.of(2025, 10, 20));
        p4.setExpiryDate(LocalDate.of(2026, 10, 20));
        p4.setPremiumAmount(new BigDecimal("15000.00"));
        p4.setCoverageDurationValue(12);
        p4.setCoverageDurationUnit("MONTHS");
        p4.setProductName("Health Companion Silver");
        p4.setReferenceInsurer("Max Bupa Health Insurance");
        p4.setNotes("Demo Scenario 4: Expired policy (Lapsed 9 days prior to evaluation baseline).");
        list.add(p4);

        // 5. POL-1005 — Home & Property (Renewal demonstration, mapped to 'Home')
        Policy p5 = new Policy();
        p5.setPolicyNumber("POL-1005");
        p5.setCustomerName("Vikram Malhotra");
        p5.setCustomerEmail("vikram.malhotra@example.com");
        p5.setPolicyType("Home");
        p5.setCustomerSinceDate(LocalDate.of(2024, 11, 15)); // Inception 1 year earlier
        p5.setStartDate(LocalDate.of(2025, 11, 15)); // Renewed term start
        p5.setExpiryDate(LocalDate.of(2026, 11, 15));
        p5.setPremiumAmount(new BigDecimal("9000.00"));
        p5.setCoverageDurationValue(12);
        p5.setCoverageDurationUnit("MONTHS");
        p5.setRenewalStatus("RENEWED");
        p5.setProductName("Home Structure & Content Shield");
        p5.setReferenceInsurer("ICICI Lombard General Insurance");
        p5.setNotes("Demo Scenario 5: Home & Property renewal demonstration (Historical tenure separated from current term).");
        list.add(p5);

        // 6. POL-1006 — Travel Shield (Expired/non-renewal demo, mapped to 'Travel')
        Policy p6 = new Policy();
        p6.setPolicyNumber("POL-1006");
        p6.setCustomerName("Neha Gupta");
        p6.setCustomerEmail("neha.gupta@example.com");
        p6.setPolicyType("Travel");
        p6.setCustomerSinceDate(LocalDate.of(2026, 10, 1));
        p6.setStartDate(LocalDate.of(2026, 10, 1));
        p6.setExpiryDate(LocalDate.of(2026, 10, 28));
        p6.setPremiumAmount(new BigDecimal("3500.00"));
        p6.setCoverageDurationValue(28);
        p6.setCoverageDurationUnit("DAYS");
        p6.setProductName("International Single-Trip Travel Shield");
        p6.setReferenceInsurer("Tata AIG General Insurance");
        p6.setNotes("Demo Scenario 6: Travel Shield expired non-renewal demonstration (28-day single trip).");
        list.add(p6);

        // 7. POL-1007 — Vehicle / Auto Insurance (Active policy with longer coverage)
        Policy p7 = new Policy();
        p7.setPolicyNumber("POL-1007");
        p7.setCustomerName("Arjun Nair");
        p7.setCustomerEmail("arjun.nair@example.com");
        p7.setPolicyType("Vehicle");
        p7.setCustomerSinceDate(LocalDate.of(2026, 10, 10));
        p7.setStartDate(LocalDate.of(2026, 10, 10));
        p7.setExpiryDate(LocalDate.of(2027, 10, 10));
        p7.setPremiumAmount(new BigDecimal("11000.00"));
        p7.setCoverageDurationValue(12);
        p7.setCoverageDurationUnit("MONTHS");
        p7.setProductName("Two-Wheeler / Auto Comprehensive");
        p7.setReferenceInsurer("SBI General Insurance");
        p7.setNotes("Demo Scenario 7: Active vehicle policy with long coverage term (Expiring Oct 2027).");
        list.add(p7);

        // 8. POL-1008 — Health Insurance (Expires today)
        Policy p8 = new Policy();
        p8.setPolicyNumber("POL-1008");
        p8.setCustomerName("Deepak Joshi");
        p8.setCustomerEmail("deepak.joshi@example.com");
        p8.setPolicyType("Health");
        p8.setCustomerSinceDate(LocalDate.of(2024, 10, 29));
        p8.setStartDate(LocalDate.of(2024, 10, 29));
        p8.setExpiryDate(LocalDate.of(2026, 10, 29));
        p8.setPremiumAmount(new BigDecimal("22000.00"));
        p8.setCoverageDurationValue(24);
        p8.setCoverageDurationUnit("MONTHS");
        p8.setProductName("Family Health Optima Multi-Year");
        p8.setReferenceInsurer("Star Health and Allied Insurance");
        p8.setNotes("Demo Scenario 8: Two-year health policy that expires today (29 October 2026).");
        list.add(p8);

        // 9. POL-1009 — Life Insurance (Long-term policy)
        Policy p9 = new Policy();
        p9.setPolicyNumber("POL-1009");
        p9.setCustomerName("Kavita Reddy");
        p9.setCustomerEmail("kavita.reddy@example.com");
        p9.setPolicyType("Life");
        p9.setCustomerSinceDate(LocalDate.of(2026, 1, 1));
        p9.setStartDate(LocalDate.of(2026, 1, 1));
        p9.setExpiryDate(LocalDate.of(2029, 1, 1));
        p9.setPremiumAmount(new BigDecimal("25000.00"));
        p9.setCoverageDurationValue(36);
        p9.setCoverageDurationUnit("MONTHS");
        p9.setProductName("Tech Term Multi-Year Life Cover");
        p9.setReferenceInsurer("Life Insurance Corporation of India (LIC)");
        p9.setNotes("Demo Scenario 9: Long-term 3-year term life insurance policy (Expiring Jan 2029).");
        list.add(p9);

        // 10. POL-1010 — Home & Property (~29 days remaining, mapped to 'Home')
        Policy p10 = new Policy();
        p10.setPolicyNumber("POL-1010");
        p10.setCustomerName("Suresh Mehta");
        p10.setCustomerEmail("suresh.mehta@example.com");
        p10.setPolicyType("Home");
        p10.setCustomerSinceDate(LocalDate.of(2025, 11, 10));
        p10.setStartDate(LocalDate.of(2025, 11, 10));
        p10.setExpiryDate(LocalDate.of(2026, 11, 27));
        p10.setPremiumAmount(new BigDecimal("7500.00"));
        p10.setCoverageDurationValue(383);
        p10.setCoverageDurationUnit("DAYS");
        p10.setProductName("Bharat Griha Raksha Standard Cover");
        p10.setReferenceInsurer("New India Assurance");
        p10.setNotes("Demo Scenario 10: Home & Property with approximately 29 days remaining.");
        list.add(p10);

        return list;
    }
}

