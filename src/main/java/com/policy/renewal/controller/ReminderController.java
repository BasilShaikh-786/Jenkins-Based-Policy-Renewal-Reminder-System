package com.policy.renewal.controller;

import com.policy.renewal.model.ReminderRecord;
import com.policy.renewal.service.PolicyReminderService;
import com.policy.renewal.service.ReminderRunReport;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ReminderController — handles web navigation and manual triggers for the reminder engine.
 *
 * <p>Provides:
 * <ul>
 *   <li>Audit view of all reminder attempts at {@code GET /reminders}</li>
 *   <li>Interactive web trigger at {@code POST /reminders/trigger}</li>
 *   <li>Programmatic REST trigger for Jenkins CI/CD pipeline at {@code POST /api/reminders/trigger}</li>
 * </ul>
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class ReminderController {

    private final PolicyReminderService reminderService;

    public ReminderController(PolicyReminderService reminderService) {
        this.reminderService = reminderService;
    }

    /**
     * Renders the reminder log audit trail page.
     * Route: GET /reminders
     */
    @GetMapping("/reminders")
    public String listReminders(Model model) {
        List<ReminderRecord> records = reminderService.getRecentReminders();
        Map<String, Long> stats = reminderService.getReminderStats();
        List<Integer> windows = reminderService.getConfiguredWindows();

        model.addAttribute("reminders", records);
        model.addAttribute("stats", stats);
        model.addAttribute("configuredWindows", windows);

        return "reminders/list";
    }

    /**
     * Interactive trigger from web interface (Dashboard or Reminders page).
     * Route: POST /reminders/trigger
     */
    @PostMapping("/reminders/trigger")
    public String triggerRemindersManually(
            @RequestParam(name = "redirect", required = false, defaultValue = "/") String redirect,
            RedirectAttributes redirectAttributes) {

        ReminderRunReport report = reminderService.processAllReminders();

        redirectAttributes.addFlashAttribute("successMessage",
                "Reminder evaluation executed! " + report.getSummary());

        return "redirect:" + (redirect.startsWith("/") ? redirect : "/");
    }

    /**
     * Programmatic REST endpoint for automated triggering by Jenkins CI/CD or CLI tools.
     * Route: POST /api/reminders/trigger
     */
    @PostMapping("/api/reminders/trigger")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> triggerRemindersApi() {
        ReminderRunReport report = reminderService.processAllReminders();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("evaluationDate", report.getEvaluationDate().toString());
        response.put("eligibleCount", report.getEligibleCount());
        response.put("simulatedCount", report.getSimulatedCount());
        response.put("sentCount", report.getSentCount());
        response.put("failedCount", report.getFailedCount());
        response.put("duplicateSkippedCount", report.getDuplicateSkippedCount());
        response.put("summary", report.getSummary());

        return ResponseEntity.ok(response);
    }
}
