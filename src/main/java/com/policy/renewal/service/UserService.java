package com.policy.renewal.service;

import com.policy.renewal.dto.UserRegistrationDto;
import com.policy.renewal.model.Role;
import com.policy.renewal.model.User;
import com.policy.renewal.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * UserService — handles business logic and persistence operations for user accounts.
 *
 * <p>Guarantees:
 * <ul>
 *   <li>All passwords are hashed with {@link PasswordEncoder} (BCrypt) before persistence.</li>
 *   <li>Usernames are unique across the database.</li>
 *   <li>Transactional integrity for all mutations.</li>
 * </ul>
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private AuditLogService auditLogService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this(userRepository, passwordEncoder, null);
    }

    @org.springframework.beans.factory.annotation.Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       @org.springframework.beans.factory.annotation.Autowired(required = false) AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    public void setAuditLogService(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    /**
     * Retrieves all registered user accounts.
     *
     * @return list of all users
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Looks up a user by ID.
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Looks up a user by username.
     */
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        return userRepository.findByUsername(username.trim());
    }

    /**
     * Checks if a username is already taken.
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return userRepository.existsByUsername(username.trim());
    }

    /**
     * Checks whether any administrator account exists in the database.
     */
    @Transactional(readOnly = true)
    public boolean hasAdmin() {
        return userRepository.existsByRole(Role.ADMIN);
    }

    /**
     * Registers a new user from a validated DTO form.
     *
     * @param dto the registration form backing object
     * @return the saved {@link User}
     * @throws IllegalArgumentException if the username is already taken
     */
    @Transactional
    public User registerUser(UserRegistrationDto dto) {
        return registerUser(
                dto.getUsername(),
                dto.getPassword(),
                dto.getFullName(),
                dto.getEmail(),
                dto.getRole()
        );
    }

    /**
     * Registers a new user with BCrypt password hashing.
     * Plaintext passwords are never saved.
     *
     * @param username    unique username
     * @param rawPassword plaintext password to be hashed
     * @param fullName    full display name
     * @param email       optional email address
     * @param role        user role (ADMIN or STAFF)
     * @return the persisted {@link User}
     */
    @Transactional
    public User registerUser(String username, String rawPassword, String fullName, String email, Role role) {
        if (username == null || username.trim().length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters.");
        }
        String cleanUsername = username.trim();
        if (userRepository.existsByUsername(cleanUsername)) {
            throw new IllegalArgumentException("Username '" + cleanUsername + "' is already registered.");
        }
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
        if (role == null) {
            throw new IllegalArgumentException("A user role must be selected.");
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(cleanUsername, hashedPassword, fullName, email, role);
        User saved = userRepository.save(user);

        log.info("[SECURITY-AUDIT] Created new user '{}' with role '{}' (ID: {})",
                saved.getUsername(), saved.getRole(), saved.getId());
        if (auditLogService != null) {
            auditLogService.logSuccess(com.policy.renewal.model.AuditLog.ACTION_USER_REGISTERED,
                    com.policy.renewal.model.AuditLog.RESOURCE_USER, saved.getUsername(),
                    "Registered user '" + saved.getUsername() + "' with role " + saved.getRole());
        }
        return saved;
    }
}
