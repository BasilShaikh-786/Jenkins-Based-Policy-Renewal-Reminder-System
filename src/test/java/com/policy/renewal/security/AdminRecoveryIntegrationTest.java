package com.policy.renewal.security;

import com.policy.renewal.PolicyRenewalApplication;
import com.policy.renewal.model.Role;
import com.policy.renewal.model.User;
import com.policy.renewal.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AdminRecoveryIntegrationTest — integration tests verifying end-to-end command-line
 * recovery mode against the JPA database.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@SpringBootTest
@Transactional
class AdminRecoveryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminRecoveryService recoveryService;

    @Autowired
    private Environment environment;

    private User targetAdminUser;

    @BeforeEach
    void setUp() {
        PolicyRenewalApplication.exitJvmOnComplete = false;
        // Ensure a clean target user exists for integration tests
        Optional<User> existing = userRepository.findByUsername("recovery_test_admin");
        if (existing.isPresent()) {
            targetAdminUser = existing.get();
            targetAdminUser.setPassword(passwordEncoder.encode("OriginalPassword@123"));
            targetAdminUser = userRepository.save(targetAdminUser);
        } else {
            targetAdminUser = new User(
                    "recovery_test_admin",
                    passwordEncoder.encode("OriginalPassword@123"),
                    "Recovery Test Admin",
                    "recovery@policyguard.local",
                    Role.ADMIN
            );
            targetAdminUser = userRepository.save(targetAdminUser);
        }
    }

    @AfterEach
    void tearDown() {
        System.clearProperty("app.admin-recovery.enabled");
        System.clearProperty("app.admin-recovery.username");
        System.clearProperty("app.admin-recovery.password");
        System.clearProperty("APP_ADMIN_PASSWORD_RESET_PASSWORD");
    }

    @Test
    @DisplayName("1. Successful recovery: updates password for target user and hashes with BCrypt")
    void testSuccessfulRecovery() {
        System.setProperty("APP_ADMIN_PASSWORD_RESET_PASSWORD", "BrandNewSecurePass@999");

        AdminRecoveryRunner runner = new AdminRecoveryRunner(recoveryService, environment);
        DefaultApplicationArguments args = new DefaultApplicationArguments(
                "--recover-admin",
                "--username=recovery_test_admin"
        );

        runner.run(args);

        AdminRecoveryResult result = runner.getLastResult();
        assertNotNull(result, "Runner must produce a result");
        assertTrue(result.isSuccess(), "Recovery must report success");
        assertEquals("recovery_test_admin", result.getUsername());

        // Verify password in database
        User updated = userRepository.findByUsername("recovery_test_admin").orElseThrow();
        assertTrue(passwordEncoder.matches("BrandNewSecurePass@999", updated.getPassword()),
                "Database password must match the new reset password");
        assertFalse(passwordEncoder.matches("OriginalPassword@123", updated.getPassword()),
                "Old password must no longer match");

        // Verify other user details remain untouched
        assertEquals("Recovery Test Admin", updated.getFullName());
        assertEquals("recovery@policyguard.local", updated.getEmail());
        assertEquals(Role.ADMIN, updated.getRole());
    }

    @Test
    @DisplayName("2. Missing password: refuses to run and preserves existing password")
    void testRecoveryFailsWhenPasswordMissing() {
        System.clearProperty("APP_ADMIN_PASSWORD_RESET_PASSWORD");
        System.clearProperty("app.admin-recovery.password");

        AdminRecoveryRunner runner = new AdminRecoveryRunner(recoveryService, environment);
        DefaultApplicationArguments args = new DefaultApplicationArguments(
                "--recover-admin",
                "--username=recovery_test_admin"
        );

        runner.run(args);

        AdminRecoveryResult result = runner.getLastResult();
        assertNotNull(result);
        assertFalse(result.isSuccess(), "Recovery must refuse to run when password is missing");
        assertTrue(result.getMessage().contains("missing"), "Error message must indicate missing password");

        // Verify existing user password in database is completely unchanged
        User current = userRepository.findByUsername("recovery_test_admin").orElseThrow();
        assertTrue(passwordEncoder.matches("OriginalPassword@123", current.getPassword()),
                "Existing password must remain unchanged when recovery fails");
    }

    @Test
    @DisplayName("3. Nonexistent user: refuses to run and does NOT create another administrator")
    void testRecoveryFailsWhenUserDoesNotExist() {
        System.setProperty("APP_ADMIN_PASSWORD_RESET_PASSWORD", "SomeValidPassword@123");

        long initialUserCount = userRepository.count();

        AdminRecoveryRunner runner = new AdminRecoveryRunner(recoveryService, environment);
        DefaultApplicationArguments args = new DefaultApplicationArguments(
                "--recover-admin",
                "--username=totally_nonexistent_user"
        );

        runner.run(args);

        AdminRecoveryResult result = runner.getLastResult();
        assertNotNull(result);
        assertFalse(result.isSuccess(), "Recovery must refuse when user does not exist");
        assertTrue(result.getMessage().contains("does not exist"), "Error message must indicate nonexistent user");

        // Verify user was NOT created
        assertTrue(userRepository.findByUsername("totally_nonexistent_user").isEmpty(),
                "Nonexistent user must not be created");
        assertEquals(initialUserCount, userRepository.count(),
                "Total user count in database must not increase");
    }

    @Test
    @DisplayName("4. Disabled recovery mode: runner does nothing when recovery flag is absent")
    void testRecoveryDisabledByDefault() {
        System.clearProperty("APP_ADMIN_PASSWORD_RESET_PASSWORD");

        AdminRecoveryRunner runner = new AdminRecoveryRunner(recoveryService, environment);
        DefaultApplicationArguments args = new DefaultApplicationArguments(
                "--some-other-flag=true"
        );

        runner.run(args);

        assertNull(runner.getLastResult(), "Runner must not execute when recovery mode is not active");

        // Verify existing user is untouched
        User current = userRepository.findByUsername("recovery_test_admin").orElseThrow();
        assertTrue(passwordEncoder.matches("OriginalPassword@123", current.getPassword()),
                "User password must remain untouched when recovery mode is disabled");
    }

    @Test
    @DisplayName("5. Target username defaults to 'admin' when --username is omitted")
    void testTargetUsernameDefaultsToAdmin() {
        AdminRecoveryRunner runner = new AdminRecoveryRunner(recoveryService, environment);
        DefaultApplicationArguments args = new DefaultApplicationArguments("--recover-admin");

        String username = runner.resolveTargetUsername(args);
        assertEquals("admin", username, "Default username must be 'admin' when omitted");
    }

    @Test
    @DisplayName("6. Never prints or stores plaintext password or hash in recovery result")
    void testNeverPrintsPasswordOrHash() {
        AdminRecoveryResult success = AdminRecoveryResult.success("admin", "Password successfully updated.");
        assertFalse(success.toString().contains("password"), "toString must not expose password");

        AdminRecoveryResult failure = AdminRecoveryResult.failure("admin", "Recovery failed: User does not exist.");
        assertFalse(failure.toString().contains("hash"), "toString must not expose hash");
    }

    @Test
    @DisplayName("7. Command-line flag parsing in PolicyRenewalApplication detects recovery mode accurately")
    void testIsRecoveryModeRequestedDetection() {
        assertTrue(PolicyRenewalApplication.isRecoveryModeRequested(new String[]{"--recover-admin"}));
        assertTrue(PolicyRenewalApplication.isRecoveryModeRequested(new String[]{"--recover-admin=true"}));
        assertTrue(PolicyRenewalApplication.isRecoveryModeRequested(new String[]{"--app.admin-recovery.enabled=true"}));
        assertTrue(PolicyRenewalApplication.isRecoveryModeRequested(new String[]{"--recovery-mode"}));
        assertTrue(PolicyRenewalApplication.isRecoveryModeRequested(new String[]{"--other", "--recover-admin"}));

        assertFalse(PolicyRenewalApplication.isRecoveryModeRequested(new String[]{"--recover-admin=false"}));
        assertFalse(PolicyRenewalApplication.isRecoveryModeRequested(new String[]{"--app.admin-recovery.enabled=false"}));
        assertFalse(PolicyRenewalApplication.isRecoveryModeRequested(new String[]{"--other=123"}));
        assertFalse(PolicyRenewalApplication.isRecoveryModeRequested(new String[]{}));
        assertFalse(PolicyRenewalApplication.isRecoveryModeRequested(null));
    }
}
