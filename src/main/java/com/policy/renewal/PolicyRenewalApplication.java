package com.policy.renewal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Clock;
import java.time.ZoneId;

/**
 * PolicyRenewalApplication — Spring Boot entry point.
 *
 * <p>This is the main class that starts the application. The {@code @SpringBootApplication}
 * annotation is a shortcut that enables three important Spring Boot features:
 * <ul>
 *   <li>{@code @Configuration} — this class can define Spring beans.</li>
 *   <li>{@code @EnableAutoConfiguration} — Spring Boot automatically configures the
 *       application based on the dependencies in the classpath (e.g., it detects H2
 *       and configures a data source automatically).</li>
 *   <li>{@code @ComponentScan} — Spring scans this package and all sub-packages for
 *       components such as controllers, services and repositories.</li>
 * </ul>
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@SpringBootApplication
@EnableScheduling
public class PolicyRenewalApplication {

    /**
     * Controls whether System.exit() is invoked upon recovery completion.
     * Defaults to true for CLI execution; set to false during automated tests.
     */
    public static boolean exitJvmOnComplete = true;

    /**
     * Configured timezone for date evaluation and scheduled job execution.
     * Defaults to Asia/Kolkata (IST, UTC+05:30) if not set in application.properties.
     * Override via the environment variable {@code SCHEDULER_TIMEZONE}.
     */
    @Value("${scheduler.timezone:Asia/Kolkata}")
    private String schedulerTimezone;

    /**
     * Application entry point. Boots the embedded Tomcat server in standard mode,
     * or executes the safe command-line recovery mechanism in recovery mode without
     * starting the web server.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        if (isRecoveryModeRequested(args)) {
            int exitCode = runRecoveryMode(args);
            if (exitJvmOnComplete) {
                System.exit(exitCode);
            }
        } else {
            SpringApplication.run(PolicyRenewalApplication.class, args);
        }
    }

    /**
     * Runs the application context in NON-WEB mode to execute admin password recovery.
     *
     * @param args command-line arguments
     * @return 0 on successful recovery, 1 on failure
     */
    public static int runRecoveryMode(String[] args) {
        System.setProperty("app.admin-recovery.enabled", "true");
        SpringApplication app = new SpringApplication(PolicyRenewalApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);

        org.springframework.context.ConfigurableApplicationContext context = null;
        int exitCode = 0;
        try {
            context = app.run(args);
            com.policy.renewal.security.AdminRecoveryRunner runner =
                    context.getBean(com.policy.renewal.security.AdminRecoveryRunner.class);
            com.policy.renewal.security.AdminRecoveryResult result = runner.getLastResult();
            if (result == null || !result.isSuccess()) {
                exitCode = 1;
            }
        } catch (Exception ex) {
            System.err.println("[ADMIN-RECOVERY] Execution failed: " + ex.getMessage());
            exitCode = 1;
        } finally {
            if (context != null) {
                try {
                    final int code = exitCode;
                    SpringApplication.exit(context, () -> code);
                } catch (Exception ignored) {
                }
            }
        }
        return exitCode;
    }

    /**
     * Determines whether recovery mode is explicitly requested from command-line arguments,
     * system properties, or environment variables.
     *
     * @param args command-line arguments
     * @return true if recovery mode is explicitly requested
     */
    public static boolean isRecoveryModeRequested(String[] args) {
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
                    if (clean.equalsIgnoreCase("--recover-admin=false")
                            || clean.equalsIgnoreCase("--app.admin-recovery.enabled=false")
                            || clean.equalsIgnoreCase("--recovery-mode=false")) {
                        return false;
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

    /**
     * Provides a timezone-aware Clock bean for consistent, testable date and time handling.
     * All reminder evaluation (window matching, audit timestamps) runs against this clock.
     * Tests can provide a fixed clock override via {@code @TestConfiguration} or mocks.
     *
     * @return clock fixed to the configured timezone (default: Asia/Kolkata / IST)
     */
    @Bean
    public Clock clock() {
        ZoneId zone = resolveZone(schedulerTimezone);
        return Clock.system(zone);
    }

    /**
     * Resolves a timezone string to a {@link ZoneId}, falling back to Asia/Kolkata
     * if the configured value is null, blank, or unrecognised.
     *
     * @param timezone the timezone string from configuration
     * @return a valid ZoneId
     */
    public static ZoneId resolveZone(String timezone) {
        if (timezone == null || timezone.isBlank()) {
            return ZoneId.of("Asia/Kolkata");
        }
        try {
            return ZoneId.of(timezone.trim());
        } catch (Exception ex) {
            // Log at warn level; do not crash startup over a misconfigured timezone
            System.err.println("[WARN] Invalid scheduler.timezone '" + timezone
                    + "'. Falling back to Asia/Kolkata. Error: " + ex.getMessage());
            return ZoneId.of("Asia/Kolkata");
        }
    }
}
