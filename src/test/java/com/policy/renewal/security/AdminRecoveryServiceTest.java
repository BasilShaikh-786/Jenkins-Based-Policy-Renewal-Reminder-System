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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * AdminRecoveryServiceTest — unit tests verifying safe password recovery logic,
 * BCrypt hashing, and strict validation guards.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@ExtendWith(MockitoExtension.class)
class AdminRecoveryServiceTest {

    @Mock
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    private AdminRecoveryService recoveryService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        recoveryService = new AdminRecoveryService(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Successfully updates password for existing user with BCrypt hash")
    void testSuccessfulPasswordReset() {
        User existingUser = new User("admin", "$2a$10$oldHashValue", "Administrator", "admin@policyguard.local", Role.ADMIN);
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(existingUser));

        AdminRecoveryResult result = recoveryService.resetPassword("admin", "NewSecurePassword@2026");

        assertTrue(result.isSuccess(), "Recovery must report success for valid existing user and password");
        assertEquals("admin", result.getUsername());
        assertFalse(result.getMessage().contains("NewSecurePassword@2026"), "Message must never contain the plaintext password");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();

        assertEquals("admin", savedUser.getUsername());
        assertTrue(passwordEncoder.matches("NewSecurePassword@2026", savedUser.getPassword()), "New password must match BCrypt hash");
        assertFalse(passwordEncoder.matches("WrongPassword", savedUser.getPassword()));
    }

    @Test
    @DisplayName("Refuses to run when password is null or blank")
    void testRefusesWhenPasswordMissing() {
        AdminRecoveryResult nullResult = recoveryService.resetPassword("admin", null);
        assertFalse(nullResult.isSuccess(), "Recovery must fail if password is null");
        assertTrue(nullResult.getMessage().contains("missing"), "Error message must indicate missing password");

        AdminRecoveryResult blankResult = recoveryService.resetPassword("admin", "   ");
        assertFalse(blankResult.isSuccess(), "Recovery must fail if password is blank");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Refuses to run when password is shorter than 6 characters")
    void testRefusesWhenPasswordTooShort() {
        AdminRecoveryResult shortResult = recoveryService.resetPassword("admin", "12345");
        assertFalse(shortResult.isSuccess(), "Recovery must fail if password is under 6 characters");
        assertTrue(shortResult.getMessage().contains("at least 6 characters"), "Error message must mention length constraint");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Refuses to run and does NOT create a user when target user does not exist")
    void testRefusesWhenTargetUserDoesNotExist() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        AdminRecoveryResult result = recoveryService.resetPassword("nonexistent", "ValidSecret@123");

        assertFalse(result.isSuccess(), "Recovery must fail if user does not exist");
        assertEquals("nonexistent", result.getUsername());
        assertTrue(result.getMessage().contains("does not exist"), "Error message must state user does not exist");

        // Crucial requirement: Do not silently create another administrator
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Refuses to run when username is null or blank")
    void testRefusesWhenUsernameBlank() {
        AdminRecoveryResult nullUserResult = recoveryService.resetPassword(null, "ValidSecret@123");
        assertFalse(nullUserResult.isSuccess(), "Recovery must fail if username is null");

        AdminRecoveryResult blankUserResult = recoveryService.resetPassword("   ", "ValidSecret@123");
        assertFalse(blankUserResult.isSuccess(), "Recovery must fail if username is blank");

        verify(userRepository, never()).save(any());
    }
}
