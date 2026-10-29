package com.policy.renewal.controller;

import com.policy.renewal.service.PolicyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * CoverageDurationApiController — REST API endpoint to compute coverage expiry dates
 * based on selected duration value and unit (Requirement 1.B).
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@RestController
@RequestMapping("/api/coverage")
public class CoverageDurationApiController {

    private final PolicyService policyService;

    public CoverageDurationApiController(PolicyService policyService) {
        this.policyService = policyService;
    }

    /**
     * Utility endpoint to calculate the coverage end date given a start date, duration, and unit.
     * Route: GET /api/coverage/calculate-end-date
     */
    @GetMapping("/calculate-end-date")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<Map<String, Object>> calculateEndDate(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("durationValue") Integer durationValue,
            @RequestParam(value = "durationUnit", defaultValue = "MONTHS") String durationUnit) {

        LocalDate endDate = policyService.calculateCoverageEndDate(startDate, durationValue, durationUnit);
        Map<String, Object> response = new HashMap<>();
        response.put("startDate", startDate);
        response.put("durationValue", durationValue);
        response.put("durationUnit", durationUnit);
        response.put("expiryDate", endDate);
        return ResponseEntity.ok(response);
    }
}

