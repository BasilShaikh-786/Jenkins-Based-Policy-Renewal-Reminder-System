package com.policy.renewal.scheduler;

import com.policy.renewal.service.PolicyReminderService;
import com.policy.renewal.service.ReminderRunReport;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ReminderScheduler — production-ready automated background scheduling component
 * for daily policy renewal reminder checks.
 *
 * <p>Implements Requirement D: Scheduled processing. Keeps scheduling concern strictly
 * decoupled from the business logic encapsulated in {@link PolicyReminderService}.
 *
 * <p>Protections:
 * <ul>
 *   <li>Configurable cron expression via {@code reminder.schedule.cron}
 *       (override with env var {@code REMINDER_SCHEDULE_CRON})</li>
 *   <li>Master on/off switch via {@code reminder.schedule.enabled}
 *       (override with env var {@code REMINDER_SCHEDULE_ENABLED})</li>
 *   <li>Timezone configured via {@code scheduler.timezone}
 *       (override with env var {@code SCHEDULER_TIMEZONE})</li>
 *   <li>Concurrency lock via {@code AtomicBoolean} prevents duplicate executions
 *       if a previous job overruns its scheduled slot</li>
 *   <li>Last-run timestamp tracked for observability and verification</li>
 * </ul>
 *
 * <p>Default cron: {@code 0 0 8 * * ?} — every day at 08:00 AM in the configured timezone.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Component
public class ReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);

    private final PolicyReminderService reminderService;
    private final Clock clock;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    /** Tracks when the last scheduled run completed (null if never run). */
    private volatile LocalDateTime lastRunAt;

    /**
     * Whether the automated scheduled job is active.
     * Bound to {@code reminder.schedule.enabled}, overridable by env var
     * {@code REMINDER_SCHEDULE_ENABLED} (Spring Boot maps env vars automatically:
     * REMINDER_SCHEDULE_ENABLED → reminder.schedule.enabled).
     */
    @Value("${reminder.schedule.enabled:false}")
    private boolean scheduleEnabled;

    /** Cron expression for the daily reminder job. Default: 08:00 AM every day. */
    @Value("${reminder.schedule.cron:0 0 8 * * ?}")
    private String scheduleCron;

    /** Configured timezone name for log messages. */
    @Value("${scheduler.timezone:Asia/Kolkata}")
    private String schedulerTimezone;

    public ReminderScheduler(PolicyReminderService reminderService, Clock clock) {
        this.reminderService = reminderService;
        this.clock = clock;
    }

    /**
     * Logs the scheduler configuration at application startup so operators can confirm
     * the correct timezone, cron expression, and enabled state without starting the app.
     */
    @PostConstruct
    public void logStartupConfiguration() {
        log.info("[SCHEDULER-CONFIG] Reminder Scheduler initialised.");
        log.info("[SCHEDULER-CONFIG] Enabled            : {}", scheduleEnabled);
        log.info("[SCHEDULER-CONFIG] Cron expression    : {}", scheduleCron);
        log.info("[SCHEDULER-CONFIG] Timezone           : {}", schedulerTimezone);
        log.info("[SCHEDULER-CONFIG] To enable: set reminder.schedule.enabled=true in application.properties");
        log.info("[SCHEDULER-CONFIG]            or export REMINDER_SCHEDULE_ENABLED=true before starting the JAR.");
        if (!scheduleEnabled) {
            log.info("[SCHEDULER-CONFIG] Scheduling is currently DISABLED. No automatic reminders will run.");
        }
    }

    /**
     * Executes the daily reminder check according to the configured cron schedule.
     *
     * <p>The cron is evaluated in the JVM's timezone; the Clock bean ensures date
     * evaluation uses the configured {@code scheduler.timezone}.
     *
     * <p>This method is safe to call manually (e.g. from tests or the REST trigger)
     * as it always respects the concurrency guard.
     */
    @Scheduled(cron = "${reminder.schedule.cron:0 0 8 * * ?}")
    public void runScheduledReminderCheck() {
        if (!scheduleEnabled) {
            log.info("[SCHEDULER] Automated reminder job is currently DISABLED (reminder.schedule.enabled=false). "
                    + "Set reminder.schedule.enabled=true or export REMINDER_SCHEDULE_ENABLED=true to activate.");
            return;
        }

        if (!isRunning.compareAndSet(false, true)) {
            log.warn("[SCHEDULER] An existing reminder job execution is still in progress. "
                    + "Skipping this trigger to prevent overlapping runs (concurrency guard active).");
            return;
        }

        try {
            LocalDateTime startedAt = LocalDateTime.now(clock);
            log.info("[SCHEDULER] ===== Daily Reminder Job STARTED =====");
            log.info("[SCHEDULER] Execution time ({}): {}", schedulerTimezone, startedAt);

            ReminderRunReport report = reminderService.processAllReminders();

            lastRunAt = LocalDateTime.now(clock);
            log.info("[SCHEDULER] ===== Daily Reminder Job COMPLETED =====");
            log.info("[SCHEDULER] Summary: {}", report.getSummary());
            log.info("[SCHEDULER] Completed at ({}): {}", schedulerTimezone, lastRunAt);

        } catch (Exception ex) {
            lastRunAt = LocalDateTime.now(clock);
            log.error("[SCHEDULER] Unexpected failure during scheduled reminder job. "
                    + "No customer data is exposed here. Error type: {}. Message: {}",
                    ex.getClass().getSimpleName(), ex.getMessage(), ex);
        } finally {
            isRunning.set(false);
        }
    }

    // =========================================================================
    // Accessors — used by tests and REST endpoints
    // =========================================================================

    /** @return true if the scheduled job is currently mid-execution */
    public boolean isRunning() {
        return isRunning.get();
    }

    /** @return true if automated scheduling is enabled */
    public boolean isScheduleEnabled() {
        return scheduleEnabled;
    }

    /** Sets the scheduling enabled flag (used by tests). */
    public void setScheduleEnabled(boolean scheduleEnabled) {
        this.scheduleEnabled = scheduleEnabled;
    }

    /** @return the timestamp of the most recent completed run, or null if never run */
    public LocalDateTime getLastRunAt() {
        return lastRunAt;
    }

    /** @return the configured cron expression */
    public String getScheduleCron() {
        return scheduleCron;
    }

    /** @return the configured timezone name */
    public String getSchedulerTimezone() {
        return schedulerTimezone;
    }
}
