package com.policy.renewal.controller;

import com.policy.renewal.dto.UserRegistrationDto;
import com.policy.renewal.model.Role;
import com.policy.renewal.model.User;
import com.policy.renewal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * UserController — administrative controller for managing user accounts and roles.
 *
 * <p>Restricted to users with role {@code ADMIN}.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Controller
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Lists all registered user accounts with their assigned roles.
     * Route: GET /users
     */
    @GetMapping
    public String listUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users/list";
    }

    /**
     * Displays form to register a new user account.
     * Route: GET /users/new
     */
    @GetMapping("/new")
    public String showCreateUserForm(Model model) {
        if (!model.containsAttribute("userDto")) {
            model.addAttribute("userDto", new UserRegistrationDto());
        }
        model.addAttribute("availableRoles", Role.values());
        return "users/form";
    }

    /**
     * Handles submission of the new user form.
     * Route: POST /users
     */
    @PostMapping
    public String createUser(
            @Valid @ModelAttribute("userDto") UserRegistrationDto userDto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("availableRoles", Role.values());
            return "users/form";
        }

        try {
            User created = userService.registerUser(userDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "User account '" + created.getUsername() + "' (" + created.getRole() + ") created successfully!");
            return "redirect:/users";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("availableRoles", Role.values());
            return "users/form";
        }
    }
}
