package com.policy.renewal.controller;

import com.policy.renewal.service.PolicyReminderService;
import com.policy.renewal.service.PolicyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
@Import(com.policy.renewal.security.SecurityConfig.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolicyService policyService;

    @MockBean
    private PolicyReminderService reminderService;

    private Map<String, Long> createStatsMap() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", 10L);
        stats.put("active", 5L);
        stats.put("expiringSoon", 2L);
        stats.put("expiring7Days", 1L);
        stats.put("expiring30Days", 2L);
        stats.put("expired", 1L);
        stats.put("renewed", 2L);
        return stats;
    }

    @Test
    @WithMockUser(username = "staffMember", roles = {"STAFF"})
    @DisplayName("GET / as STAFF returns 200 OK and renders index view with dashboard metrics")
    void testDashboardHome() throws Exception {
        when(policyService.getDashboardStats()).thenReturn(createStatsMap());
        when(policyService.getUpcomingRenewals()).thenReturn(Collections.emptyList());
        when(policyService.getAllPolicies()).thenReturn(Collections.emptyList());
        when(policyService.getRenewalAlertDays()).thenReturn(30);
        when(reminderService.getConfiguredWindows()).thenReturn(List.of(30, 7, 1));
        when(reminderService.getRecentReminders()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("stats"))
                .andExpect(model().attributeExists("upcomingRenewals"))
                .andExpect(model().attributeExists("recentPolicies"))
                .andExpect(model().attribute("alertDays", 30));
    }

    @Test
    @WithMockUser(username = "adminUser", roles = {"ADMIN"})
    @DisplayName("GET / as ADMIN returns 200 OK and renders index view")
    void testDashboardHomeAsAdmin() throws Exception {
        when(policyService.getDashboardStats()).thenReturn(createStatsMap());
        when(policyService.getUpcomingRenewals()).thenReturn(Collections.emptyList());
        when(policyService.getAllPolicies()).thenReturn(Collections.emptyList());
        when(policyService.getRenewalAlertDays()).thenReturn(30);
        when(reminderService.getConfiguredWindows()).thenReturn(List.of(30, 7, 1));
        when(reminderService.getRecentReminders()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("GET / without authentication redirects anonymous user to /login")
    void testDashboardHomeUnauthenticatedRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/").accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
}
