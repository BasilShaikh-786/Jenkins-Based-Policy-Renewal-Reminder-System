package com.policy.renewal.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthControllerTest — unit tests for authentication views and access-denied pages.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@WebMvcTest(AuthController.class)
@Import(com.policy.renewal.security.SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /login renders custom login view with 200 OK")
    void testShowLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeDoesNotExist("errorMessage"))
                .andExpect(model().attributeDoesNotExist("successMessage"));
    }

    @Test
    @DisplayName("GET /login?error=true populates error message on model")
    void testLoginPageWithError() throws Exception {
        mockMvc.perform(get("/login").param("error", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("GET /login?logout=true populates logout success message on model")
    void testLoginPageWithLogout() throws Exception {
        mockMvc.perform(get("/login").param("logout", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/login"))
                .andExpect(model().attributeExists("successMessage"));
    }

    @Test
    @DisplayName("GET /access-denied returns 403 Forbidden status and renders error/403 view")
    void testAccessDeniedPage() throws Exception {
        mockMvc.perform(get("/access-denied"))
                .andExpect(status().isForbidden())
                .andExpect(view().name("error/403"));
    }
}
