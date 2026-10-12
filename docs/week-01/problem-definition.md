# Problem Definition

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C  
**Date:** October 2026

---

## 1. What Is an Insurance Policy?

An **insurance policy** is a formal written contract between a customer and an insurance company. In this contract, the customer pays a regular fee called a **premium**, and in return the insurance company promises to provide financial protection against specific risks — for example, a health policy covers medical expenses, a vehicle policy covers accident damage, and a life policy provides a payout to the customer's family if the customer passes away.

Every insurance policy has two important dates:

- **Start date** — the date on which the policy becomes active and protection begins.
- **Expiry date** — the date on which the policy ends and protection stops unless the policy is renewed.

---

## 2. What Is Policy Renewal?

**Policy renewal** is the process of extending an insurance policy beyond its original expiry date. Before the expiry date arrives, the customer must pay the next premium and confirm that they wish to continue their coverage. If renewal does not happen before the expiry date, the policy lapses and the customer loses their protection.

Renewal is a time-sensitive, recurring process. A policy that lapses even for a single day may leave the customer unprotected. Many policies must be renewed annually, but some are renewed quarterly or on other cycles.

---

## 3. Why Do Agents and Administrators Need to Monitor Expiry Dates?

An insurance agent or office administrator typically manages a large number of policies simultaneously — sometimes hundreds or thousands of them. Their responsibilities include:

- Knowing which policies are expiring soon so that customers can be contacted in advance.
- Identifying which policies have already expired without being renewed so that corrective action can be taken.
- Maintaining accurate records of each policy's current status (active, expired, or renewed).
- Reporting on the portfolio of policies they manage.

Without a system that highlights upcoming and past expiry dates, the agent must manually review every policy record to find those that need attention. This manual review is slow, error-prone and unsustainable as the number of policies grows.

---

## 4. Problems That Occur When Renewals Are Tracked Manually

Manual tracking — using notebooks, spreadsheets or paper files — introduces the following problems:

| Problem | Consequence |
|---|---|
| Human error in reading expiry dates | A policy that should be renewed is missed. |
| No automatic sorting by urgency | Agents spend time searching instead of working. |
| No standardised status classification | Different agents use different terminology, causing confusion. |
| Duplicate policy entries | The same customer appears multiple times with conflicting information. |
| Delayed discovery of expired policies | Customers remain unprotected without realising it. |
| Difficult searching and filtering | Finding a policy by name or number requires scanning all records. |
| No audit trail | There is no record of when a policy was updated or by whom. |
| Fragile data | Spreadsheets are easily corrupted, lost or accidentally overwritten. |
| No statistics or reporting | Management cannot see the health of the policy portfolio at a glance. |

These problems directly affect customers, who may experience gaps in coverage, and they expose the insurance office to reputational and legal risk.

---

## 5. How a Policy Renewal Reminder System Improves the Process

A web-based policy renewal reminder system addresses each of the above problems:

- **Centralised records** — all policy data is stored in a single database, preventing duplicates and data loss.
- **Automatic status classification** — the system uses dates to identify which policies are active, expiring soon, already expired, or renewed, eliminating manual checking.
- **Dashboard statistics** — the home page displays counts by status, giving managers an immediate overview.
- **Search and filter** — agents can find any policy by customer name or policy number in seconds.
- **Upcoming renewal alerts** — the system lists policies expiring within a configurable window (e.g., the next 30 days), so agents know exactly who needs to be contacted.
- **Validation** — the system rejects invalid data such as an expiry date that precedes the start date, ensuring data quality.
- **Consistent terminology** — all statuses are defined by the system, preventing inconsistent labelling.

> **Important clarification:** At this stage of the project, the system will *identify* and *display* policies that need renewal. It will **not** automatically send emails or SMS messages to customers. That functionality is a potential future enhancement and requires additional planning and approval.

---

## 6. Summary

The core problem is that insurance policy renewal is a time-critical, recurring process that is currently tracked manually by agents and administrators, leading to missed renewals, data errors, wasted time and inadequate oversight. A structured web application can solve this problem by providing a reliable, searchable, centrally managed record of all policies, with automatic identification of upcoming and overdue renewals and a dashboard that gives managers immediate situational awareness.
