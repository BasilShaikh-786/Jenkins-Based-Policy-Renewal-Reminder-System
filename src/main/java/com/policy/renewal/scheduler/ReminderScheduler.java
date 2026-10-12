package com.policy.renewal.scheduler;

import com.policy.renewal.service.PolicyReminderService;
import com.policy.renewal.service.ReminderRunReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ReminderScheduler — automated background scheduling component for policy reminder checks.
 *
 * <p>Implements Requirement D: Scheduled processing. Keeps scheduling concern strictly
 * decoupled from the business logic encapsulated in {@link PolicyReminderService}.
 *
 * <p>Protections:
 * <ul>
 *   <li>Configurable cron expression via {@code reminder.schedule.cron}</li>
 *   <li>Master switch via {@code reminder.schedule.enabled}</li>
 *   <li>Concurrency lock via {@code AtomicBoolean} prevents duplicate executions if a job overruns</li>
 * </ul>
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Component
public class ReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);

    private final PolicyReminderService reminderService;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    @Value("${reminder.schedule.enabled:true}")
    private boolean scheduleEnabled = true;

    public ReminderScheduler(PolicyReminderService reminderService) {
        this.reminderService = reminderService;
    }

    /**
     * Executes the daily reminder check according to the configured cron schedule.
     */
    @Scheduled(cron = "${reminder.schedule.cron:0 0 8 * * ?}")
    public void runScheduledReminderCheck() {
        if (!scheduleEnabled) {
            log.info("[SCHEDULER] Automated reminder job is currently disabled (reminder.schedule.enabled=false).");
            return;
        }

        if (!isRunning.compareAndSet(false, true)) {
            log.warn("[SCHEDULER] An existing reminder job execution is still in progress. Skipping duplicate run.");
            return;
        }

        try {
            log.info("[SCHEDULER] Commencing scheduled policy expiry reminder evaluation...");
            ReminderRunReport report = reminderService.processAllReminders();
            log.info("[SCHEDULER] Scheduled reminder run concluded: {}", report.getSummary());
        } catch (Exception ex) {
            log.error("[SCHEDULER] Unexpected failure during scheduled reminder job: {}", ex.getMessage(), ex);
        } finally {
            isRunning.set(false);
        }
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public boolean isScheduleEnabled() {
        return scheduleEnabled;
    }

    public void setScheduleEnabled(boolean scheduleEnabled) {
        this.scheduleEnabled = scheduleEnabled;
    }
}
