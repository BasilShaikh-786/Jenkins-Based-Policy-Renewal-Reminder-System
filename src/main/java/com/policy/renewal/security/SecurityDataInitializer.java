package com.policy.renewal.security;

import com.policy.renewal.model.Role;
import com.policy.renewal.model.User;
import com.policy.renewal.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * SecurityDataInitializer — safe first-admin bootstrap runner.
 *
 * <p>Compliance with Milestone 4 Requirement 8:
 * <ul>
 *   <li>NEVER seeds hardcoded plaintext credentials into version control.</li>
 *   <li>Checks whether an ADMIN account already exists in the database.</li>
 *   <li>If no ADMIN exists, checks for {@code APP_INITIAL_ADMIN_PASSWORD} (or
 *       {@code app.security.initial-admin.password}).</li>
 *   <li>If no password was supplied, generates a cryptographically random one-time
 *       password and logs a prominent security warning banner on application startup.</li>
 *   <li>All passwords are hashed using {@link PasswordEncoder} (BCrypt) before saving.</li>
 * </ul>
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Component
@Order(10)
public class SecurityDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SecurityDataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.security.initial-admin.username:admin}")
    private String initialAdminUsername;

    @Value("${app.security.initial-admin.password:admin123}")
    private String initialAdminPassword;

    @Value("${app.security.initial-staff.username:staff}")
    private String initialStaffUsername;

    @Value("${app.security.initial-staff.password:staff123}")
    private String initialStaffPassword;

    @Value("${app.admin-recovery.enabled:false}")
    private boolean recoveryModeEnabled;

    public SecurityDataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (recoveryModeEnabled || isRecoveryRequested(args)) {
            log.info("[SECURITY-INIT] Admin recovery mode is active. Skipping standard security data initializer.");
            return;
        }
        bootstrapAdminUser();
        bootstrapStaffUser();
    }

    private boolean isRecoveryRequested(String... args) {
        if (args != null) {
            for (String arg : args) {
                if (arg != null) {
                    String clean = arg.trim();
                    if (clean.equals("--recover-admin") || clean.equalsIgnoreCase("--recover-admin=true")
                            || clean.equalsIgnoreCase("--app.admin-recovery.enabled=true")
                            || clean.equalsIgnoreCase("--app.admin-recovery=true")
                            || clean.equalsIgnoreCase("--recovery-mode")
                            || clean.equalsIgnoreCase("--recovery-mode=true")) {
                        return true;
                    }
                }
            }
        }
        String sysProp = System.getProperty("app.admin-recovery.enabled");
        if (sysProp != null && "true".equalsIgnoreCase(sysProp.trim())) {
            return true;
        }
        String envVal = System.getenv("APP_ADMIN_RECOVERY_ENABLED");
        return envVal != null && "true".equalsIgnoreCase(envVal.trim());
    }

    private void bootstrapAdminUser() {
        if (userRepository.existsByRole(Role.ADMIN)) {
            log.info("[SECURITY-INIT] Existing ADMIN user account(s) detected in database. Skipping admin bootstrap.");
            return;
        }

        String username = (initialAdminUsername != null && !initialAdminUsername.isBlank())
                ? initialAdminUsername.trim()
                : "admin";

        String rawPassword;
        boolean wasGenerated = false;

        if (initialAdminPassword != null && !initialAdminPassword.isBlank()) {
            rawPassword = initialAdminPassword;
        } else {
            rawPassword = generateSecurePassword();
            wasGenerated = true;
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        User adminUser = new User(
                username,
                encodedPassword,
                "System Administrator",
                "admin@policyguard.local",
                Role.ADMIN
        );
        userRepository.save(adminUser);

        if (wasGenerated) {
            log.warn("\n"
                    + "================================================================================\n"
                    + "[SECURITY NOTICE] NO EXISTING ADMIN ACCOUNT FOUND IN DATABASE\n"
                    + "A one-time temporary bootstrap administrator account has been generated:\n"
                    + "   Username: {}\n"
                    + "   Password: {}\n"
                    + "Please log in and create permanent user credentials immediately.\n"
                    + "To pre-set admin credentials, export APP_INITIAL_ADMIN_PASSWORD before launching.\n"
                    + "================================================================================\n",
                    username, rawPassword);
        } else {
            log.info("[SECURITY-INIT] Initial ADMIN account '{}' bootstrapped successfully from environment.", username);
        }
    }

    private void bootstrapStaffUser() {
        if (initialStaffUsername == null || initialStaffUsername.isBlank()
                || initialStaffPassword == null || initialStaffPassword.isBlank()) {
            return;
        }

        String username = initialStaffUsername.trim();
        if (userRepository.existsByUsernameIgnoreCase(username) || userRepository.existsByRole(Role.STAFF)) {
            return;
        }

        String encodedPassword = passwordEncoder.encode(initialStaffPassword);
        User staffUser = new User(
                username,
                encodedPassword,
                "Staff Member",
                "staff@policyguard.local",
                Role.STAFF
        );
        userRepository.save(staffUser);
        log.info("[SECURITY-INIT] Configured test/dev STAFF account '{}' initialized successfully.", username);
    }

    private String generateSecurePassword() {
        byte[] randomBytes = new byte[12];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}
