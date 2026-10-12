# User Stories

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C  
**Source:** Week 1 Functional Requirements (FR-01 through FR-15)

---

## How to Read This Document

Each user story follows the format:  
**"As a [user role], I want [functionality], so that [benefit]."**

Stories are numbered US-01, US-02, etc. Each story traces back to one or more functional requirements.

User roles used in this document:
- **Agent** — Insurance Agent / Policy Manager (primary user)
- **Admin** — System Administrator (full access)
- **Viewer** — Read-Only Viewer / Reporting Manager

---

## US-01 — Create a New Policy

| Field | Detail |
|---|---|
| **Story ID** | US-01 |
| **User Role** | Agent / Admin |
| **Description** | As an agent, I want to create a new insurance policy record using a form, so that the policy is stored in the central system and can be monitored for renewal. |
| **Business Value** | Ensures every managed policy is tracked; prevents renewal deadlines from being missed. |
| **Priority** | Must Have |
| **Dependencies** | None |
| **Traces To** | FR-01, FR-05, FR-13, FR-14, FR-15 |
| **Acceptance Criteria** | See `acceptance-criteria.md` → AC-US-01 |
| **Suggested Test Case** | TC-US-01: Submit the create-policy form with all valid fields. Verify the new record appears in the policy list. |

---

## US-02 — View the Policy List

| Field | Detail |
|---|---|
| **Story ID** | US-02 |
| **User Role** | Agent / Admin / Viewer |
| **Description** | As an agent, I want to view a list of all insurance policies, so that I have a complete picture of the portfolio I manage. |
| **Business Value** | Provides situational awareness of the entire policy portfolio. |
| **Priority** | Must Have |
| **Dependencies** | US-01 (at least one policy must exist) |
| **Traces To** | FR-02 |
| **Acceptance Criteria** | See `acceptance-criteria.md` → AC-US-02 |
| **Suggested Test Case** | TC-US-02: With five pre-loaded policies, navigate to the policies page and verify five rows are displayed. |

---

## US-03 — View Policy Details

| Field | Detail |
|---|---|
| **Story ID** | US-03 |
| **User Role** | Agent / Admin / Viewer |
| **Description** | As an agent, I want to view the complete details of a specific policy on a dedicated page, so that I can quickly retrieve all information about a customer's coverage. |
| **Business Value** | Allows agents to give accurate information to customers without searching through a list. |
| **Priority** | Must Have |
| **Dependencies** | US-01 |
| **Traces To** | FR-03 |
| **Acceptance Criteria** | See `acceptance-criteria.md` → AC-US-03 |
| **Suggested Test Case** | TC-US-03: Click a policy in the list. Verify the detail page shows all stored fields matching the record. |

---

## US-04 — Edit an Existing Policy

| Field | Detail |
|---|---|
| **Story ID** | US-04 |
| **User Role** | Agent / Admin |
| **Description** | As an agent, I want to edit an existing policy record, so that I can correct mistakes or update information when a policy's details change. |
| **Business Value** | Keeps policy data accurate and up to date. |
| **Priority** | Must Have |
| **Dependencies** | US-01 |
| **Traces To** | FR-04, FR-13, FR-14, FR-15 |
| **Acceptance Criteria** | See `acceptance-criteria.md` → AC-US-04 |
| **Suggested Test Case** | TC-US-04: Edit the expiry date of an existing policy. Verify the updated date appears in the list and detail page. |

---

## US-05 — Prevent Duplicate Policy Numbers

| Field | Detail |
|---|---|
| **Story ID** | US-05 |
| **User Role** | System (enforced on all users) |
| **Description** | As an administrator, I want the system to prevent duplicate policy numbers, so that each policy record is uniquely identifiable and data integrity is maintained. |
| **Business Value** | Prevents confusion caused by two records sharing the same identifier. |
| **Priority** | Must Have |
| **Dependencies** | US-01 |
| **Traces To** | FR-05 |
| **Acceptance Criteria** | See `acceptance-criteria.md` → AC-US-05 |
| **Suggested Test Case** | TC-US-05: Create a policy with number "POL-001". Attempt to create a second with the same number. Verify an error is shown and only one record exists. |

---

## US-06 — Search Policies by Customer Name

| Field | Detail |
|---|---|
| **Story ID** | US-06 |
| **User Role** | Agent / Admin / Viewer |
| **Description** | As an agent, I want to search for policies by customer name, so that I can quickly find all policies belonging to a specific customer when they call. |
| **Business Value** | Saves time; eliminates manual scrolling through a long list. |
| **Priority** | Must Have |
| **Dependencies** | US-02 |
| **Traces To** | FR-06 |
| **Acceptance Criteria** | See `acceptance-criteria.md` → AC-US-06 |
| **Suggested Test Case** | TC-US-06: With policies belonging to "Ahmed Ali" and "Priya Sharma", search "Ahmed". Verify only Ahmed's policies appear. |

---

## US-07 — Search Policies by Policy Number

