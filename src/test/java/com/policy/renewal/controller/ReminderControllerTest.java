package com.policy.renewal.controller;

import com.policy.renewal.service.PolicyReminderService;
import com.policy.renewal.service.ReminderRunReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ReminderControllerTest — web and API endpoint unit tests for reminder actions.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@WebMvcTest(ReminderController.class)
class ReminderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolicyReminderService reminderService;

    @Test
    @DisplayName("GET /reminders renders reminders/list view with stats and configured windows")
    void testListReminders() throws Exception {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", 0L);
        stats.put("simulated", 0L);
        stats.put("sent", 0L);
        stats.put("failed", 0L);

        when(reminderService.getRecentReminders()).thenReturn(Collections.emptyList());
        when(reminderService.getReminderStats()).thenReturn(stats);
        when(reminderService.getConfiguredWindows()).thenReturn(List.of(30, 7, 1));

        mockMvc.perform(get("/reminders"))
                .andExpect(status().isOk())
                .andExpect(view().name("reminders/list"))
                .andExpect(model().attributeExists("reminders"))
                .andExpect(model().attributeExists("stats"))
                .andExpect(model().attributeExists("configuredWindows"));
    }

    @Test
    @DisplayName("POST /reminders/trigger executes processing and redirects with flash message")
    void testTriggerRemindersPost() throws Exception {
        ReminderRunReport report = new ReminderRunReport(LocalDate.now());
        when(reminderService.processAllReminders()).thenReturn(report);

        mockMvc.perform(post("/reminders/trigger")
                        .param("redirect", "/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(reminderService, times(1)).processAllReminders();
    }

    @Test
    @DisplayName("POST /api/reminders/trigger returns 200 OK JSON response for CI/CD pipelines")
    void testTriggerRemindersApi() throws Exception {
        ReminderRunReport report = new ReminderRunReport(LocalDate.of(2026, 10, 15));
        when(reminderService.processAllReminders()).thenReturn(report);

        mockMvc.perform(post("/api/reminders/trigger")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.eligibleCount").value(0))
                .andExpect(jsonPath("$.evaluationDate").value("2026-10-15"));

        verify(reminderService, times(1)).processAllReminders();
    }
}
