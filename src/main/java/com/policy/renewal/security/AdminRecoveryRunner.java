package com.policy.renewal.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AdminRecoveryRunner — command-line-only runner for safe admin password recovery.
 *
 * <p>Execution criteria:
 * <ul>
 *   <li>Runs only when explicitly activated via command-line flag (e.g. {@code --recover-admin} or
 *       {@code --app.admin-recovery.enabled=true}) or environment variable {@code APP_ADMIN_RECOVERY_ENABLED=true}.</li>
 *   <li>Runs at {@code @Order(1)} before any other startup initializers.</li>
 *   <li>Resolves the new password from {@code APP_ADMIN_PASSWORD_RESET_PASSWORD}.</li>
 *   <li>Resolves target username (defaults to "admin" if unspecified).</li>
 *   <li>Executes password update via {@link AdminRecoveryService}.</li>
 *   <li>Never prints or logs the password or password hash.</li>
 * </ul>
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Component
@Order(1)
public class AdminRecoveryRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminRecoveryRunner.class);

    private final AdminRecoveryService recoveryService;
    private final Environment environment;

    private AdminRecoveryResult lastResult;

    public AdminRecoveryRunner(AdminRecoveryService recoveryService, Environment environment) {
        this.recoveryService = recoveryService;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!isRecoveryMode(args)) {
            log.debug("[ADMIN-RECOVERY] Recovery mode is not enabled. Skipping recovery runner.");
            return;
        }

        log.info("[ADMIN-RECOVERY] Explicit admin password recovery mode detected.");

        String targetUsername = resolveTargetUsername(args);
        String resetPassword = resolveResetPassword();

        this.lastResult = recoveryService.resetPassword(targetUsername, resetPassword);

        System.out.println("================================================================================");
        if (lastResult.isSuccess()) {
            System.out.println("[ADMIN-RECOVERY] SUCCESS: " + lastResult.getMessage());
        } else {
            System.err.println("[ADMIN-RECOVERY] ERROR: " + lastResult.getMessage());
        }
        System.out.println("================================================================================");
    }

    /**
     * Determines whether recovery mode is explicitly enabled.
     */
    public boolean isRecoveryMode(ApplicationArguments args) {
        if (args != null) {
            if (args.containsOption("recover-admin")) {
                List<String> values = args.getOptionValues("recover-admin");
                if (values == null || values.isEmpty() || !"false".equalsIgnoreCase(values.get(0))) {
                    return true;
                }
            }
            if (args.containsOption("app.admin-recovery.enabled")) {
                List<String> values = args.getOptionValues("app.admin-recovery.enabled");
                if (values == null || values.isEmpty() || !"false".equalsIgnoreCase(values.get(0))) {
                    return true;
                }
            }
            if (args.containsOption("recovery-mode")) {
                List<String> values = args.getOptionValues("recovery-mode");
                if (values == null || values.isEmpty() || !"false".equalsIgnoreCase(values.get(0))) {
                    return true;
                }
            }
            if (args.getSourceArgs() != null) {
                for (String arg : args.getSourceArgs()) {
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
        }

        String prop = environment.getProperty("app.admin-recovery.enabled");
        if (prop != null && "true".equalsIgnoreCase(prop.trim())) {
            return true;
        }

        String sysProp = System.getProperty("app.admin-recovery.enabled");
        if (sysProp != null && "true".equalsIgnoreCase(sysProp.trim())) {
            return true;
        }

        String envVar = System.getenv("APP_ADMIN_RECOVERY_ENABLED");
        if (envVar != null && "true".equalsIgnoreCase(envVar.trim())) {
            return true;
        }

        return false;
    }

    /**
     * Resolves the target username from CLI arguments, environment/properties, or default "admin".
     */
    public String resolveTargetUsername(ApplicationArguments args) {
        if (args != null) {
            String[] options = {"username", "target-user", "user", "app.admin-recovery.username"};
            for (String opt : options) {
                if (args.containsOption(opt)) {
                    List<String> values = args.getOptionValues(opt);
                    if (values != null && !values.isEmpty()) {
                        return values.get(0);
                    }
                }
            }
            if (args.getSourceArgs() != null) {
                for (String arg : args.getSourceArgs()) {
                    if (arg != null) {
                        for (String opt : options) {
                            String prefix = "--" + opt + "=";
                            if (arg.startsWith(prefix)) {
                                return arg.substring(prefix.length());
                            }
                        }
                    }
                }
            }
        }

        String prop = environment.getProperty("app.admin-recovery.username");
        if (prop != null && !prop.isBlank()) {
            return prop.trim();
        }

        String envUser = System.getenv("APP_ADMIN_PASSWORD_RESET_USERNAME");
        if (envUser != null && !envUser.isBlank()) {
            return envUser.trim();
        }

        return "admin";
    }

    /**
     * Resolves the new password from APP_ADMIN_PASSWORD_RESET_PASSWORD environment variable or property.
     */
    public String resolveResetPassword() {
        String envPass = System.getenv("APP_ADMIN_PASSWORD_RESET_PASSWORD");
        if (envPass != null && !envPass.isBlank()) {
            return envPass;
        }

        String propPass = environment.getProperty("APP_ADMIN_PASSWORD_RESET_PASSWORD");
        if (propPass != null && !propPass.isBlank()) {
            return propPass;
        }

        String configPass = environment.getProperty("app.admin-recovery.password");
        if (configPass != null && !configPass.isBlank()) {
            return configPass;
        }

        return null;
    }

    public AdminRecoveryResult getLastResult() {
        return lastResult;
    }
}
