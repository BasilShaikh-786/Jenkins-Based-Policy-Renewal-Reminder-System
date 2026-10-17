package com.policy.renewal.controller;

import com.policy.renewal.model.AuditLog;
import com.policy.renewal.security.SecurityConfig;
import com.policy.renewal.service.AuditLogService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuditLogControllerTest — web layer unit tests for administrative audit trail inspection.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@WebMvcTest(AuditLogController.class)
@Import({SecurityConfig.class, AuthController.class})
class AuditLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditLogService auditLogService;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("GET /audit as ADMIN returns 200 OK and renders audit/list view")
    void testViewAuditLogsAsAdmin() throws Exception {
        AuditLog sampleLog = new AuditLog(
                AuditLog.ACTION_POLICY_CREATED,
                AuditLog.RESOURCE_POLICY,
                "POL-1001",
                "admin",
                AuditLog.STATUS_SUCCESS,
                "Created policy");
        when(auditLogService.getRecentAuditLogs()).thenReturn(List.of(sampleLog));

        mockMvc.perform(get("/audit"))
                .andExpect(status().isOk())
                .andExpect(view().name("audit/list"))
                .andExpect(model().attributeExists("logs"))
                .andExpect(model().attribute("selectedType", "ALL"))
                .andExpect(model().attribute("totalLogsCount", 1));

        verify(auditLogService, times(1)).getRecentAuditLogs();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("GET /audit?type=POLICY as ADMIN filters by category")
    void testViewAuditLogsWithFilterAsAdmin() throws Exception {
        when(auditLogService.getLogsByResourceType("POLICY")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/audit").param("type", "POLICY"))
                .andExpect(status().isOk())
                .andExpect(view().name("audit/list"))
                .andExpect(model().attribute("selectedType", "POLICY"));

        verify(auditLogService, times(1)).getLogsByResourceType("POLICY");
    }

    @Test
    @WithMockUser(roles = {"STAFF"})
    @DisplayName("GET /audit as STAFF returns 403 Forbidden")
    void testViewAuditLogsForbiddenForStaff() throws Exception {
        mockMvc.perform(get("/audit"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(auditLogService);
    }

    @Test
    @DisplayName("GET /audit unauthenticated redirects to /login")
    void testViewAuditLogsRedirectsForAnonymous() throws Exception {
        mockMvc.perform(get("/audit").accept(org.springframework.http.MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verifyNoInteractions(auditLogService);
    }
}
