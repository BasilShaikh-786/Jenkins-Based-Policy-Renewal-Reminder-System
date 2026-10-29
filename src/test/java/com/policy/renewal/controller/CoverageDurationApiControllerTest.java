package com.policy.renewal.controller;

import com.policy.renewal.security.SecurityConfig;
import com.policy.renewal.service.PolicyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CoverageDurationApiControllerTest — Unit tests for the coverage duration end-date calculation API.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@WebMvcTest(CoverageDurationApiController.class)
@Import({SecurityConfig.class, AuthController.class})
class CoverageDurationApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolicyService policyService;

    @Test
    @WithMockUser(roles = {"STAFF"})
    @DisplayName("GET /api/coverage/calculate-end-date calculates coverage expiry date")
    void testCalculateEndDate() throws Exception {
        when(policyService.calculateCoverageEndDate(eq(LocalDate.of(2026, 10, 15)), eq(12), eq("MONTHS")))
                .thenReturn(LocalDate.of(2027, 10, 14));

        mockMvc.perform(get("/api/coverage/calculate-end-date")
                        .param("startDate", "2026-10-15")
                        .param("durationValue", "12")
                        .param("durationUnit", "MONTHS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.expiryDate").value("2027-10-14"));

        verify(policyService, times(1))
                .calculateCoverageEndDate(eq(LocalDate.of(2026, 10, 15)), eq(12), eq("MONTHS"));
    }
}

