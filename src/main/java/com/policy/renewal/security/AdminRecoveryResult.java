package com.policy.renewal.security;

/**
 * AdminRecoveryResult — encapsulates the outcome of an administrative password recovery operation.
 *
 * <p>Never stores or exposes plaintext passwords or password hashes.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
public class AdminRecoveryResult {

    private final boolean success;
    private final String message;
    private final String username;

    public AdminRecoveryResult(boolean success, String message, String username) {
        this.success = success;
        this.message = message;
        this.username = username;
    }

    public static AdminRecoveryResult success(String username, String message) {
        return new AdminRecoveryResult(true, message, username);
    }

    public static AdminRecoveryResult failure(String username, String message) {
        return new AdminRecoveryResult(false, message, username);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "AdminRecoveryResult{" +
                "success=" + success +
                ", username='" + username + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
