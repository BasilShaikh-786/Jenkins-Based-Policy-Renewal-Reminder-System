package com.policy.renewal.controller;

import com.policy.renewal.model.AuditLog;
import com.policy.renewal.service.AuditLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * AuditLogController — administrative view controller for inspecting system audit events (Milestone 5).
 *
 * <p>Access rule: Strictly restricted to users with the {@code ADMIN} role.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Controller
@RequestMapping("/audit")
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /**
     * Displays recent system audit logs with optional filtering by resource category.
     *
     * @param type  optional resource category filter (POLICY, USER, DATABASE, SECURITY)
     * @param model view model
     * @return Thymeleaf view name
     */
    @GetMapping
    public String viewAuditLogs(@RequestParam(name = "type", required = false) String type, Model model) {
        List<AuditLog> logs;
        if (type != null && !type.isBlank() && !"ALL".equalsIgnoreCase(type)) {
            logs = auditLogService.getLogsByResourceType(type);
        } else {
            logs = auditLogService.getRecentAuditLogs();
        }

        model.addAttribute("logs", logs);
        model.addAttribute("selectedType", type != null ? type.toUpperCase() : "ALL");
        model.addAttribute("totalLogsCount", logs.size());
        model.addAttribute("activeTab", "audit");

        return "audit/list";
    }
}
