# Functional Requirements

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## How to Read This Document

Each requirement has a unique identifier (e.g., **FR-01**).  
Priority levels: **High** = must be in the initial version; **Medium** = should be included; **Low** = could be deferred.

---

## FR-01 — Create a New Policy

| Field | Detail |
|---|---|
| **Requirement ID** | FR-01 |
| **Description** | The system shall allow an authorised user to create a new insurance policy record by submitting a web form with all required fields. |
| **Priority** | High |
| **Expected Behaviour** | The user fills in: Policy Number, Customer Name, Customer Email, Policy Type, Start Date, Expiry Date, Premium Amount, and optional Notes. Upon submission, the system validates the data, saves the record and redirects the user to the policy detail page or the policy list. |
| **Acceptance Condition** | A valid policy form is submitted → the record appears in the database and in the policy list → the user sees a success message. |
| **Suggested Test Case** | TC-FR-01: Submit a form with all valid fields. Verify that the policy record is saved and visible in the list. |

---

## FR-02 — View All Policies

| Field | Detail |
|---|---|
| **Requirement ID** | FR-02 |
| **Description** | The system shall display a list of all stored policy records, showing the policy number, customer name, policy type, expiry date and derived renewal status for each record. |
| **Priority** | High |
| **Expected Behaviour** | The policies page loads and shows one row per policy in tabular form. The list is sorted by expiry date ascending by default. An empty state message is shown when no records exist. |
| **Acceptance Condition** | All policies stored in the database appear in the list. Status column reflects the correct derived classification for each record. |
| **Suggested Test Case** | TC-FR-02: With five pre-loaded policies, navigate to the policies page and verify that all five are displayed with correct statuses. |

---

## FR-03 — View Individual Policy Details

| Field | Detail |
|---|---|
| **Requirement ID** | FR-03 |
| **Description** | The system shall display a dedicated page showing all stored fields for a single selected policy. |
| **Priority** | High |
| **Expected Behaviour** | Clicking on a policy in the list navigates to a detail page showing: Policy Number, Customer Name, Customer Email, Policy Type, Start Date, Expiry Date, Premium Amount, Renewal Status, Notes, and Creation/Update timestamps. |
| **Acceptance Condition** | The detail page loads for a valid policy ID and all stored fields are correctly displayed. Accessing a non-existent policy ID returns a 404 or error page. |
| **Suggested Test Case** | TC-FR-03: Click a policy from the list. Verify all fields on the detail page match the stored record. |

---

## FR-04 — Update Policy Information

| Field | Detail |
|---|---|
| **Requirement ID** | FR-04 |
| **Description** | The system shall allow an authorised user to edit the fields of an existing policy record using a pre-populated edit form. |
| **Priority** | High |
| **Expected Behaviour** | The edit form is pre-filled with the current values. The user modifies one or more fields and submits. Validation is applied. If valid, the updated record is saved. If invalid, errors are shown and data is not saved. |
| **Acceptance Condition** | Editing and saving a valid change updates the database record. The policy list reflects the new values. An invalid edit attempt shows error messages without saving. |
| **Suggested Test Case** | TC-FR-04: Load the edit form for an existing policy, change the expiry date to a valid future date, save, and verify the list and detail page reflect the new date. |

---

## FR-05 — Prevent Duplicate Policy Numbers

| Field | Detail |
|---|---|
| **Requirement ID** | FR-05 |
| **Description** | The system shall reject any attempt to save a policy with a policy number that already exists in the database. |
| **Priority** | High |
| **Expected Behaviour** | When a new policy is submitted with a policy number that matches an existing record, the form is returned to the user with an error message identifying the duplicate. The record is not saved. |
| **Acceptance Condition** | Attempting to create or update a policy with an already-used policy number → error message displayed → no duplicate record saved. |
| **Suggested Test Case** | TC-FR-05: Create a policy with number "POL-001". Attempt to create a second policy with the same number. Verify the error message appears and only one record exists in the database. |

---

## FR-06 — Search by Customer Name

| Field | Detail |
|---|---|
| **Requirement ID** | FR-06 |
| **Description** | The system shall allow the user to search the policy list by entering a customer name or partial name. |
| **Priority** | High |
| **Expected Behaviour** | A search input is available on the policies page. Entering a name or partial name and submitting returns only policies where the customer name contains the entered text (case-insensitive). A no-results state is shown if no match is found. |
| **Acceptance Condition** | Searching "Ahmed" returns all policies with "Ahmed" in the customer name. An empty result set shows a friendly message, not a blank page. |
| **Suggested Test Case** | TC-FR-06: With five policies of different names, search for a name that matches two records. Verify only two rows appear. |

---

## FR-07 — Search by Policy Number

| Field | Detail |
|---|---|
| **Requirement ID** | FR-07 |
| **Description** | The system shall allow the user to find a policy by entering its policy number or a partial policy number. |
| **Priority** | High |
| **Expected Behaviour** | The same search field or a dedicated search field accepts a policy number input and returns matching records. |
| **Acceptance Condition** | Entering the exact policy number "POL-003" returns the single matching record. Entering a prefix that matches multiple records returns all matching records. |
| **Suggested Test Case** | TC-FR-07: Search for a known policy number. Verify the correct single record is returned. |

---

## FR-08 — Filter by Renewal Status

