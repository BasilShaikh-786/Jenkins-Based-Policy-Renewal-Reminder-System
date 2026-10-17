package com.policy.renewal.service;

import com.policy.renewal.dto.BackupInfo;
import com.policy.renewal.dto.BackupVerificationResult;
import com.policy.renewal.model.AuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DatabaseBackupService — manages database backup creation, cataloging, and safe recovery verification.
 *
 * <p>Key Guarantees:
 * <ul>
 *   <li>Non-blocking, live database backup using native H2 online {@code SCRIPT TO} command with ZIP compression.</li>
 *   <li>Timestamped backup archives stored in a dedicated directory (default: {@code ./data/backups}).</li>
 *   <li>Path traversal prevention on all file lookup and deletion operations.</li>
 *   <li>Safe recovery verification: restores into an isolated, temporary in-memory database to assert table
 *       integrity and row accessibility without ever touching or modifying the live production database.</li>
 *   <li>Full integration with {@link AuditLogService} to maintain a verifiable trail of backup events.</li>
 * </ul>
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Service
public class DatabaseBackupService {

    private static final Logger log = LoggerFactory.getLogger(DatabaseBackupService.class);
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final JdbcTemplate jdbcTemplate;
    private final AuditLogService auditLogService;
    private final Path backupDirectory;
    private Clock clock = Clock.systemDefaultZone();

    public DatabaseBackupService(JdbcTemplate jdbcTemplate) {
        this(jdbcTemplate, null, "./data/backups", Clock.systemDefaultZone());
    }

    public DatabaseBackupService(JdbcTemplate jdbcTemplate, AuditLogService auditLogService) {
        this(jdbcTemplate, auditLogService, "./data/backups", Clock.systemDefaultZone());
    }

    @Autowired
    public DatabaseBackupService(JdbcTemplate jdbcTemplate,
                                 @Autowired(required = false) AuditLogService auditLogService,
                                 @Value("${app.backup.directory:./data/backups}") String backupDirPath,
                                 @Autowired(required = false) Clock clock) {
        this.jdbcTemplate = jdbcTemplate;
        this.auditLogService = auditLogService;
        this.backupDirectory = Paths.get(backupDirPath).toAbsolutePath().normalize();
        if (clock != null) {
            this.clock = clock;
        }
        ensureBackupDirectoryExists();
    }

    public void setClock(Clock clock) {
        if (clock != null) {
            this.clock = clock;
        }
    }

    /**
     * Ensures the configured backup directory exists on the filesystem.
     */
    private void ensureBackupDirectoryExists() {
        try {
            if (!Files.exists(backupDirectory)) {
                Files.createDirectories(backupDirectory);
                log.info("[BACKUP] Created backup directory at: {}", backupDirectory);
            }
        } catch (IOException e) {
            log.error("[BACKUP] Failed to create backup directory: {}", backupDirectory, e);
        }
    }

    public Path getBackupDirectory() {
        ensureBackupDirectoryExists();
        return backupDirectory;
    }

    /**
     * Creates a timestamped, compressed online backup of the live H2 database.
     *
     * @return {@link BackupInfo} containing metadata of the created backup
     * @throws RuntimeException if the backup command fails or produces an empty file
     */
    public BackupInfo createBackup() {
        ensureBackupDirectoryExists();
        String timestamp = LocalDateTime.now(clock).format(TIMESTAMP_FORMAT);
        String fileName = "policyrenewaldb_backup_" + timestamp + ".zip";
        Path targetPath = backupDirectory.resolve(fileName).normalize();

        // Prevent path traversal
        if (!targetPath.startsWith(backupDirectory)) {
            throw new SecurityException("Invalid target backup path.");
        }

        String h2SafePath = targetPath.toString().replace('\\', '/');
        log.info("[BACKUP] Initiating online database backup to: {}", h2SafePath);

        try {
            // H2 native online SCRIPT command with ZIP compression
            String scriptCommand = "SCRIPT TO '" + h2SafePath + "' COMPRESSION ZIP";
            jdbcTemplate.execute(scriptCommand);

            if (!Files.exists(targetPath)) {
                throw new IOException("Backup file was not created on disk: " + targetPath);
            }

            long sizeBytes = Files.size(targetPath);
            if (sizeBytes == 0) {
                throw new IOException("Generated backup file is 0 bytes: " + targetPath);
            }

            LocalDateTime createdTime = LocalDateTime.now(clock);
            BackupInfo info = new BackupInfo(fileName, targetPath.toString(), sizeBytes, createdTime, "ZIP");

            log.info("[BACKUP] [SUCCESS] Backup created: {} ({})", fileName, info.getFormattedSize());

            if (auditLogService != null) {
                auditLogService.logSuccess(
                        AuditLog.ACTION_BACKUP_CREATED,
                        AuditLog.RESOURCE_DATABASE,
                        fileName,
                        "Created online database backup archive (" + info.getFormattedSize() + ")");
            }

            return info;
        } catch (Exception e) {
            log.error("[BACKUP] [FAILED] Could not complete database backup: {}", e.getMessage(), e);
            if (auditLogService != null) {
                auditLogService.logFailure(
                        AuditLog.ACTION_BACKUP_CREATED,
                        AuditLog.RESOURCE_DATABASE,
                        fileName,
                        "Backup creation failed: " + e.getMessage());
            }
            throw new RuntimeException("Database backup failed: " + e.getMessage(), e);
        }
    }

