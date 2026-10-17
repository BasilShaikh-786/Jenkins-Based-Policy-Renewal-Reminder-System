package com.policy.renewal.dto;

import com.policy.renewal.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * UserRegistrationDto — form backing data transfer object for creating new user accounts.
 *
 * <p>Enforces server-side validation rules before user creation:
 * <ul>
 *   <li>Username: 3 to 50 characters, non-blank</li>
 *   <li>Password: at least 6 characters, non-blank (hashed with BCrypt before persistence)</li>
 *   <li>Full Name: non-blank, up to 100 characters</li>
 *   <li>Email: optional, valid email format</li>
 *   <li>Role: required (ADMIN or STAFF)</li>
 * </ul>
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
public class UserRegistrationDto {

    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters.")
    private String username;

    @NotBlank(message = "Password is required.")
    @Size(min = 6, message = "Password must be at least 6 characters.")
    private String password;

    @NotBlank(message = "Full name is required.")
    @Size(max = 100, message = "Full name must not exceed 100 characters.")
    private String fullName;

    @Email(message = "Please enter a valid email address.")
    @Size(max = 150, message = "Email must not exceed 150 characters.")
    private String email;

    @NotNull(message = "Please select a role.")
    private Role role = Role.STAFF;

    public UserRegistrationDto() {
    }

    public UserRegistrationDto(String username, String password, String fullName, String email, Role role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
