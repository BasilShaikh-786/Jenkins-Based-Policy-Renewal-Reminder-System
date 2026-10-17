package com.policy.renewal.security;

import com.policy.renewal.PolicyRenewalApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PolicyRenewalApplicationRecoveryCliTest — verifies CLI exit code behavior of
 * {@link PolicyRenewalApplication#runRecoveryMode(String[])}.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
class PolicyRenewalApplicationRecoveryCliTest {

    @BeforeEach
    void setUp() {
        PolicyRenewalApplication.exitJvmOnComplete = false;
    }

    @AfterEach
    void tearDown() {
        System.clearProperty("app.admin-recovery.enabled");
        System.clearProperty("app.admin-recovery.username");
        System.clearProperty("app.admin-recovery.password");
        System.clearProperty("APP_ADMIN_PASSWORD_RESET_PASSWORD");
    }

    @Test
    @DisplayName("runRecoveryMode returns exit code 1 when password is missing")
    void testExitCode1WhenPasswordMissing() {
        System.clearProperty("APP_ADMIN_PASSWORD_RESET_PASSWORD");

        int exitCode = PolicyRenewalApplication.runRecoveryMode(new String[]{
                "--recover-admin",
                "--username=admin",
                "--spring.datasource.url=jdbc:h2:mem:clitest_missingpass;DB_CLOSE_DELAY=-1"
        });

        assertEquals(1, exitCode, "Must return exit code 1 when recovery password is missing");
    }

    @Test
    @DisplayName("runRecoveryMode returns exit code 1 when target user does not exist")
    void testExitCode1WhenUserDoesNotExist() {
        System.setProperty("APP_ADMIN_PASSWORD_RESET_PASSWORD", "ValidSecretPassword@123");

        int exitCode = PolicyRenewalApplication.runRecoveryMode(new String[]{
                "--recover-admin",
                "--username=nonexistent_admin_account",
                "--spring.datasource.url=jdbc:h2:mem:clitest_nonexistent;DB_CLOSE_DELAY=-1"
        });

        assertEquals(1, exitCode, "Must return exit code 1 when target user does not exist");
    }
}
