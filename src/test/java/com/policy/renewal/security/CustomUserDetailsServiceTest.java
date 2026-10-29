package com.policy.renewal.security;

import com.policy.renewal.model.Role;
import com.policy.renewal.model.User;
import com.policy.renewal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * CustomUserDetailsServiceTest — unit tests for the Spring Security UserDetailsService bridge.
 *
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    @DisplayName("loadUserByUsername returns UserDetails with ROLE_ADMIN authority for ADMIN user")
    void testLoadUserByUsernameAdmin() {
        User admin = new User("admin", "$2a$10$encodedPasswordHash", "Admin Name", "admin@example.com", Role.ADMIN);
        when(userRepository.findByUsernameIgnoreCase("admin")).thenReturn(Optional.of(admin));

        UserDetails details = userDetailsService.loadUserByUsername("admin");

        assertNotNull(details);
        assertEquals("admin", details.getUsername());
        assertEquals("$2a$10$encodedPasswordHash", details.getPassword());
        assertTrue(details.isEnabled());
        assertTrue(details.isAccountNonExpired());
        assertTrue(details.isAccountNonLocked());
        assertTrue(details.isCredentialsNonExpired());

        assertTrue(details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("loadUserByUsername handles case-insensitivity and trims whitespace")
    void testLoadUserByUsernameCaseInsensitiveAndTrimming() {
        User admin = new User("admin", "$2a$10$encodedPasswordHash", "Admin Name", "admin@example.com", Role.ADMIN);
        when(userRepository.findByUsernameIgnoreCase("Admin")).thenReturn(Optional.of(admin));

        UserDetails details = userDetailsService.loadUserByUsername("  Admin  ");

        assertNotNull(details);
        assertEquals("admin", details.getUsername());
    }

    @Test
    @DisplayName("loadUserByUsername returns UserDetails with ROLE_STAFF authority for STAFF user")
    void testLoadUserByUsernameStaff() {
        User staff = new User("staff1", "$2a$10$staffEncodedPassword", "Staff Member", "staff@example.com", Role.STAFF);
        when(userRepository.findByUsernameIgnoreCase("staff1")).thenReturn(Optional.of(staff));

        UserDetails details = userDetailsService.loadUserByUsername("staff1");

        assertNotNull(details);
        assertEquals("staff1", details.getUsername());
        assertTrue(details.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_STAFF")));
    }

    @Test
    @DisplayName("loadUserByUsername throws UsernameNotFoundException when user does not exist")
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByUsernameIgnoreCase("ghost")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("ghost"));
    }

    @Test
    @DisplayName("loadUserByUsername throws UsernameNotFoundException when username is null or blank")
    void testLoadUserByUsernameNullOrBlank() {
        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername(null));
        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("   "));
    }
}
