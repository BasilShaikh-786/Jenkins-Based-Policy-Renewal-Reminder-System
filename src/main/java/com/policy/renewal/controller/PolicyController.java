package com.policy.renewal.controller;

import com.policy.renewal.model.Policy;
import com.policy.renewal.service.PolicyReminderService;
import com.policy.renewal.service.PolicyService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
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
    private PolicyReminderService reminderService;
    public PolicyController(PolicyService policyService) {
        this.policyService = policyService;
    }

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    public void setReminderService(PolicyReminderService reminderService) {
        this.reminderService = reminderService;
    }

    private void populatePeriodAttributes(Model model, Policy policy) {
        if (policy != null) {
            model.addAttribute("customerRelationshipPeriod", policyService.calculateCustomerRelationshipPeriod(policy));
        }
    }

    /**
     * Lists policies with optional text search, status filtering, and expiry date range filtering.
     * Route: GET /policies
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public String listPolicies(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "expiryFrom", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryFrom,
            @RequestParam(name = "expiryTo", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryTo,
            Model model) {

        List<Policy> results = (search != null && !search.isBlank())
                ? policyService.search(search.trim())
                : policyService.getAllPolicies();

        results = policyService.filterPolicies(results, status, expiryFrom, expiryTo);

        model.addAttribute("policies", results);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("expiryFrom", expiryFrom);
        model.addAttribute("expiryTo", expiryTo);
        model.addAttribute("policyService", policyService);

        return "policies/list";
    }

    /**
     * Displays an empty form for creating a new policy.
     * Route: GET /policies/new
     */
    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("policy")) {
            Policy policy = new Policy();
            policy.setCoverageDurationValue(12);
            policy.setCoverageDurationUnit("MONTHS");
            model.addAttribute("policy", policy);
        }
        model.addAttribute("isEdit", false);
        populatePeriodAttributes(model, (Policy) model.getAttribute("policy"));
        return "policies/form";
    }

    /**
     * Handles submission of a new policy form.
     * Route: POST /policies
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String createPolicy(
            @Valid @ModelAttribute("policy") Policy policy,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            populatePeriodAttributes(model, policy);
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
            populatePeriodAttributes(model, policy);
            return "policies/form";
        }
    }

    /**
     * Displays full details of an individual policy.
     * Route: GET /policies/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewPolicy(@PathVariable("id") Long id, Model model) {
        Optional<Policy> policyOpt = policyService.findById(id);
        if (policyOpt.isEmpty()) {
            return "error/404";
        }

        Policy policy = policyOpt.get();
        model.addAttribute("policy", policy);
        model.addAttribute("derivedStatus", policyService.getDerivedStatus(policy));
        model.addAttribute("customerRelationshipPeriod", policyService.calculateCustomerRelationshipPeriod(policy));
        model.addAttribute("policyService", policyService);
        model.addAttribute("reminderHistory", reminderService != null
                ? reminderService.getReminderHistoryForPolicy(id)
                : java.util.List.of());

        return "policies/detail";
    }

    /**
     * Displays a pre-populated form to edit an existing policy.
     * Route: GET /policies/{id}/edit
     */
    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Optional<Policy> policyOpt = policyService.findById(id);
        if (policyOpt.isEmpty()) {
            return "error/404";
        }

        Policy policy = policyOpt.get();
        model.addAttribute("policy", policy);
        model.addAttribute("isEdit", true);
        populatePeriodAttributes(model, policy);
        return "policies/form";
    }

    /**
     * Handles submission of the edited policy form.
     * Route: POST /policies/{id}
     */
    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updatePolicy(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("policy") Policy policy,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        policy.setId(id);

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            populatePeriodAttributes(model, policy);
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
            populatePeriodAttributes(model, policy);
            return "policies/form";
        }
    }

    /**
     * Marks a policy as Renewed (FR-11).
     * Route: POST /policies/{id}/renew
     */
    @PostMapping("/{id}/renew")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
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
     * Deletes an existing policy by ID.
     * Route: POST /policies/{id}/delete or DELETE /policies/{id}
     */
    @RequestMapping(value = {"/{id}/delete", "/{id}"}, method = {RequestMethod.POST, RequestMethod.DELETE})
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePolicy(
            @PathVariable("id") Long id,
            RedirectAttributes redirectAttributes) {

        try {
            Optional<Policy> policyOpt = policyService.findById(id);
            if (policyOpt.isEmpty()) {
                return "error/404";
            }
            String policyNum = policyOpt.get().getPolicyNumber();
            policyService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Policy " + policyNum + " deleted successfully!");
            return "redirect:/policies";
        } catch (IllegalArgumentException ex) {
            return "error/404";
        }
    }

    /**
     * Displays policies with upcoming renewal dates within the alert window (FR-09).
     * Route: GET /policies/upcoming
     */
    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public String upcomingRenewals(
            @RequestParam(name = "days", required = false) Integer days,
            Model model) {
        int windowDays = (days != null && days > 0) ? days : policyService.getRenewalAlertDays();
        List<Policy> upcoming = policyService.getUpcomingRenewals(windowDays);
        model.addAttribute("upcomingPolicies", upcoming);
        model.addAttribute("alertDays", windowDays);
        model.addAttribute("policyService", policyService);

        return "policies/upcoming";
    }
}
