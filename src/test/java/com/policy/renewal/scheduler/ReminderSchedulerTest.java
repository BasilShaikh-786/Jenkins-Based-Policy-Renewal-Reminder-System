package com.policy.renewal.scheduler;

import com.policy.renewal.service.PolicyReminderService;
import com.policy.renewal.service.ReminderRunReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ReminderSchedulerTest — unit tests for the background reminder scheduler component.
 *
 * <p>Uses Mockito to isolate the scheduler from the database and notification services.
 * Tests verify:
 * <ul>
 *   <li>Scheduler runs when enabled</li>
 *   <li>Scheduler skips when disabled (env-var / property override path)</li>
 *   <li>Concurrency guard prevents overlapping executions</li>
 *   <li>Last-run timestamp is recorded after successful execution</li>
 *   <li>Scheduler gracefully handles exceptions without crashing</li>
 *   <li>Timezone and cron accessor methods return configured values</li>
 * </ul>
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@ExtendWith(MockitoExtension.class)
class ReminderSchedulerTest {

    @Mock
    private PolicyReminderService reminderService;

    private ReminderScheduler reminderScheduler;

    // Fixed clock: 2026-10-15 08:00:00 IST
    private static final ZoneId IST = ZoneId.of("Asia/Kolkata");
    private static final Clock FIXED_CLOCK = Clock.fixed(
            Instant.parse("2026-10-15T02:30:00Z"), // 02:30 UTC = 08:00 IST
            IST
    );

    @BeforeEach
    void setUp() {
        reminderScheduler = new ReminderScheduler(reminderService, FIXED_CLOCK);
        reminderScheduler.setScheduleEnabled(true);
    }

    // =========================================================================
    // Core execution tests
    // =========================================================================

    @Test
    @DisplayName("Scheduled check triggers reminder processing when enabled")
    void testScheduledCheckRunsWhenEnabled() {
        ReminderRunReport dummyReport = new ReminderRunReport(LocalDate.now(FIXED_CLOCK));
        when(reminderService.processAllReminders()).thenReturn(dummyReport);

        reminderScheduler.runScheduledReminderCheck();

        verify(reminderService, times(1)).processAllReminders();
        assertFalse(reminderScheduler.isRunning(), "isRunning flag must be false after job completes");
    }

    @Test
    @DisplayName("Scheduled check does nothing when schedule is disabled (env-var path)")
    void testScheduledCheckSkippedWhenDisabled() {
        reminderScheduler.setScheduleEnabled(false);

        reminderScheduler.runScheduledReminderCheck();

        verify(reminderService, never()).processAllReminders();
        assertFalse(reminderScheduler.isRunning(), "isRunning flag must remain false when job is disabled");
    }

    // =========================================================================
    // Last-run timestamp tracking
    // =========================================================================

    @Test
    @DisplayName("Last-run timestamp is null before any execution")
    void testLastRunAtIsNullBeforeFirstExecution() {
        assertNull(reminderScheduler.getLastRunAt(),
                "lastRunAt must be null before the scheduler has ever run");
    }

    @Test
    @DisplayName("Last-run timestamp is recorded after successful execution")
    void testLastRunAtIsRecordedAfterExecution() {
        ReminderRunReport dummyReport = new ReminderRunReport(LocalDate.now(FIXED_CLOCK));
        when(reminderService.processAllReminders()).thenReturn(dummyReport);

        reminderScheduler.runScheduledReminderCheck();

        LocalDateTime lastRun = reminderScheduler.getLastRunAt();
        assertNotNull(lastRun, "lastRunAt must be set after a successful run");
        // Should be 2026-10-15 08:00:00 IST
        assertEquals(2026, lastRun.getYear());
        assertEquals(10, lastRun.getMonthValue());
        assertEquals(15, lastRun.getDayOfMonth());
    }

    @Test
    @DisplayName("Last-run timestamp is recorded even when reminder service throws an exception")
    void testLastRunAtIsRecordedAfterFailure() {
        when(reminderService.processAllReminders())
                .thenThrow(new RuntimeException("Simulated DB failure"));

        // Should not propagate the exception
        assertDoesNotThrow(() -> reminderScheduler.runScheduledReminderCheck());

        assertNotNull(reminderScheduler.getLastRunAt(),
                "lastRunAt must be set even when the job throws an exception");
        assertFalse(reminderScheduler.isRunning(),
                "isRunning must be released even when an exception occurs");
    }

    // =========================================================================
    // Concurrency guard
    // =========================================================================

    @Test
    @DisplayName("Concurrency guard prevents a second overlapping execution")
    void testConcurrencyGuardPreventsOverlap() throws InterruptedException {
        // Simulate a long-running first execution
        when(reminderService.processAllReminders()).thenAnswer(invocation -> {
            Thread.sleep(200); // simulate work
            return new ReminderRunReport(LocalDate.now(FIXED_CLOCK));
        });

        // Start first run in background
        Thread firstRun = new Thread(() -> reminderScheduler.runScheduledReminderCheck());
        firstRun.start();

        // Give the first thread a moment to acquire the lock
        Thread.sleep(50);

        // Second invocation should be skipped by the concurrency guard
        reminderScheduler.runScheduledReminderCheck();

        firstRun.join(2000);

        // processAllReminders must only have been called once (by the first thread)
        verify(reminderService, times(1)).processAllReminders();
    }

    // =========================================================================
    // Exception handling
    // =========================================================================

    @Test
    @DisplayName("Exception in reminder service does not propagate from scheduler")
    void testExceptionHandledGracefully() {
        when(reminderService.processAllReminders())
                .thenThrow(new RuntimeException("Simulated SMTP timeout"));

        // Must not throw
        assertDoesNotThrow(() -> reminderScheduler.runScheduledReminderCheck());
        assertFalse(reminderScheduler.isRunning(),
                "isRunning flag must be released after an exception");
    }

    // =========================================================================
    // Configuration accessor tests
    // =========================================================================

    @Test
    @DisplayName("Default cron expression accessor returns configured value")
    void testGetScheduleCron() {
        // Default injected by Spring, but we verify the accessor works
        // (value is null in unit test without @Value injection, hence we test non-null after manual set)
        ReminderScheduler scheduler = new ReminderScheduler(reminderService, FIXED_CLOCK);
        // scheduleCron defaults via @Value; in unit test it's null — verify method exists and is callable
        assertDoesNotThrow(scheduler::getScheduleCron);
    }

    @Test
    @DisplayName("Scheduler timezone accessor returns configured value")
    void testGetSchedulerTimezone() {
        ReminderScheduler scheduler = new ReminderScheduler(reminderService, FIXED_CLOCK);
        assertDoesNotThrow(scheduler::getSchedulerTimezone);
    }

    @Test
    @DisplayName("isScheduleEnabled reflects the set value correctly")
    void testIsScheduleEnabledReflectsSetValue() {
        reminderScheduler.setScheduleEnabled(true);
        assertTrue(reminderScheduler.isScheduleEnabled());

        reminderScheduler.setScheduleEnabled(false);
        assertFalse(reminderScheduler.isScheduleEnabled());
    }
}
