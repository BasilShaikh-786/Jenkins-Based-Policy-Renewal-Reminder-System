package com.policy.renewal.repository;

import com.policy.renewal.model.Role;
import com.policy.renewal.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository — Spring Data JPA repository for {@link User} entity management.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique username.
     *
     * @param username the username to look up
     * @return an {@link Optional} containing the user if found, or empty
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by username ignoring case (prevents login failure from case mismatches).
     *
     * @param username the username to look up
     * @return an {@link Optional} containing the user if found, or empty
     */
    Optional<User> findByUsernameIgnoreCase(String username);

    /**
     * Checks if a user already exists with the given username.
     *
     * @param username the username to check
     * @return true if a user exists with this username
     */
    boolean existsByUsername(String username);

    /**
     * Checks if a user already exists with the given username (case-insensitive).
     *
     * @param username the username to check
     * @return true if a user exists with this username
     */
    boolean existsByUsernameIgnoreCase(String username);

    /**
     * Checks if any user exists with the given role.
     * Used during startup to detect if any admin account is already present.
     *
     * @param role the role to check
     * @return true if at least one user with this role exists
     */
    boolean existsByRole(Role role);

    /**
     * Counts the number of users registered with the given role.
     *
     * @param role the role to count
     * @return count of users with this role
     */
    long countByRole(Role role);
}
