package com.policy.renewal.controller;

import com.policy.renewal.service.PolicyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * HomeControllerTest — web layer unit tests for the Dashboard HomeController.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@WebMvcTest(HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolicyService policyService;

    @Test
    @DisplayName("GET / returns 200 OK and renders index view with dashboard metrics")
    void testDashboardHome() throws Exception {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", 10L);
        stats.put("active", 5L);
        stats.put("expiringSoon", 2L);
        stats.put("expired", 1L);
        stats.put("renewed", 2L);

        when(policyService.getDashboardStats()).thenReturn(stats);
        when(policyService.getUpcomingRenewals()).thenReturn(Collections.emptyList());
        when(policyService.getAllPolicies()).thenReturn(Collections.emptyList());
        when(policyService.getRenewalAlertDays()).thenReturn(30);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("stats"))
                .andExpect(model().attributeExists("upcomingRenewals"))
                .andExpect(model().attributeExists("recentPolicies"))
                .andExpect(model().attribute("alertDays", 30));
    }
}
