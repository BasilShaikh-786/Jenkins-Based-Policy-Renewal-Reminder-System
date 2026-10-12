package com.policy.renewal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PolicyRenewalApplication — Spring Boot entry point.
 *
 * <p>This is the main class that starts the application. The {@code @SpringBootApplication}
 * annotation is a shortcut that enables three important Spring Boot features:
 * <ul>
 *   <li>{@code @Configuration} — this class can define Spring beans.</li>
 *   <li>{@code @EnableAutoConfiguration} — Spring Boot automatically configures the
 *       application based on the dependencies in the classpath (e.g., it detects H2
 *       and configures a data source automatically).</li>
 *   <li>{@code @ComponentScan} — Spring scans this package and all sub-packages for
 *       components such as controllers, services and repositories.</li>
 * </ul>
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@SpringBootApplication
public class PolicyRenewalApplication {

    /**
     * Application entry point. Boots the embedded Tomcat server and initialises
     * the Spring application context.
     *
     * @param args command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        SpringApplication.run(PolicyRenewalApplication.class, args);
    }
}
