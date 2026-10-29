package com.policy.renewal.security;

import com.policy.renewal.model.Role;
import com.policy.renewal.model.User;
import com.policy.renewal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * SecurityDataInitializerTest — unit tests for safe first-admin and initial user bootstrap.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@ExtendWith(MockitoExtension.class)
class SecurityDataInitializerTest {

    @Mock
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    private SecurityDataInitializer initializer;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        initializer = new SecurityDataInitializer(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Skips admin bootstrap if an ADMIN already exists in the database")
    void testSkipsAdminBootstrapWhenAdminExists() {
        when(userRepository.existsByRole(Role.ADMIN)).thenReturn(true);

        initializer.run();

        verify(userRepository, never()).save(argThat(user -> user.getRole() == Role.ADMIN));
    }

    @Test
    @DisplayName("Bootstraps admin user with configured password when none exists")
    void testBootstrapsAdminWithConfiguredPassword() {
        when(userRepository.existsByRole(Role.ADMIN)).thenReturn(false);
        ReflectionTestUtils.setField(initializer, "initialAdminUsername", "superadmin");
        ReflectionTestUtils.setField(initializer, "initialAdminPassword", "ExplicitSecret@123");

        initializer.run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertEquals("superadmin", saved.getUsername());
        assertEquals(Role.ADMIN, saved.getRole());
        assertTrue(passwordEncoder.matches("ExplicitSecret@123", saved.getPassword()));
    }

    @Test
    @DisplayName("Bootstraps admin user with generated secure password when password property is empty")
    void testBootstrapsAdminWithGeneratedPassword() {
        when(userRepository.existsByRole(Role.ADMIN)).thenReturn(false);
        ReflectionTestUtils.setField(initializer, "initialAdminUsername", "admin");
        ReflectionTestUtils.setField(initializer, "initialAdminPassword", "");

        initializer.run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertEquals("admin", saved.getUsername());
        assertEquals(Role.ADMIN, saved.getRole());
        assertNotNull(saved.getPassword());
        assertTrue(saved.getPassword().startsWith("$2a$"), "Password must be BCrypt encoded");
    }

    @Test
    @DisplayName("Bootstraps configured staff user when credentials are provided and user does not exist")
    void testBootstrapsStaffUserWhenConfigured() {
        when(userRepository.existsByRole(Role.ADMIN)).thenReturn(true); // admin already exists
        ReflectionTestUtils.setField(initializer, "initialStaffUsername", "stafftester");
        ReflectionTestUtils.setField(initializer, "initialStaffPassword", "StaffSecret@123");
        when(userRepository.existsByUsernameIgnoreCase("stafftester")).thenReturn(false);
        when(userRepository.existsByRole(Role.STAFF)).thenReturn(false);

        initializer.run();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertEquals("stafftester", saved.getUsername());
        assertEquals(Role.STAFF, saved.getRole());
        assertTrue(passwordEncoder.matches("StaffSecret@123", saved.getPassword()));
    }

    @Test
    @DisplayName("Skips staff bootstrap when staff username already exists")
    void testSkipsStaffBootstrapWhenUserExists() {
        when(userRepository.existsByRole(Role.ADMIN)).thenReturn(true);
        ReflectionTestUtils.setField(initializer, "initialStaffUsername", "stafftester");
        ReflectionTestUtils.setField(initializer, "initialStaffPassword", "StaffSecret@123");
        when(userRepository.existsByUsernameIgnoreCase("stafftester")).thenReturn(true);

        initializer.run();

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Skips all bootstrap operations when admin recovery mode is enabled")
    void testSkipsBootstrapWhenRecoveryModeActive() {
        ReflectionTestUtils.setField(initializer, "recoveryModeEnabled", true);

        initializer.run();

        verify(userRepository, never()).existsByRole(any());
        verify(userRepository, never()).save(any());
    }
}
