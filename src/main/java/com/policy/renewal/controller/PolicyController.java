package com.policy.renewal.controller;

import com.policy.renewal.model.Policy;
import com.policy.renewal.service.PolicyService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * PolicyController — manages all CRUD and renewal workflow operations for Policy records.
 *
 * <p>Implements routes 2 through 9 defined in docs/week-03/api-and-route-design.md.
 * Follows the POST/Redirect/GET pattern for form submissions and flash message handling.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Controller
@RequestMapping("/policies")
public class PolicyController {

    private final PolicyService policyService;

    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    /**
     * Lists policies with optional text search and derived status filtering.
     * Route: GET /policies
     */
    @GetMapping
    public String listPolicies(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status,
            Model model) {

        List<Policy> results = (search != null && !search.isBlank())
                ? policyService.search(search.trim())
                : policyService.getAllPolicies();

        if (status != null && !status.isBlank() && !status.equalsIgnoreCase("ALL")) {
            results = results.stream()
                    .filter(p -> policyService.getDerivedStatus(p).equalsIgnoreCase(status))
                    .toList();
        }

        model.addAttribute("policies", results);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("policyService", policyService);

        return "policies/list";
    }

    /**
     * Displays an empty form for creating a new policy.
     * Route: GET /policies/new
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("policy")) {
            model.addAttribute("policy", new Policy());
        }
        model.addAttribute("isEdit", false);
        return "policies/form";
    }

    /**
     * Handles submission of a new policy form.
     * Route: POST /policies
     */
    @PostMapping
    public String createPolicy(
            @Valid @ModelAttribute("policy") Policy policy,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "policies/form";
        }

        try {
            Policy saved = policyService.save(policy);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Policy " + saved.getPolicyNumber() + " created successfully!");
            return "redirect:/policies/" + saved.getId();
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("isEdit", false);
            return "policies/form";
        }
    }

    /**
     * Displays full details of an individual policy.
     * Route: GET /policies/{id}
     */
    @GetMapping("/{id}")
    public String viewPolicy(@PathVariable("id") Long id, Model model) {
        Optional<Policy> policyOpt = policyService.findById(id);
        if (policyOpt.isEmpty()) {
            return "error/404";
        }

        Policy policy = policyOpt.get();
        model.addAttribute("policy", policy);
        model.addAttribute("derivedStatus", policyService.getDerivedStatus(policy));
        model.addAttribute("policyService", policyService);

        return "policies/detail";
    }

    /**
     * Displays a pre-populated form to edit an existing policy.
     * Route: GET /policies/{id}/edit
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Optional<Policy> policyOpt = policyService.findById(id);
        if (policyOpt.isEmpty()) {
            return "error/404";
        }

        model.addAttribute("policy", policyOpt.get());
        model.addAttribute("isEdit", true);
        return "policies/form";
    }

    /**
     * Handles submission of the edited policy form.
     * Route: POST /policies/{id}
     */
    @PostMapping("/{id}")
    public String updatePolicy(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("policy") Policy policy,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        policy.setId(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "policies/form";
        }

        try {
            Policy saved = policyService.save(policy);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Policy " + saved.getPolicyNumber() + " updated successfully!");
            return "redirect:/policies/" + saved.getId();
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("isEdit", true);
            return "policies/form";
        }
    }

    /**
     * Marks a policy as Renewed (FR-11).
     * Route: POST /policies/{id}/renew
     */
    @PostMapping("/{id}/renew")
    public String markAsRenewed(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes) {

        try {
            policyService.markAsRenewed(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Policy marked as RENEWED successfully!");
            return "redirect:/policies/" + id;
        } catch (IllegalArgumentException ex) {
            return "error/404";
        }
    }

    /**
     * Displays policies with upcoming renewal dates within the alert window (FR-09).
     * Route: GET /policies/upcoming
     */
    @GetMapping("/upcoming")
    public String upcomingRenewals(Model model) {
        List<Policy> upcoming = policyService.getUpcomingRenewals();
        model.addAttribute("upcomingPolicies", upcoming);
        model.addAttribute("alertDays", policyService.getRenewalAlertDays());
        model.addAttribute("policyService", policyService);

        return "policies/upcoming";
    }
}
