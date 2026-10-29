package com.policy.renewal.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

/**
 * SecurityConfig — Central Spring Security configuration for the Policy Renewal Reminder System.
 *
 * <p>Enforces:
 * <ul>
 *   <li>Database-backed user authentication with BCrypt password hashing.</li>
 *   <li>Role-Based Access Control (RBAC) separating ADMIN and STAFF capabilities.</li>
 *   <li>Protection of all sensitive routes by default.</li>
 *   <li>CSRF protection on all mutating web endpoints (POST/DELETE).</li>
 *   <li>Session fixation protection and secure cookie invalidation on logout.</li>
 *   <li>HTTP Basic authentication support for automated CI/CD API endpoints.</li>
 *   <li>H2 console frame options and CSRF bypass restricted exclusively to ADMIN.</li>
 * </ul>
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // -----------------------------------------------------------------
            // Route Authorization Matrix (RBAC)
            // -----------------------------------------------------------------
            .authorizeHttpRequests(auth -> auth
                // Static resources — public access
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()

                // Public authentication, error, and container health routes (Milestone 6)
                .requestMatchers("/login", "/access-denied", "/error/**", "/health", "/actuator/health").permitAll()

                // Operational routes — accessible to both ADMIN and STAFF
                .requestMatchers(HttpMethod.GET, "/", "/policies", "/policies/upcoming").hasAnyRole("ADMIN", "STAFF")
                .requestMatchers(HttpMethod.POST, "/policies/*/renew").hasAnyRole("ADMIN", "STAFF")
                .requestMatchers("/api/coverage/**").hasAnyRole("ADMIN", "STAFF")

                // Policy mutations & full customer record — strictly restricted to ADMIN
                .requestMatchers(HttpMethod.GET, "/policies/new", "/policies/*/edit", "/policies/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/policies", "/policies/*").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/policies/*/delete").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/policies/*").hasRole("ADMIN")

                // Reminder engine audit logs & manual triggers — strictly restricted to ADMIN
                .requestMatchers("/reminders", "/reminders/trigger", "/api/reminders/trigger").hasRole("ADMIN")

                // User account management — strictly restricted to ADMIN
                .requestMatchers("/users/**").hasRole("ADMIN")

                // Milestone 5: System audit logs & database backup management — strictly restricted to ADMIN
                .requestMatchers("/audit/**", "/backups/**", "/api/backups/**").hasRole("ADMIN")

                // H2 Database Console — strictly restricted to ADMIN
                .requestMatchers("/h2-console/**").hasRole("ADMIN")

                // Any other unspecified route requires full authentication
                .anyRequest().authenticated()
            )

            // -----------------------------------------------------------------
            // Form Login Configuration
            // -----------------------------------------------------------------
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", false)
                .failureUrl("/login?error=true")
                .permitAll()
            )

            // -----------------------------------------------------------------
            // Logout Configuration
            // -----------------------------------------------------------------
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )

            // -----------------------------------------------------------------
            // HTTP Basic Auth (for automated Jenkins CI/CD /api endpoints)
            // -----------------------------------------------------------------
            .httpBasic(Customizer.withDefaults())

            // -----------------------------------------------------------------
            // CSRF Protection
            // Enabled by default; exempted only for stateless REST API & H2 console
            // -----------------------------------------------------------------
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**", "/h2-console/**")
            )

            // -----------------------------------------------------------------
            // Frame Options (Allow same-origin for embedded H2 console)
            // -----------------------------------------------------------------
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )

            // -----------------------------------------------------------------
            // Session Management Security
            // -----------------------------------------------------------------
            .sessionManagement(session -> session
                .sessionFixation(fixation -> fixation.migrateSession())
            )

            // -----------------------------------------------------------------
            // Access Denied (403 Forbidden) Handling
            // -----------------------------------------------------------------
            .exceptionHandling(exceptions -> exceptions
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }
}