| Field | Detail |
|---|---|
| **Requirement ID** | FR-08 |
| **Description** | The system shall allow the user to filter the policy list by renewal status (Active, Expiring Soon, Expired, Renewed). |
| **Priority** | Medium |
| **Expected Behaviour** | A dropdown or button group allows selection of one status. The list updates to show only policies matching the selected status. Selecting "All" removes the filter and shows the full list. |
| **Acceptance Condition** | Selecting "Expired" shows only policies whose expiry date is in the past and that are not marked as Renewed. |
| **Suggested Test Case** | TC-FR-08: With policies of each status in the database, select each filter option and verify only matching records appear. |

---

## FR-09 — View Upcoming Renewals

| Field | Detail |
|---|---|
| **Requirement ID** | FR-09 |
| **Description** | The system shall display a dedicated section or page listing all policies whose expiry date falls within the configurable renewal-alert window (default: 30 days from today) and that have not been marked as Renewed. |
| **Priority** | High |
| **Expected Behaviour** | The upcoming renewals view loads and shows policies ordered by expiry date ascending. The number of days until expiry is displayed for each record. |
| **Acceptance Condition** | All policies with expiry dates between today and today + 30 days (and status not Renewed) appear in the list. Policies outside this window do not appear. |
| **Suggested Test Case** | TC-FR-09: Create policies expiring in 10, 20, 35 and 60 days. Verify that only the 10-day and 20-day policies appear in the upcoming renewals list. |

---

## FR-10 — View Expired Policies

| Field | Detail |
|---|---|
| **Requirement ID** | FR-10 |
| **Description** | The system shall display a list of policies whose expiry date has already passed and that have not been marked as Renewed. |
| **Priority** | High |
| **Expected Behaviour** | Expired policies are visually distinguished (e.g., highlighted in red) in the main list and can be viewed in a filtered "Expired" view. |
| **Acceptance Condition** | A policy whose expiry date is in the past and whose renewal status is not Renewed appears in the expired category. |
| **Suggested Test Case** | TC-FR-10: Create a policy with an expiry date in the past. Verify it appears under the Expired filter and in dashboard expired count. |

---

## FR-11 — Mark a Policy as Renewed

| Field | Detail |
|---|---|
| **Requirement ID** | FR-11 |
| **Description** | The system shall allow an agent to mark an existing policy as Renewed. A renewed policy shall not be reclassified as Expired even if its original expiry date has passed. |
| **Priority** | High |
| **Expected Behaviour** | The edit form or detail page includes a "Mark as Renewed" action. Setting the renewal status to "RENEWED" stores this value explicitly. The status-derivation logic checks the stored status first before applying date-based classification. |
| **Acceptance Condition** | A policy with an expiry date in the past that has been marked as Renewed is shown as Renewed, not Expired, in the list and dashboard. |
| **Suggested Test Case** | TC-FR-11: Create a policy with an expiry date yesterday. Mark it as Renewed. Verify it appears as Renewed, not Expired. |

---

## FR-12 — Display Dashboard Statistics

| Field | Detail |
|---|---|
| **Requirement ID** | FR-12 |
| **Description** | The system shall display a dashboard on the home page showing: Total Policies, Active Policies, Expiring Soon, Expired and Renewed — all derived from live database data at the time of page load. |
| **Priority** | High |
| **Expected Behaviour** | On loading the home page, the application queries the database, computes the counts and displays them as summary cards or tiles. |
| **Acceptance Condition** | Dashboard counts are numerically correct given the current state of the database. Adding or modifying a record and reloading the page updates the counts immediately. |
| **Suggested Test Case** | TC-FR-12: With a known set of policies (e.g., 3 active, 2 expiring soon, 1 expired, 1 renewed), load the dashboard and verify all counts are exactly correct. |

---

## FR-13 — Validate Required Fields

| Field | Detail |
|---|---|
| **Requirement ID** | FR-13 |
| **Description** | The system shall reject form submissions where any required field is blank or invalid, and return the form with descriptive field-level error messages. |
| **Priority** | High |
| **Expected Behaviour** | Required fields: Policy Number, Customer Name, Policy Type, Start Date, Expiry Date, Premium. Submitting without a required field returns an error message adjacent to that field. |
| **Acceptance Condition** | Submitting a form with a missing required field shows a specific, descriptive error for that field. The record is not saved. |
| **Suggested Test Case** | TC-FR-13a: Submit create form with blank Customer Name → error shown, record not saved. TC-FR-13b: Submit with blank Policy Number → error shown, record not saved. |

---

## FR-14 — Validate Date Logic

| Field | Detail |
|---|---|
| **Requirement ID** | FR-14 |
| **Description** | The system shall reject any policy submission where the expiry date is earlier than the start date. |
| **Priority** | High |
| **Expected Behaviour** | If the user enters a start date of 2026-01-01 and an expiry date of 2025-12-31, an error message is displayed on the form and the record is not saved. |
| **Acceptance Condition** | A form submitted with expiry date < start date produces a date-order validation error. |
| **Suggested Test Case** | TC-FR-14: Enter start date = today, expiry date = yesterday. Submit. Verify date-order error message appears. |

---

## FR-15 — Validate Premium Amount

| Field | Detail |
|---|---|
| **Requirement ID** | FR-15 |
| **Description** | The system shall reject premium values that are negative. Zero is allowed (representing a waived or complimentary policy). |
| **Priority** | High |
| **Expected Behaviour** | Submitting a premium of –100 produces an error message. Submitting a premium of 0 or any positive number is accepted. |
| **Acceptance Condition** | Form submission with a negative premium value → error message shown → record not saved. |
| **Suggested Test Case** | TC-FR-15: Enter premium = –1. Submit form. Verify error message appears and record is not created. |
