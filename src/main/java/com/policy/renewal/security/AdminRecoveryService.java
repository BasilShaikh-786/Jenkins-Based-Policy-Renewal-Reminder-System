package com.policy.renewal.security;

import com.policy.renewal.model.User;
import com.policy.renewal.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * AdminRecoveryService — service executing safe, one-time admin password recovery.
 *
 * <p>Guarantees:
 * <ul>
 *   <li>Updates ONLY the selected existing user's password.</li>
 *   <li>Never deletes, recreates, or resets any users, policies, reminders, or database tables.</li>
 *   <li>Always hashes passwords using BCrypt via {@link PasswordEncoder}. Never stores plaintext.</li>
 *   <li>Refuses to run if password is missing/invalid, or target user does not exist.</li>
 *   <li>Never silently creates another administrator.</li>
 *   <li>Never prints or logs the raw password or password hash.</li>
 * </ul>
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Service
public class AdminRecoveryService {

    private static final Logger log = LoggerFactory.getLogger(AdminRecoveryService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private com.policy.renewal.service.AuditLogService auditLogService;

    public AdminRecoveryService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this(userRepository, passwordEncoder, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public AdminRecoveryService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                @org.springframework.beans.factory.annotation.Autowired(required = false) com.policy.renewal.service.AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    public void setAuditLogService(com.policy.renewal.service.AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /**
     * Resets the password for an existing user account.
     *
     * @param username    target username (must not be blank, must exist in DB)
     * @param rawPassword new raw password from APP_ADMIN_PASSWORD_RESET_PASSWORD (must not be blank, min 6 chars)
     * @return {@link AdminRecoveryResult} indicating success or failure without exposing sensitive data
     */
    @Transactional
    public AdminRecoveryResult resetPassword(String username, String rawPassword) {
        if (username == null || username.isBlank()) {
            String errorMsg = "Recovery failed: Target username is required and cannot be blank.";
            log.error("[ADMIN-RECOVERY] {}", errorMsg);
            return AdminRecoveryResult.failure(username, errorMsg);
        }

        String targetUser = username.trim();

        if (rawPassword == null || rawPassword.isBlank()) {
            String errorMsg = "Recovery failed: Reset password is missing. Please set the APP_ADMIN_PASSWORD_RESET_PASSWORD environment variable.";
            log.error("[ADMIN-RECOVERY] {}", errorMsg);
            return AdminRecoveryResult.failure(targetUser, errorMsg);
        }

        if (rawPassword.length() < 6) {
            String errorMsg = "Recovery failed: Password must be at least 6 characters in length.";
            log.error("[ADMIN-RECOVERY] {}", errorMsg);
            return AdminRecoveryResult.failure(targetUser, errorMsg);
        }

        Optional<User> userOptional = userRepository.findByUsername(targetUser);
        if (userOptional.isEmpty()) {
            String errorMsg = "Recovery failed: User '" + targetUser + "' does not exist in the database. Recovery mode will not create new accounts.";
            log.error("[ADMIN-RECOVERY] {}", errorMsg);
            return AdminRecoveryResult.failure(targetUser, errorMsg);
        }

        User user = userOptional.get();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);

        String successMsg = "Password for user '" + targetUser + "' has been successfully updated.";
        log.info("[ADMIN-RECOVERY] [SUCCESS] {}", successMsg);
        if (auditLogService != null) {
            auditLogService.log(com.policy.renewal.model.AuditLog.ACTION_ADMIN_PASSWORD_RESET,
                    com.policy.renewal.model.AuditLog.RESOURCE_SECURITY, targetUser,
                    "CLI_RECOVERY", com.policy.renewal.model.AuditLog.STATUS_SUCCESS, successMsg);
        }
        return AdminRecoveryResult.success(targetUser, successMsg);
    }
}
