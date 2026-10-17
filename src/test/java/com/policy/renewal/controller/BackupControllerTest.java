package com.policy.renewal.controller;

import com.policy.renewal.dto.BackupInfo;
import com.policy.renewal.dto.BackupVerificationResult;
import com.policy.renewal.security.SecurityConfig;
import com.policy.renewal.service.DatabaseBackupService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * BackupControllerTest — web layer unit tests for database backup and recovery endpoints.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@WebMvcTest(BackupController.class)
@Import({SecurityConfig.class, AuthController.class})
class BackupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatabaseBackupService backupService;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("GET /backups as ADMIN returns 200 OK and renders backups/list view")
    void testListBackupsAsAdmin() throws Exception {
        BackupInfo sample = new BackupInfo(
                "policyrenewaldb_backup_20261017.zip",
                "./data/backups/policyrenewaldb_backup_20261017.zip",
                2048,
                LocalDateTime.now(),
                "ZIP");
        when(backupService.listBackups()).thenReturn(List.of(sample));

        mockMvc.perform(get("/backups"))
                .andExpect(status().isOk())
                .andExpect(view().name("backups/list"))
                .andExpect(model().attributeExists("backups"))
                .andExpect(model().attribute("totalBackups", 1));

        verify(backupService, times(1)).listBackups();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST /backups/create as ADMIN triggers backup and redirects to /backups")
    void testCreateBackupAsAdmin() throws Exception {
        BackupInfo created = new BackupInfo(
                "policyrenewaldb_backup_20261017_150000.zip",
                "./data/backups/policyrenewaldb_backup_20261017_150000.zip",
                3072,
                LocalDateTime.now(),
                "ZIP");
        when(backupService.createBackup()).thenReturn(created);

        mockMvc.perform(post("/backups/create").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/backups"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(backupService, times(1)).createBackup();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST /backups/{fileName}/verify as ADMIN triggers recovery verification and redirects")
    void testVerifyBackupAsAdmin() throws Exception {
        BackupVerificationResult successResult = BackupVerificationResult.success(
                "test_backup.zip", 10, 2, 5, 12, "Verification passed");
        when(backupService.verifyBackup("test_backup.zip")).thenReturn(successResult);

        mockMvc.perform(post("/backups/test_backup.zip/verify").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/backups"))
                .andExpect(flash().attributeExists("verificationSuccess"));

        verify(backupService, times(1)).verifyBackup("test_backup.zip");
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST /backups/{fileName}/delete as ADMIN deletes archive and redirects")
    void testDeleteBackupAsAdmin() throws Exception {
        when(backupService.deleteBackup("old_backup.zip")).thenReturn(true);

        mockMvc.perform(post("/backups/old_backup.zip/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/backups"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(backupService, times(1)).deleteBackup("old_backup.zip");
    }

    @Test
    @WithMockUser(roles = {"STAFF"})
    @DisplayName("GET /backups as STAFF returns 403 Forbidden")
    void testListBackupsForbiddenForStaff() throws Exception {
        mockMvc.perform(get("/backups"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(backupService);
    }

    @Test
    @WithMockUser(roles = {"STAFF"})
    @DisplayName("POST /backups/create as STAFF returns 403 Forbidden")
    void testCreateBackupForbiddenForStaff() throws Exception {
        mockMvc.perform(post("/backups/create").with(csrf()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(backupService);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST /api/backups/create as ADMIN (CSRF exempt) returns 200 OK and BackupInfo JSON")
    void testApiCreateBackupAsAdmin() throws Exception {
        BackupInfo created = new BackupInfo(
                "ci_backup.zip", "./data/backups/ci_backup.zip", 4096, LocalDateTime.now(), "ZIP");
        when(backupService.createBackup()).thenReturn(created);

        mockMvc.perform(post("/api/backups/create"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName").value("ci_backup.zip"))
                .andExpect(jsonPath("$.backupType").value("ZIP"));

        verify(backupService, times(1)).createBackup();
    }

    @Test
    @DisplayName("POST /api/backups/create unauthenticated returns 401 Unauthorized")
    void testApiCreateBackupUnauthorizedForAnonymous() throws Exception {
        mockMvc.perform(post("/api/backups/create"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(backupService);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST /api/backups/{fileName}/verify as ADMIN returns 200 OK and verification report JSON")
    void testApiVerifyBackupAsAdmin() throws Exception {
        BackupVerificationResult res = BackupVerificationResult.success(
                "ci_backup.zip", 10, 2, 0, 4, "Recovery verification passed");
        when(backupService.verifyBackup("ci_backup.zip")).thenReturn(res);

        mockMvc.perform(post("/api/backups/ci_backup.zip/verify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.policyCount").value(10));

        verify(backupService, times(1)).verifyBackup("ci_backup.zip");
    }
}
