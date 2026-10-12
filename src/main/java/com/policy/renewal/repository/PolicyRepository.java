package com.policy.renewal.repository;

import com.policy.renewal.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * PolicyRepository — data access layer for the Policy entity.
 *
 * <p>This interface extends {@code JpaRepository}, which provides common database
 * operations for free — no implementation code is needed. Spring Data JPA
 * automatically generates the implementation at startup.
 *
 * <p>Inherited operations include:
 * <ul>
 *   <li>{@code save(policy)} — insert or update a record</li>
 *   <li>{@code findById(id)} — find a record by its primary key</li>
 *   <li>{@code findAll()} — retrieve all records</li>
 *   <li>{@code count()} — count total records</li>
 *   <li>{@code existsById(id)} — check if a record exists</li>
 *   <li>{@code delete(policy)} — remove a record</li>
 * </ul>
 *
 * <p>Custom query methods are added below for application-specific lookups.
 *
 * <p>Project: Jenkins-Based Policy Renewal Reminder System
 * <p>Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
 */
@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    // =========================================================================
    // Unique constraint check
    // =========================================================================

    /**
     * Finds a policy by its business key (policy number).
     * Used by the service layer to check for duplicate policy numbers before saving.
     *
     * @param policyNumber the policy number to search for
     * @return an Optional containing the matching policy, or empty if not found
     */
    Optional<Policy> findByPolicyNumber(String policyNumber);

    // =========================================================================
    // Search methods — used by FR-06 and FR-07
    // =========================================================================

    /**
     * Searches for policies where the customer name contains the given text,
     * case-insensitive. Used by FR-06 (Search by Customer Name).
     *
     * @param name the text to search for within the customer name
     * @return list of matching policies, ordered by expiry date ascending
     */
    List<Policy> findByCustomerNameContainingIgnoreCaseOrderByExpiryDateAsc(String name);

    /**
     * Searches for policies where the policy number contains the given text,
     * case-insensitive. Used by FR-07 (Search by Policy Number).
     *
     * @param policyNumber the text to search for within the policy number
     * @return list of matching policies
     */
    List<Policy> findByPolicyNumberContainingIgnoreCase(String policyNumber);

    // =========================================================================
    // Upcoming renewals — used by FR-09
    // =========================================================================

    /**
     * Finds policies expiring between today (inclusive) and the given cutoff date
     * (inclusive), excluding policies that have been explicitly marked as RENEWED.
     *
     * <p>This query implements FR-09: a policy is "Expiring Soon" if its expiry date
     * falls within the configurable alert window AND it has not been renewed.
     *
     * @param today   today's date (start of the window, inclusive)
     * @param cutoff  the last date of the alert window (today + N days, inclusive)
     * @return list of upcoming-renewal policies, ordered by expiry date ascending
     */
    @Query("SELECT p FROM Policy p " +
           "WHERE p.expiryDate >= :today AND p.expiryDate <= :cutoff " +
           "AND (p.renewalStatus IS NULL OR p.renewalStatus <> 'RENEWED') " +
           "ORDER BY p.expiryDate ASC")
    List<Policy> findUpcomingRenewals(@Param("today") LocalDate today,
                                      @Param("cutoff") LocalDate cutoff);

    // =========================================================================
    // Status-based counts — used by FR-12 (Dashboard)
    // =========================================================================

    /**
     * Counts policies that have been explicitly marked as RENEWED.
     *
     * @return count of renewed policies
     */
    long countByRenewalStatus(String renewalStatus);

    /**
     * Counts policies that have expired (expiry date before today)
     * and have NOT been marked as RENEWED.
     *
     * @param today today's date
     * @return count of expired policies
     */
    @Query("SELECT COUNT(p) FROM Policy p " +
           "WHERE p.expiryDate < :today " +
           "AND (p.renewalStatus IS NULL OR p.renewalStatus <> 'RENEWED')")
    long countExpired(@Param("today") LocalDate today);

    /**
     * Counts policies expiring within the alert window and not renewed.
     *
     * @param today  today's date
     * @param cutoff the end of the alert window (today + N days)
     * @return count of expiring-soon policies
     */
    @Query("SELECT COUNT(p) FROM Policy p " +
           "WHERE p.expiryDate >= :today AND p.expiryDate <= :cutoff " +
           "AND (p.renewalStatus IS NULL OR p.renewalStatus <> 'RENEWED')")
    long countExpiringSoon(@Param("today") LocalDate today,
                           @Param("cutoff") LocalDate cutoff);

    /**
     * Counts active policies (expiry date after the alert window and not renewed).
     *
     * @param cutoff the end of the alert window (today + N days)
     * @return count of active policies
     */
    @Query("SELECT COUNT(p) FROM Policy p " +
           "WHERE p.expiryDate > :cutoff " +
           "AND (p.renewalStatus IS NULL OR p.renewalStatus <> 'RENEWED')")
    long countActive(@Param("cutoff") LocalDate cutoff);

    // =========================================================================
    // All policies sorted by expiry date — used by FR-02
    // =========================================================================

    /**
     * Returns all policies ordered by expiry date ascending (soonest first).
     * This is the default sort order for the policy list.
     *
     * @return all policies sorted by expiry date ascending
     */
    List<Policy> findAllByOrderByExpiryDateAsc();
}
