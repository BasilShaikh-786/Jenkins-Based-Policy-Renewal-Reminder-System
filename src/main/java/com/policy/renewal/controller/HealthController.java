package com.policy.renewal.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HealthController — provides a lightweight, publicly accessible health check endpoint
 * for Docker container health monitoring, Kubernetes probes, and load balancer checks (Milestone 6).
 *
 * <p>Key Guarantees:
 * <ul>
 *   <li>Exempt from authentication in {@code SecurityConfig} to support non-interactive container health checks.</li>
 *   <li>Checks live database responsiveness using {@link Connection#isValid(int)}.</li>
 *   <li>Returns HTTP 200 OK with {@code status: UP} when operational.</li>
 *   <li>Returns HTTP 503 SERVICE_UNAVAILABLE with {@code status: DOWN} if the database is unreachable.</li>
 * </ul>
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@RestController
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Health check endpoint compatible with both standard /health and /actuator/health paths.
     *
     * @return HTTP 200 with JSON payload when healthy; HTTP 503 if database check fails
     */
    @GetMapping({"/health", "/actuator/health"})
    public ResponseEntity<Map<String, Object>> checkHealth() {
        boolean dbHealthy = checkDatabase();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", dbHealthy ? "UP" : "DOWN");
        response.put("database", dbHealthy ? "UP" : "DOWN");
        response.put("timestamp", Instant.now().toString());
        response.put("application", "policy-renewal-reminder-system");

        if (dbHealthy) {
            return ResponseEntity.ok(response);
        } else {
            log.warn("[HEALTH-CHECK] Database is unavailable; reporting DOWN status");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
        }
    }

    private boolean checkDatabase() {
        if (dataSource == null) {
            return false;
        }
        try (Connection conn = dataSource.getConnection()) {
            return conn.isValid(2);
        } catch (Exception e) {
            log.error("[HEALTH-CHECK] Database connection check failed: {}", e.getMessage());
            return false;
        }
    }
}
