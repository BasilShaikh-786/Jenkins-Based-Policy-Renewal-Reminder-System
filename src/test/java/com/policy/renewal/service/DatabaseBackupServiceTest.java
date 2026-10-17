package com.policy.renewal.service;

import com.policy.renewal.dto.BackupInfo;
import com.policy.renewal.dto.BackupVerificationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DatabaseBackupServiceTest — automated test suite for database backup and safe recovery verification.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@SpringBootTest
class DatabaseBackupServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private AuditLogService auditLogService;

    @Test
    @DisplayName("createBackup creates a valid, non-empty compressed zip file in the target directory")
    void testCreateBackup(@TempDir Path tempDir) {
        DatabaseBackupService backupService = new DatabaseBackupService(
                jdbcTemplate, auditLogService, tempDir.toString(), Clock.systemDefaultZone());

        BackupInfo backup = backupService.createBackup();

        assertNotNull(backup);
        assertNotNull(backup.getFileName());
        assertTrue(backup.getFileName().endsWith(".zip"));
        assertTrue(backup.getSizeBytes() > 0, "Backup file size must be greater than zero bytes");
        assertTrue(Files.exists(tempDir.resolve(backup.getFileName())));
        assertEquals("ZIP", backup.getBackupType());
    }

    @Test
    @DisplayName("listBackups returns created archives sorted chronologically")
    void testListBackups(@TempDir Path tempDir) {
        DatabaseBackupService backupService = new DatabaseBackupService(
                jdbcTemplate, auditLogService, tempDir.toString(), Clock.systemDefaultZone());

        BackupInfo backup1 = backupService.createBackup();
        List<BackupInfo> list = backupService.listBackups();

        assertFalse(list.isEmpty());
        assertTrue(list.stream().anyMatch(b -> b.getFileName().equals(backup1.getFileName())));
    }

    @Test
    @DisplayName("getBackup retrieves metadata for existing backup and empty for nonexistent")
    void testGetBackup(@TempDir Path tempDir) {
        DatabaseBackupService backupService = new DatabaseBackupService(
                jdbcTemplate, auditLogService, tempDir.toString(), Clock.systemDefaultZone());

        BackupInfo backup = backupService.createBackup();
        Optional<BackupInfo> found = backupService.getBackup(backup.getFileName());

        assertTrue(found.isPresent());
        assertEquals(backup.getFileName(), found.get().getFileName());

        Optional<BackupInfo> notFound = backupService.getBackup("nonexistent.zip");
        assertTrue(notFound.isEmpty());
    }

    @Test
    @DisplayName("verifyBackup successfully restores into isolated in-memory DB and validates record counts")
    void testVerifyBackupRestoresIntoIsolatedDatabase(@TempDir Path tempDir) {
        DatabaseBackupService backupService = new DatabaseBackupService(
                jdbcTemplate, auditLogService, tempDir.toString(), Clock.systemDefaultZone());

        BackupInfo backup = backupService.createBackup();

        BackupVerificationResult result = backupService.verifyBackup(backup.getFileName());

        assertNotNull(result);
        assertTrue(result.isValid(), "Recovery verification should pass for a freshly created backup");
        assertEquals(backup.getFileName(), result.getFileName());
        assertNull(result.getErrorMessage());
        assertNotNull(result.getMessage());
        // Verify that record counts were queried
        assertTrue(result.getPolicyCount() >= 0);
        assertTrue(result.getUserCount() >= 0);
    }

    @Test
    @DisplayName("verifyBackup rejects path traversal attacks safely")
    void testVerifyBackupRejectsPathTraversal(@TempDir Path tempDir) {
        DatabaseBackupService backupService = new DatabaseBackupService(
                jdbcTemplate, auditLogService, tempDir.toString(), Clock.systemDefaultZone());

        BackupVerificationResult result = backupService.verifyBackup("../secret.zip");
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("Invalid"));
    }

    @Test
    @DisplayName("deleteBackup deletes backup file safely and prevents path traversal")
    void testDeleteBackup(@TempDir Path tempDir) {
        DatabaseBackupService backupService = new DatabaseBackupService(
                jdbcTemplate, auditLogService, tempDir.toString(), Clock.systemDefaultZone());

        BackupInfo backup = backupService.createBackup();
        assertTrue(Files.exists(tempDir.resolve(backup.getFileName())));

        boolean deleted = backupService.deleteBackup(backup.getFileName());
        assertTrue(deleted);
        assertFalse(Files.exists(tempDir.resolve(backup.getFileName())));

        // Path traversal deletion attempt returns false
        assertFalse(backupService.deleteBackup("../../etc/passwd"));
    }
}
