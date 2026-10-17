package com.policy.renewal.scheduler;

import com.policy.renewal.PolicyRenewalApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SchedulingConfigurationTest — integration tests that verify the scheduling
 * configuration loaded from {@code src/test/resources/application.properties}.
 *
 * <p>These tests guard against:
 * <ul>
 *   <li>Scheduling being accidentally enabled during automated test runs</li>
 *   <li>The production file-based database being used instead of the test in-memory DB</li>
 *   <li>An invalid or missing timezone causing startup failures</li>
 *   <li>The Clock bean using the correct Asia/Kolkata timezone</li>
 *   <li>Env-var override documentation (tested via property binding)</li>
 * </ul>
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@SpringBootTest
class SchedulingConfigurationTest {

    @Autowired
    private ReminderScheduler reminderScheduler;

    @Autowired
    private Clock clock;

    @Value("${reminder.schedule.enabled:false}")
    private boolean scheduleEnabled;

    @Value("${scheduler.timezone:Asia/Kolkata}")
    private String schedulerTimezone;

    @Value("${reminder.schedule.cron:0 0 8 * * ?}")
    private String scheduleCron;

    // =========================================================================
    // Test environment safety checks
    // =========================================================================

    @Test
    @DisplayName("Scheduling is DISABLED in the test environment to prevent accidental execution")
    void testSchedulingIsDisabledInTests() {
        assertFalse(scheduleEnabled,
                "reminder.schedule.enabled must be false in the test environment. "
                + "Check src/test/resources/application.properties.");
        assertFalse(reminderScheduler.isScheduleEnabled(),
                "ReminderScheduler.isScheduleEnabled() must return false in tests.");
    }

    @Test
    @DisplayName("Scheduler has not run automatically during application context startup")
    void testSchedulerHasNotRunDuringStartup() {
        assertNull(reminderScheduler.getLastRunAt(),
                "Scheduler must not have executed during context startup — "
                + "check that reminder.schedule.enabled=false in test properties.");
    }

    // =========================================================================
    // Timezone configuration
    // =========================================================================

    @Test
    @DisplayName("scheduler.timezone property is set to Asia/Kolkata in test configuration")
    void testTimezonePropertyIsAsiKolkata() {
        assertEquals("Asia/Kolkata", schedulerTimezone,
                "scheduler.timezone must be Asia/Kolkata. "
                + "Check src/test/resources/application.properties.");
    }

    @Test
    @DisplayName("Clock bean uses Asia/Kolkata timezone")
    void testClockBeanUsesCorrectTimezone() {
        assertNotNull(clock, "Clock bean must be present in the application context");
        ZoneId clockZone = clock.getZone();
        assertEquals(ZoneId.of("Asia/Kolkata"), clockZone,
                "Clock bean must use ZoneId.of('Asia/Kolkata'). Actual zone: " + clockZone);
    }

    @Test
    @DisplayName("resolveZone falls back to Asia/Kolkata for invalid timezone strings")
    void testResolveZoneFallbackForInvalidTimezone() {
        ZoneId result = PolicyRenewalApplication.resolveZone("INVALID/ZONE");
        assertEquals(ZoneId.of("Asia/Kolkata"), result,
                "resolveZone must fall back to Asia/Kolkata for invalid timezone strings.");
    }

    @Test
    @DisplayName("resolveZone falls back to Asia/Kolkata for null input")
    void testResolveZoneFallbackForNull() {
        ZoneId result = PolicyRenewalApplication.resolveZone(null);
        assertEquals(ZoneId.of("Asia/Kolkata"), result,
                "resolveZone must fall back to Asia/Kolkata for null input.");
    }

    @Test
    @DisplayName("resolveZone correctly parses a valid timezone string")
    void testResolveZoneValidTimezone() {
        ZoneId result = PolicyRenewalApplication.resolveZone("UTC");
        assertEquals(ZoneId.of("UTC"), result);

        ZoneId ist = PolicyRenewalApplication.resolveZone("Asia/Kolkata");
        assertEquals(ZoneId.of("Asia/Kolkata"), ist);
    }

    // =========================================================================
    // Cron expression
    // =========================================================================

    @Test
    @DisplayName("Default cron expression matches the 08:00 AM daily schedule")
    void testDefaultCronExpression() {
        assertNotNull(scheduleCron, "reminder.schedule.cron must not be null");
        assertFalse(scheduleCron.isBlank(), "reminder.schedule.cron must not be blank");
        // Default is "0 0 8 * * ?" — 08:00 AM every day
        assertEquals("0 0 8 * * ?", scheduleCron,
                "Default cron expression must be '0 0 8 * * ?' (08:00 AM daily). "
                + "Actual: " + scheduleCron);
    }

    // =========================================================================
    // Env-var override documentation test
    // =========================================================================

    @Test
    @DisplayName("Scheduler can be enabled programmatically (simulates env-var REMINDER_SCHEDULE_ENABLED=true)")
    void testSchedulerCanBeEnabledProgrammatically() {
        // This simulates what happens when REMINDER_SCHEDULE_ENABLED=true env var is set.
        // The property binding maps the env var to reminder.schedule.enabled automatically.
        // We test via the setter to verify the enabled/disabled state changes correctly.
        boolean originalState = reminderScheduler.isScheduleEnabled();
        try {
            reminderScheduler.setScheduleEnabled(true);
            assertTrue(reminderScheduler.isScheduleEnabled(),
                    "Scheduler must report enabled after setScheduleEnabled(true)");
        } finally {
            // Restore original state so other tests are not affected
            reminderScheduler.setScheduleEnabled(originalState);
        }
    }
}
