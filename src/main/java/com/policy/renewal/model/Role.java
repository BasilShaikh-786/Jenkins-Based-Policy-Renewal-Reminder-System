package com.policy.renewal.model;

/**
 * Role — Enumeration of user roles within the Policy Renewal Reminder System.
 *
 * <p>Implements Role-Based Access Control (RBAC) requirements:
 * <ul>
 *   <li>{@link #ADMIN}: Full administrative privileges (manage users, create/edit/delete policies,
 *       view full customer profiles, trigger reminders, view reminder logs).</li>
 *   <li>{@link #STAFF}: Operational privileges (view overview dashboard, search/filter policies,
 *       view upcoming renewals, mark policies as renewed). Restricted from policy mutation,
 *       customer profile dossiers, reminder history, manual triggers, and user administration.</li>
 * </ul>
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
public enum Role {
    ADMIN,
    STAFF;

    /**
     * @return the Spring Security granted authority string format (e.g. "ROLE_ADMIN").
     */
    public String asAuthority() {
        return "ROLE_" + this.name();
    }
}