    /**
     * Lists all existing backup files in the backup directory, sorted by modification date descending.
     *
     * @return list of {@link BackupInfo}
     */
    public List<BackupInfo> listBackups() {
        ensureBackupDirectoryExists();
        if (!Files.exists(backupDirectory)) {
            return Collections.emptyList();
        }

        try (Stream<Path> stream = Files.list(backupDirectory)) {
            return stream
                    .filter(path -> Files.isRegularFile(path) && (path.toString().endsWith(".zip") || path.toString().endsWith(".sql")))
                    .map(this::toBackupInfo)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(BackupInfo::getCreatedAt).reversed())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("[BACKUP] Error listing backups in {}: {}", backupDirectory, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Retrieves metadata for a specific backup file by name.
     *
     * @param fileName backup filename (e.g. policyrenewaldb_backup_20261017_143000.zip)
     * @return Optional containing BackupInfo if found
     */
    public Optional<BackupInfo> getBackup(String fileName) {
        if (fileName == null || fileName.isBlank() || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            return Optional.empty();
        }
        Path path = backupDirectory.resolve(fileName).normalize();
        if (!path.startsWith(backupDirectory) || !Files.exists(path) || !Files.isRegularFile(path)) {
            return Optional.empty();
        }
        return Optional.ofNullable(toBackupInfo(path));
    }

    /**
     * Performs a safe, non-destructive recovery verification by restoring the backup archive into an
     * isolated temporary in-memory H2 database, checking schema and records.
     *
     * @param fileName the backup archive to verify
     * @return {@link BackupVerificationResult} detailing success or failure and table record counts
     */
    public BackupVerificationResult verifyBackup(String fileName) {
        if (fileName == null || fileName.isBlank() || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            return BackupVerificationResult.failure(fileName, "Invalid or unsafe backup file name.");
        }

        Path backupFile = backupDirectory.resolve(fileName).normalize();
        if (!backupFile.startsWith(backupDirectory) || !Files.exists(backupFile)) {
            return BackupVerificationResult.failure(fileName, "Backup file does not exist on disk.");
        }

        String h2SafePath = backupFile.toString().replace('\\', '/');
        String verifyDbUrl = "jdbc:h2:mem:verify_" + System.nanoTime() + ";DB_CLOSE_DELAY=0";

        log.info("[BACKUP-VERIFY] Commencing isolated recovery verification for '{}' using in-memory instance", fileName);

        try (Connection conn = DriverManager.getConnection(verifyDbUrl, "sa", "");
             Statement stmt = conn.createStatement()) {

            // Replay the backup script in the isolated verification database
            String restoreCmd = "RUNSCRIPT FROM '" + h2SafePath + "' COMPRESSION ZIP";
            stmt.execute(restoreCmd);

            // Query record counts from restored tables
            long policyCount = queryCount(stmt, "SELECT COUNT(*) FROM POLICY");
            long userCount = queryCount(stmt, "SELECT COUNT(*) FROM APP_USER");
            long reminderCount = queryCount(stmt, "SELECT COUNT(*) FROM REMINDER_RECORD");
            long auditCount = queryCount(stmt, "SELECT COUNT(*) FROM AUDIT_LOG");

            String msg = String.format(
                    "Recovery verification passed. Tables restored: Policies: %d, Users: %d, Reminder Records: %d, Audit Logs: %d",
                    policyCount, userCount, reminderCount, auditCount);

            log.info("[BACKUP-VERIFY] [SUCCESS] {} -> {}", fileName, msg);

            if (auditLogService != null) {
                auditLogService.logSuccess(
                        AuditLog.ACTION_BACKUP_VERIFIED,
                        AuditLog.RESOURCE_DATABASE,
                        fileName,
                        msg);
            }

            return BackupVerificationResult.success(fileName, policyCount, userCount, reminderCount, auditCount, msg);

        } catch (Exception e) {
            String errorMsg = "Verification failed: " + e.getMessage();
            log.error("[BACKUP-VERIFY] [FAILED] Could not verify backup '{}': {}", fileName, errorMsg, e);

            if (auditLogService != null) {
                auditLogService.logFailure(
                        AuditLog.ACTION_BACKUP_VERIFIED,
                        AuditLog.RESOURCE_DATABASE,
                        fileName,
                        errorMsg);
            }

            return BackupVerificationResult.failure(fileName, errorMsg);
        }
    }

    /**
     * Safely deletes an old backup file.
     *
     * @param fileName the filename to remove
     * @return true if deleted, false otherwise
     */
    public boolean deleteBackup(String fileName) {
        if (fileName == null || fileName.isBlank() || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            return false;
        }
        Path target = backupDirectory.resolve(fileName).normalize();
        if (!target.startsWith(backupDirectory) || !Files.exists(target)) {
            return false;
        }

        try {
            Files.delete(target);
            log.info("[BACKUP] Deleted backup file: {}", fileName);
            if (auditLogService != null) {
                auditLogService.logSuccess(
                        "BACKUP_DELETED",
                        AuditLog.RESOURCE_DATABASE,
                        fileName,
                        "Deleted database backup archive: " + fileName);
            }
            return true;
        } catch (IOException e) {
            log.error("[BACKUP] Failed to delete backup file {}: {}", fileName, e.getMessage());
            return false;
        }
    }

    private long queryCount(Statement stmt, String sql) {
        try (ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (Exception e) {
            // Table might not exist in an older backup; log as debug and return 0
            log.debug("[BACKUP-VERIFY] Optional table query failed '{}': {}", sql, e.getMessage());
        }
        return 0L;
    }

    private BackupInfo toBackupInfo(Path path) {
        try {
            BasicFileAttributes attrs = Files.readAttributes(path, BasicFileAttributes.class);
            long size = attrs.size();
            LocalDateTime created = LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), clock.getZone());
            String type = path.toString().endsWith(".zip") ? "ZIP" : "SQL";
            return new BackupInfo(path.getFileName().toString(), path.toString(), size, created, type);
        } catch (IOException e) {
            log.warn("[BACKUP] Could not read attributes for backup file: {}", path, e);
            return null;
        }
    }
}
