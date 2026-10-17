package com.policy.renewal.controller;

import com.policy.renewal.dto.UserRegistrationDto;
import com.policy.renewal.model.Role;
import com.policy.renewal.model.User;
import com.policy.renewal.service.UserService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserControllerTest — web layer unit tests for administrative user management endpoints.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@WebMvcTest(UserController.class)
@Import({com.policy.renewal.security.SecurityConfig.class, AuthController.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("GET /users as ADMIN returns 200 OK and renders users/list view")
    void testListUsersAsAdmin() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/list"))
                .andExpect(model().attributeExists("users"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("GET /users/new as ADMIN returns 200 OK and renders users/form view")
    void testShowCreateUserFormAsAdmin() throws Exception {
        mockMvc.perform(get("/users/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/form"))
                .andExpect(model().attributeExists("userDto"))
                .andExpect(model().attributeExists("availableRoles"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST /users as ADMIN with valid parameters registers user and redirects")
    void testCreateUserSuccessAsAdmin() throws Exception {
        User createdUser = new User("newofficer", "hash", "New Officer", "officer@example.com", Role.STAFF);
        createdUser.setId(2L);
        when(userService.registerUser(any(UserRegistrationDto.class))).thenReturn(createdUser);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .param("username", "newofficer")
                        .param("password", "SecureP@ss123")
                        .param("fullName", "New Officer")
                        .param("email", "officer@example.com")
                        .param("role", "STAFF"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(userService, times(1)).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST /users as ADMIN with validation errors returns form view with errors")
    void testCreateUserValidationErrorAsAdmin() throws Exception {
        mockMvc.perform(post("/users")
                        .with(csrf())
                        .param("username", "ab") // too short, min is 3
                        .param("password", "123") // too short, min is 6
                        .param("fullName", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("users/form"))
                .andExpect(model().attributeHasFieldErrors("userDto", "username", "password", "fullName"));

        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST /users as ADMIN with duplicate username displays error message on form")
    void testCreateUserDuplicateUsernameAsAdmin() throws Exception {
        when(userService.registerUser(any(UserRegistrationDto.class)))
                .thenThrow(new IllegalArgumentException("Username 'admin' is already registered."));

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .param("username", "admin")
                        .param("password", "Secret123")
                        .param("fullName", "Admin Duplicate")
                        .param("email", "admin@example.com")
                        .param("role", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/form"))
                .andExpect(model().attribute("errorMessage", "Username 'admin' is already registered."));
    }

    @Test
    @WithMockUser(roles = {"STAFF"})
    @DisplayName("GET /users as STAFF returns 403 Forbidden")
    void testListUsersForbiddenForStaff() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"STAFF"})
    @DisplayName("GET /users/new as STAFF returns 403 Forbidden")
    void testShowCreateUserFormForbiddenForStaff() throws Exception {
        mockMvc.perform(get("/users/new"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"STAFF"})
    @DisplayName("POST /users as STAFF returns 403 Forbidden")
    void testCreateUserForbiddenForStaff() throws Exception {
        mockMvc.perform(post("/users")
                        .with(csrf())
                        .param("username", "intruder")
                        .param("password", "Secret123")
                        .param("fullName", "Intruder")
                        .param("role", "ADMIN"))
                .andExpect(status().isForbidden());

        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    @DisplayName("POST /users without CSRF token returns 403 Forbidden")
    void testCreateUserWithoutCsrfForbidden() throws Exception {
        mockMvc.perform(post("/users")
                        .param("username", "nocsrf")
                        .param("password", "Secret123")
                        .param("fullName", "No Csrf")
                        .param("role", "STAFF"))
                .andExpect(status().isForbidden());

        verify(userService, never()).registerUser(any(UserRegistrationDto.class));
    }

    @Test
    @DisplayName("GET /users without authentication redirects to login")
    void testListUsersUnauthenticatedRedirectsToLogin() throws Exception {
        mockMvc.perform(get("/users").accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }
}
