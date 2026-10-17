package com.policy.renewal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * AuthController — handles authentication flow pages including login and access denied.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Controller
public class AuthController {

    /**
     * Renders the custom PolicyGuard login page.
     *
     * @param error  presence indicates an authentication failure
     * @param logout presence indicates a successful logout event
     * @param model  Spring MVC model
     * @return view name "auth/login"
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(name = "error", required = false) String error,
            @RequestParam(name = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("errorMessage", "Invalid username or password. Please try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been successfully logged out.");
        }

        return "auth/login";
    }

    /**
     * Renders the 403 Forbidden access denied page.
     *
     * @return view name "error/403"
     */
    @GetMapping("/access-denied")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String accessDenied() {
        return "error/403";
    }
}
