package com.policy.renewal.controller;

import com.policy.renewal.dto.BackupInfo;
import com.policy.renewal.dto.BackupVerificationResult;
import com.policy.renewal.service.DatabaseBackupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * BackupController — administrative interface and CI/CD REST endpoints for database backup & recovery (Milestone 5).
 *
 * <p>Access rule: Strictly restricted to users with the {@code ADMIN} role.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Controller
@PreAuthorize("hasRole('ADMIN')")
public class BackupController {

    private final DatabaseBackupService backupService;

    public BackupController(DatabaseBackupService backupService) {
        this.backupService = backupService;
    }

    /**
     * Renders the database backup and recovery console.
     */
    @GetMapping("/backups")
    public String listBackups(Model model) {
        List<BackupInfo> backups = backupService.listBackups();
        model.addAttribute("backups", backups);
        model.addAttribute("totalBackups", backups.size());
        model.addAttribute("activeTab", "backups");
        return "backups/list";
    }

    /**
     * Web action: Triggers an immediate, live compressed database backup.
     */
    @PostMapping("/backups/create")
    public String createBackup(RedirectAttributes redirectAttributes) {
        try {
            BackupInfo info = backupService.createBackup();
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Database backup archive created successfully: " + info.getFileName() + " (" + info.getFormattedSize() + ")");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Failed to create database backup: " + e.getMessage());
        }
        return "redirect:/backups";
    }

    /**
     * Web action: Executes safe recovery verification by restoring the backup archive into an isolated in-memory DB.
     */
    @PostMapping("/backups/{fileName}/verify")
    public String verifyBackup(@PathVariable String fileName, RedirectAttributes redirectAttributes) {
        try {
            BackupVerificationResult result = backupService.verifyBackup(fileName);
            if (result.isValid()) {
                redirectAttributes.addFlashAttribute("verificationSuccess", result);
                redirectAttributes.addFlashAttribute("successMessage", result.getMessage());
            } else {
                redirectAttributes.addFlashAttribute("verificationFailure", result);
                redirectAttributes.addFlashAttribute("errorMessage", result.getMessage());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Verification failed: " + e.getMessage());
        }
        return "redirect:/backups";
    }

    /**
     * Web action: Deletes a specific backup archive.
     */
    @PostMapping("/backups/{fileName}/delete")
    public String deleteBackup(@PathVariable String fileName, RedirectAttributes redirectAttributes) {
        boolean deleted = backupService.deleteBackup(fileName);
        if (deleted) {
            redirectAttributes.addFlashAttribute("successMessage", "Backup archive '" + fileName + "' was deleted.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete backup archive '" + fileName + "'.");
        }
        return "redirect:/backups";
    }

    // =========================================================================
    // Programmatic REST API Endpoints (for Jenkins CI/CD & Automated DevOps)
    // Protected by HTTP Basic Auth with ADMIN role; CSRF-exempted under /api/**
    // =========================================================================

    /**
     * REST endpoint to trigger an automated database backup from a Jenkins pipeline stage.
     */
    @PostMapping("/api/backups/create")
    @ResponseBody
    public ResponseEntity<BackupInfo> apiCreateBackup() {
        BackupInfo info = backupService.createBackup();
        return ResponseEntity.ok(info);
    }

    /**
     * REST endpoint to verify a database backup from a Jenkins pipeline stage.
     */
    @PostMapping("/api/backups/{fileName}/verify")
    @ResponseBody
    public ResponseEntity<BackupVerificationResult> apiVerifyBackup(@PathVariable String fileName) {
        BackupVerificationResult result = backupService.verifyBackup(fileName);
        if (result.isValid()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}
