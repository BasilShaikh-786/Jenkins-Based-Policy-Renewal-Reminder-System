package com.policy.renewal.service;

import com.policy.renewal.dto.UserRegistrationDto;
import com.policy.renewal.model.Role;
import com.policy.renewal.model.User;
import com.policy.renewal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UserServiceTest — unit tests for user account management and password security.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("registerUser hashes password using BCrypt and never stores plaintext")
    void testRegisterUserHashesPassword() {
        String rawPassword = "MySecretPassword@123";
        when(userRepository.existsByUsername("jdoe")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.registerUser("jdoe", rawPassword, "John Doe", "jdoe@example.com", Role.STAFF);

        assertNotNull(user);
        assertNotEquals(rawPassword, user.getPassword(), "Database user password must never be stored in plaintext");
        assertTrue(passwordEncoder.matches(rawPassword, user.getPassword()), "Encoded password must match via BCrypt");
        assertEquals("jdoe", user.getUsername());
        assertEquals(Role.STAFF, user.getRole());
    }

    @Test
    @DisplayName("registerUser from DTO delegates correctly with BCrypt hashing")
    void testRegisterUserFromDto() {
        UserRegistrationDto dto = new UserRegistrationDto("admin2", "AdminSecret1", "Admin Two", "admin2@example.com", Role.ADMIN);
        when(userRepository.existsByUsername("admin2")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User user = userService.registerUser(dto);

        assertNotNull(user);
        assertTrue(passwordEncoder.matches("AdminSecret1", user.getPassword()));
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    @DisplayName("registerUser rejects duplicate username with IllegalArgumentException")
    void testRegisterUserRejectsDuplicateUsername() {
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("existingUser", "ValidPass123", "Existing", "e@example.com", Role.STAFF));

        assertTrue(ex.getMessage().contains("already registered"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("registerUser rejects username shorter than 3 characters")
    void testRegisterUserRejectsShortUsername() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("ab", "ValidPass123", "Short", "s@example.com", Role.STAFF));

        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("  ", "ValidPass123", "Blank", "b@example.com", Role.STAFF));

        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser(null, "ValidPass123", "Null", "n@example.com", Role.STAFF));
    }

    @Test
    @DisplayName("registerUser rejects password shorter than 6 characters")
    void testRegisterUserRejectsShortPassword() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("validuser", "12345", "Short Pass", "sp@example.com", Role.STAFF));

        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("validuser", null, "Null Pass", "np@example.com", Role.STAFF));
    }

    @Test
    @DisplayName("registerUser rejects null role")
    void testRegisterUserRejectsNullRole() {
        assertThrows(IllegalArgumentException.class, () ->
                userService.registerUser("validuser", "ValidPass123", "No Role", "nr@example.com", null));
    }

    @Test
    @DisplayName("getAllUsers delegates to repository findAll")
    void testGetAllUsers() {
        User u1 = new User("u1", "h1", "User 1", "u1@example.com", Role.STAFF);
        User u2 = new User("u2", "h2", "User 2", "u2@example.com", Role.ADMIN);
        when(userRepository.findAll()).thenReturn(List.of(u1, u2));

        List<User> result = userService.getAllUsers();
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("findById retrieves user from repository")
    void testFindById() {
        User user = new User("u1", "h1", "User 1", "u1@example.com", Role.STAFF);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertTrue(userService.findById(1L).isPresent());
        assertTrue(userService.findById(999L).isEmpty());
    }

    @Test
    @DisplayName("findByUsername retrieves user when present, handles null/blank safely")
    void testFindByUsername() {
        User user = new User("targetUser", "hash", "Target", "t@example.com", Role.STAFF);
        when(userRepository.findByUsername("targetUser")).thenReturn(Optional.of(user));

        assertTrue(userService.findByUsername("targetUser").isPresent());
        assertTrue(userService.findByUsername("unknown").isEmpty());
        assertTrue(userService.findByUsername(null).isEmpty());
        assertTrue(userService.findByUsername("   ").isEmpty());
    }

    @Test
    @DisplayName("existsByUsername returns true when user exists, false otherwise")
    void testExistsByUsername() {
        when(userRepository.existsByUsername("admin")).thenReturn(true);
        when(userRepository.existsByUsername("unknown")).thenReturn(false);

        assertTrue(userService.existsByUsername("admin"));
        assertFalse(userService.existsByUsername("unknown"));
        assertFalse(userService.existsByUsername(null));
        assertFalse(userService.existsByUsername("  "));
    }

    @Test
    @DisplayName("hasAdmin queries existsByRole with Role.ADMIN")
    void testHasAdmin() {
        when(userRepository.existsByRole(Role.ADMIN)).thenReturn(true);
        assertTrue(userService.hasAdmin());

        when(userRepository.existsByRole(Role.ADMIN)).thenReturn(false);
        assertFalse(userService.hasAdmin());
    }
}
