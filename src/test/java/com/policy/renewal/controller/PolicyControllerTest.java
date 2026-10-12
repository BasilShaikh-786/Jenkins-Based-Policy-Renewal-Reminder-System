package com.policy.renewal.controller;

import com.policy.renewal.model.Policy;
import com.policy.renewal.service.PolicyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * PolicyControllerTest — web layer unit tests for Spring MVC controller endpoints.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@WebMvcTest(PolicyController.class)
class PolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolicyService policyService;

    private Policy createMockPolicy(Long id, String number) {
        Policy policy = new Policy();
        policy.setId(id);
        policy.setPolicyNumber(number);
        policy.setCustomerName("Alice Smith");
        policy.setCustomerEmail("alice@example.com");
        policy.setPolicyType("Health");
        policy.setStartDate(LocalDate.of(2026, 1, 1));
        policy.setExpiryDate(LocalDate.of(2026, 12, 31));
        policy.setPremiumAmount(new BigDecimal("6000.00"));
        return policy;
    }

    @Test
    @DisplayName("GET /policies returns 200 OK and renders policies/list view")
    void testListPolicies() throws Exception {
        when(policyService.getAllPolicies()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/policies"))
                .andExpect(status().isOk())
                .andExpect(view().name("policies/list"))
                .andExpect(model().attributeExists("policies"));
    }

    @Test
    @DisplayName("GET /policies/new returns 200 OK and renders policies/form view")
    void testShowCreateForm() throws Exception {
        mockMvc.perform(get("/policies/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("policies/form"))
                .andExpect(model().attributeExists("policy"))
                .andExpect(model().attribute("isEdit", false));
    }

    @Test
    @DisplayName("POST /policies with valid fields redirects to detail page")
    void testCreatePolicySuccess() throws Exception {
        Policy savedPolicy = createMockPolicy(1L, "POL-100");
        when(policyService.save(any(Policy.class))).thenReturn(savedPolicy);

        mockMvc.perform(post("/policies")
                        .param("policyNumber", "POL-100")
                        .param("customerName", "Alice Smith")
                        .param("customerEmail", "alice@example.com")
                        .param("policyType", "Health")
                        .param("startDate", "2026-01-01")
                        .param("expiryDate", "2026-12-31")
                        .param("premiumAmount", "6000.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/policies/1"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("POST /policies with missing required fields returns form view with errors")
    void testCreatePolicyValidationError() throws Exception {
        mockMvc.perform(post("/policies")
                        .param("policyNumber", "")
                        .param("customerName", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("policies/form"))
                .andExpect(model().attributeHasFieldErrors("policy", "policyNumber", "customerName"));
    }

    @Test
    @DisplayName("GET /policies/{id} returns 200 OK when policy is found")
    void testViewPolicyFound() throws Exception {
        Policy policy = createMockPolicy(1L, "POL-100");
        when(policyService.findById(1L)).thenReturn(Optional.of(policy));
        when(policyService.getDerivedStatus(policy)).thenReturn("ACTIVE");

        mockMvc.perform(get("/policies/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("policies/detail"))
                .andExpect(model().attributeExists("policy"))
                .andExpect(model().attribute("derivedStatus", "ACTIVE"));
    }

    @Test
    @DisplayName("GET /policies/{id} returns 404 view when policy does not exist")
    void testViewPolicyNotFound() throws Exception {
        when(policyService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/policies/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/404"));
    }

    @Test
    @DisplayName("POST /policies/{id}/renew redirects to policy detail page")
    void testMarkAsRenewed() throws Exception {
        mockMvc.perform(post("/policies/1/renew"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/policies/1"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}