| Field | Detail |
|---|---|
| **Story ID** | US-07 |
| **User Role** | Agent / Admin / Viewer |
| **Description** | As an agent, I want to search for a policy by its policy number, so that I can retrieve a specific record directly when I know the number. |
| **Business Value** | Enables instant lookup by reference number. |
| **Priority** | Must Have |
| **Dependencies** | US-02 |
| **Traces To** | FR-07 |
| **Acceptance Criteria** | See `acceptance-criteria.md` → AC-US-07 |
| **Suggested Test Case** | TC-US-07: Search for policy number "POL-003". Verify only that policy is returned. |

---

## US-08 — Filter Policies by Status

| Field | Detail |
|---|---|
| **Story ID** | US-08 |
| **User Role** | Agent / Admin / Viewer |
| **Description** | As an agent, I want to filter the policy list by renewal status (Active, Expiring Soon, Expired, Renewed), so that I can focus on the group of policies that needs my attention. |
| **Business Value** | Reduces information overload; allows targeted action. |
| **Priority** | Should Have |
| **Dependencies** | US-02 |
| **Traces To** | FR-08 |
| **Acceptance Criteria** | See `acceptance-criteria.md` → AC-US-08 |
| **Suggested Test Case** | TC-US-08: With policies in each status category, select "Expired". Verify only expired policies are shown. |

---

## US-09 — View Upcoming Renewals

| Field | Detail |
|---|---|
| **Story ID** | US-09 |
| **User Role** | Agent / Admin |
| **Description** | As an agent, I want to see a dedicated list of policies whose expiry dates are approaching within 30 days, so that I can prioritise contacting those customers before their coverage lapses. |
| **Business Value** | Core business value — this is the primary reason the system exists. |
| **Priority** | Must Have |
| **Dependencies** | US-01 |
| **Traces To** | FR-09 |
| **Acceptance Criteria** | See `acceptance-criteria.md` → AC-US-09 |
| **Suggested Test Case** | TC-US-09: Create policies expiring in 10, 20, 35 and 60 days. Verify only the 10-day and 20-day policies appear in upcoming renewals. |

---

## US-10 — View Expired Policies

| Field | Detail |
|---|---|
| **Story ID** | US-10 |
| **User Role** | Agent / Admin |
| **Description** | As an agent, I want to see a list of policies that have already expired and have not been renewed, so that I can follow up with those customers urgently. |
| **Business Value** | Enables urgent action on lapsed policies before customer harm occurs. |
| **Priority** | Must Have |
| **Dependencies** | US-01 |
| **Traces To** | FR-10 |
| **Acceptance Criteria** | See `acceptance-criteria.md` → AC-US-10 |
| **Suggested Test Case** | TC-US-10: Create a policy with an expiry date in the past. Verify it appears in the expired list. |

---

## US-11 — Mark a Policy as Renewed

| Field | Detail |
|---|---|
| **Story ID** | US-11 |
| **User Role** | Agent / Admin |
| **Description** | As an agent, I want to mark a policy as Renewed after the customer has paid their premium, so that the system no longer counts it as expired or approaching expiry. |
| **Business Value** | Keeps the dashboard and upcoming renewals list accurate; prevents unnecessary follow-up on completed renewals. |
| **Priority** | Must Have |
| **Dependencies** | US-04 |
| **Traces To** | FR-11 |
| **Acceptance Criteria** | See `acceptance-criteria.md` → AC-US-11 |
| **Suggested Test Case** | TC-US-11: Create a policy with an expiry date yesterday. Mark it as Renewed. Verify it appears as Renewed, not Expired, in the list and dashboard. |

---

## US-12 — View Dashboard Statistics

| Field | Detail |
|---|---|
| **Story ID** | US-12 |
| **User Role** | Agent / Admin / Viewer |
| **Description** | As a manager, I want to see a dashboard on the home page showing the counts of Total, Active, Expiring Soon, Expired and Renewed policies, so that I have an immediate overview of the portfolio's health. |
| **Business Value** | Enables management-level situational awareness without navigating the full list. |
| **Priority** | Must Have |
| **Dependencies** | US-01 |
| **Traces To** | FR-12 |
| **Acceptance Criteria** | See `acceptance-criteria.md` → AC-US-12 |
| **Suggested Test Case** | TC-US-12: With a known set of policies (3 active, 2 expiring soon, 1 expired, 1 renewed), load the dashboard and verify counts are exactly correct. |

---

## US-13 — Validate Required Fields

| Field | Detail |
|---|---|
| **Story ID** | US-13 |
| **User Role** | System (enforced for all users) |
| **Description** | As a system administrator, I want the system to reject incomplete form submissions and display clear error messages, so that only complete and valid policy records enter the database. |
| **Business Value** | Data quality; prevents incomplete records from causing classification errors. |
| **Priority** | Must Have |
| **Dependencies** | US-01, US-04 |
| **Traces To** | FR-13, FR-14, FR-15 |
| **Acceptance Criteria** | See `acceptance-criteria.md` → AC-US-13 |
| **Suggested Test Case** | TC-US-13: Submit the create form with each required field blank in turn. Verify a specific error message appears for each missing field. |
