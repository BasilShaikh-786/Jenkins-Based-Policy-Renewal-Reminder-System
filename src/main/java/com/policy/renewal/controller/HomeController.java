package com.policy.renewal.controller;

import com.policy.renewal.model.Policy;
import com.policy.renewal.service.PolicyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    public HomeController(PolicyService policyService) {
        this.policyService = policyService;
    }

    /**
     * Renders the main dashboard page.
     * Computes live statistics across all policies and highlights imminent renewals.
     *
     * @param model Spring MVC model to pass attributes to the view
     * @return view name "index"
     */
    @GetMapping("/")
    public String home(Model model) {
        Map<String, Long> stats = policyService.getDashboardStats();
        List<Policy> upcomingRenewals = policyService.getUpcomingRenewals();
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

        return "index";
    }
}
