package com.policy.renewal.security;

import com.policy.renewal.model.Policy;
import com.policy.renewal.repository.PolicyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SecurityIntegrationTest — end-to-end integration tests verifying Spring Security
 * authentication, logout, Role-Based Access Control (RBAC), CSRF protection,
 * and HTTP Basic authentication for CI/CD endpoints.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PolicyRepository policyRepository;

    // =========================================================================
    // Authentication & Logout Flow Tests (Task 3)
    // =========================================================================

    @Test
    @DisplayName("Form login with valid credentials authenticates user and redirects to home")
    void testSuccessfulFormLoginAndRedirect() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "testadmin")
                        .param("password", "TestAdmin@123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("testadmin").withRoles("ADMIN"));
    }

    @Test
    @DisplayName("Form login with invalid credentials rejects and redirects to /login?error=true")
    void testFailedFormLoginRedirectsWithError() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "testadmin")
                        .param("password", "WrongSecretPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("Logout flow invalidates session and redirects to /login?logout=true")
    void testLogoutFlowWithCsrf() throws Exception {
        // First log in to obtain session
        MvcResult loginResult = mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "testadmin")
                        .param("password", "TestAdmin@123"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession();
        assertNotNull(session);

        // Perform browser logout with session, accept header, and csrf
        mockMvc.perform(post("/logout")
                        .session(session)
                        .accept(MediaType.TEXT_HTML)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout=true"))
                .andExpect(unauthenticated());
    }

    // =========================================================================
    // Unauthorized Direct URL Access Tests (Task 6)
    // =========================================================================

    @Test
    @DisplayName("Unauthenticated access to protected routes redirects to /login")
    void testUnauthenticatedDirectUrlAccessRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/").accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        mockMvc.perform(get("/policies").accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        mockMvc.perform(get("/policies/new").accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        mockMvc.perform(get("/reminders").accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        mockMvc.perform(get("/users").accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @DisplayName("Static CSS/JS resources are publicly accessible without authentication")
    void testStaticAssetsPubliclyAccessible() throws Exception {
        mockMvc.perform(get("/css/style.css"))
                .andExpect(status().isOk());
    }

    // =========================================================================
    // Role-Based Access Control (RBAC) Tests (Tasks 4 & 5)
    // =========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("ADMIN role has full access to overview, policies, mutations, reminders, and users")
    void testAdminRolePermissionsOnRealDatabase() throws Exception {
        // Read views
        mockMvc.perform(get("/")).andExpect(status().isOk());
        mockMvc.perform(get("/policies")).andExpect(status().isOk());
        mockMvc.perform(get("/policies/upcoming")).andExpect(status().isOk());
        mockMvc.perform(get("/policies/new")).andExpect(status().isOk());
        mockMvc.perform(get("/reminders")).andExpect(status().isOk());
        mockMvc.perform(get("/users")).andExpect(status().isOk());

        // Policy mutation (create)
        mockMvc.perform(post("/policies")
                        .with(csrf())
                        .param("policyNumber", "POL-INTEG-SEC")
                        .param("customerName", "Security Customer")
                        .param("customerEmail", "sec@example.com")
                        .param("policyType", "Health")
                        .param("startDate", "2026-01-01")
                        .param("expiryDate", "2026-12-31")
                        .param("premiumAmount", "7500.00"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "staff", roles = {"STAFF"})
    @DisplayName("STAFF role has access to operational dashboard, policy list, and renewals")
    void testStaffPermittedRoutes() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
        mockMvc.perform(get("/policies")).andExpect(status().isOk());
        mockMvc.perform(get("/policies/upcoming")).andExpect(status().isOk());

        // Create a test policy for renewal test
        Policy policy = new Policy();
        policy.setPolicyNumber("POL-RENEW-TEST");
        policy.setCustomerName("Renew Customer");
        policy.setPolicyType("Health");
        policy.setStartDate(LocalDate.of(2026, 1, 1));
        policy.setExpiryDate(LocalDate.of(2026, 10, 20));
        policy.setPremiumAmount(new BigDecimal("5000.00"));
        Policy saved = policyRepository.save(policy);

        // Staff can mark policy renewed
        mockMvc.perform(post("/policies/" + saved.getId() + "/renew").with(csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "staff", roles = {"STAFF"})
    @DisplayName("STAFF role is strictly FORBIDDEN from administrative routes and policy mutations")
    void testStaffForbiddenRoutes() throws Exception {
        // Customer profile dossier & reminder history view restricted to ADMIN
        mockMvc.perform(get("/policies/1")).andExpect(status().isForbidden());

        // Policy creation restricted to ADMIN
        mockMvc.perform(get("/policies/new")).andExpect(status().isForbidden());
        mockMvc.perform(post("/policies").with(csrf())
                        .param("policyNumber", "POL-BAD")
                        .param("customerName", "Bad"))
                .andExpect(status().isForbidden());

        // Policy editing restricted to ADMIN
        mockMvc.perform(get("/policies/1/edit")).andExpect(status().isForbidden());

        // Policy deletion restricted to ADMIN
        mockMvc.perform(post("/policies/1/delete").with(csrf()))
                .andExpect(status().isForbidden());

        // Reminder logs & trigger restricted to ADMIN
        mockMvc.perform(get("/reminders")).andExpect(status().isForbidden());
        mockMvc.perform(post("/reminders/trigger").with(csrf()))
                .andExpect(status().isForbidden());

        // User management restricted to ADMIN
        mockMvc.perform(get("/users")).andExpect(status().isForbidden());
        mockMvc.perform(post("/users").with(csrf())
                        .param("username", "evil")
                        .param("password", "Pass1234")
                        .param("fullName", "Evil")
                        .param("role", "ADMIN"))
                .andExpect(status().isForbidden());
    }

    // =========================================================================
    // CSRF Protection Tests (Task 7)
    // =========================================================================

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Mutating POST requests without CSRF token are rejected with 403 Forbidden")
    void testCsrfProtectionEnforcedOnMutations() throws Exception {
        mockMvc.perform(post("/policies")
                        .param("policyNumber", "POL-NOCSRF")
                        .param("customerName", "No Csrf"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/policies/1/delete"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/users")
                        .param("username", "nocsrfuser")
                        .param("password", "Password@123")
                        .param("fullName", "No Csrf")
                        .param("role", "STAFF"))
                .andExpect(status().isForbidden());
    }

    // =========================================================================
    // HTTP Basic Authentication for Automated CI/CD (Task 12)
    // =========================================================================

    @Test
    @DisplayName("HTTP Basic auth on /api/reminders/trigger succeeds for ADMIN and does not require CSRF")
    void testHttpBasicAuthForCiCdApiSuccess() throws Exception {
        mockMvc.perform(post("/api/reminders/trigger")
                        .with(httpBasic("testadmin", "TestAdmin@123"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @DisplayName("HTTP Basic auth on /api/reminders/trigger is forbidden for STAFF")
    void testHttpBasicAuthForCiCdApiForbiddenForStaff() throws Exception {
        mockMvc.perform(post("/api/reminders/trigger")
                        .with(httpBasic("teststaff", "TestStaff@123"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("HTTP Basic auth on /api/reminders/trigger is unauthorized for invalid credentials")
    void testHttpBasicAuthForCiCdApiUnauthorized() throws Exception {
        mockMvc.perform(post("/api/reminders/trigger")
                        .with(httpBasic("testadmin", "WrongPassword"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
