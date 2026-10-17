package com.policy.renewal.controller;

import com.policy.renewal.model.Policy;
import com.policy.renewal.service.PolicyReminderService;
import com.policy.renewal.service.PolicyService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * HomeController — handles navigation and data rendering for the application dashboard.
 *
 * <p>Implements FR-12 (Dashboard Metrics & Overview) and route #1 from the API design.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Controller
public class HomeController {

    private final PolicyService policyService;
    private final PolicyReminderService reminderService;

    public HomeController(PolicyService policyService, PolicyReminderService reminderService) {
        this.policyService = policyService;
        this.reminderService = reminderService;
    }

    /**
     * Renders the main dashboard page.
     * Computes live statistics across all policies and highlights imminent renewals.
     *
     * @param window optional filter for upcoming renewal days (e.g. 7 or 30)
     * @param model  Spring MVC model to pass attributes to the view
     * @return view name "index"
     */
    @GetMapping("/")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public String home(@RequestParam(name = "window", required = false) Integer window, Model model) {
        Map<String, Long> stats = policyService.getDashboardStats();

        List<Policy> upcomingRenewals = (window != null && window > 0)
                ? policyService.getUpcomingRenewals(window)
                : policyService.getUpcomingRenewals();

        List<Policy> allPolicies = policyService.getAllPolicies();

        // Limit recent policies preview to top 5
        List<Policy> recentPolicies = allPolicies.stream()
                .limit(5)
                .toList();

        model.addAttribute("stats", stats);
        model.addAttribute("upcomingRenewals", upcomingRenewals);
        model.addAttribute("recentPolicies", recentPolicies);
        model.addAttribute("policyService", policyService);
        model.addAttribute("alertDays", policyService.getRenewalAlertDays());
        model.addAttribute("selectedWindow", window);
        model.addAttribute("reminderWindows", reminderService != null ? reminderService.getConfiguredWindows() : List.of(30, 7, 1));
        model.addAttribute("recentReminders", reminderService != null ? reminderService.getRecentReminders() : List.of());

        return "index";
    }
}
