package com.policy.renewal.scheduler;

import com.policy.renewal.service.PolicyReminderService;
import com.policy.renewal.service.ReminderRunReport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ReminderSchedulerTest — unit tests for the background reminder scheduler component.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@ExtendWith(MockitoExtension.class)
class ReminderSchedulerTest {

    @Mock
    private PolicyReminderService reminderService;

    @InjectMocks
    private ReminderScheduler reminderScheduler;

    @BeforeEach
    void setUp() {
        reminderScheduler.setScheduleEnabled(true);
    }

    @Test
    @DisplayName("Scheduled check triggers reminder processing when enabled")
    void testScheduledCheckRunsWhenEnabled() {
        ReminderRunReport dummyReport = new ReminderRunReport(LocalDate.now());
        when(reminderService.processAllReminders()).thenReturn(dummyReport);

        reminderScheduler.runScheduledReminderCheck();

        verify(reminderService, times(1)).processAllReminders();
        assertFalse(reminderScheduler.isRunning());
    }

    @Test
    @DisplayName("Scheduled check does nothing when schedule is disabled")
    void testScheduledCheckSkippedWhenDisabled() {
        reminderScheduler.setScheduleEnabled(false);

        reminderScheduler.runScheduledReminderCheck();

        verify(reminderService, never()).processAllReminders();
    }
}
