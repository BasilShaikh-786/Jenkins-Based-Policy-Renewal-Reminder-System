# Acceptance Criteria

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## AC-US-01 — Create a New Policy

**Given** the agent is on the Add Policy page  
**When** they enter all required fields with valid data (policy number, customer name, email, type, start date, expiry date, premium ≥ 0) and submit  
**Then** the system saves the record to the database, redirects to the policy list or detail page and displays a success confirmation.

**Given** the agent submits the form with any required field blank  
**When** the form is processed  
**Then** the form is returned to the user with a specific error message adjacent to each missing field; no record is saved.

**Given** the agent enters an expiry date earlier than the start date  
**When** the form is submitted  
**Then** a date-order validation error is shown; no record is saved.

**Given** the agent enters a premium value less than zero  
**When** the form is submitted  
**Then** a premium validation error is shown; no record is saved.

---

## AC-US-02 — View the Policy List

**Given** at least one policy exists in the database  
**When** the agent navigates to the Policies page  
**Then** all policies appear in a tabular list, each row showing the policy number, customer name, policy type, expiry date and derived renewal status. The list is sorted by expiry date ascending by default.

**Given** no policies exist in the database  
**When** the agent navigates to the Policies page  
**Then** a friendly "No policies found" message is displayed instead of an empty table.

---

## AC-US-03 — View Policy Details

**Given** the agent is on the policy list and a policy exists  
**When** they click on a policy row or its "View" link  
**Then** a detail page loads showing all stored fields for that policy: Policy Number, Customer Name, Customer Email, Policy Type, Start Date, Expiry Date, Premium, Renewal Status, Notes, Created At and Updated At.

**Given** the agent navigates to a detail URL with a non-existent policy ID  
**When** the page is requested  
**Then** a handled error page with an appropriate message (e.g., "Policy not found") is displayed; a stack trace is not shown.

---

## AC-US-04 — Edit an Existing Policy

**Given** the agent is on the detail page of an existing policy  
**When** they click Edit and modify one or more valid fields and save  
**Then** the database record is updated and the list and detail page reflect the new values.

**Given** the agent submits the edit form with invalid data  
**When** the form is processed  
**Then** field-level error messages are shown; the database record is not changed.

**Given** the agent changes the policy number to one that belongs to a different existing policy  
**When** the form is submitted  
**Then** a duplicate-policy-number error is shown; no update is saved.

---

## AC-US-05 — Prevent Duplicate Policy Numbers

**Given** a policy with number "POL-001" already exists in the database  
**When** the agent submits a new policy form with policy number "POL-001"  
**Then** the form is returned with an error message stating that the policy number already exists; no new record is saved.

**Given** a policy with number "POL-001" already exists  
**When** the agent edits a different policy and changes its number to "POL-001"  
**Then** the same duplicate error is shown; no update is saved.

---

## AC-US-06 — Search by Customer Name

**Given** five policies exist, two belonging to "Ahmed Ali" and three to other customers  
**When** the agent enters "Ahmed" in the search box and submits  
**Then** exactly two records are returned (both belonging to "Ahmed Ali"). The search is case-insensitive.

**Given** the search term does not match any customer name  
**When** the search is submitted  
**Then** a friendly "No matching policies found" message is displayed; no error occurs.

---

## AC-US-07 — Search by Policy Number

**Given** a policy with number "POL-003" exists  
**When** the agent searches for "POL-003"  
**Then** exactly one record is returned matching that policy number.

---

## AC-US-08 — Filter by Status

**Given** the policy list contains policies of all four status categories  
**When** the agent selects "Expired" from the status filter  
**Then** only policies whose expiry date is in the past and whose stored renewal status is not RENEWED are displayed.

**When** the agent selects "All" or clears the filter  
**Then** all policies are displayed.

---

## AC-US-09 — View Upcoming Renewals

**Given** the renewal alert window is set to 30 days  
**And** policies exist expiring in 10, 20, 35 and 60 days  
**And** none of them are marked as Renewed  
**When** the agent navigates to the Upcoming Renewals page  
**Then** only the policies expiring in 10 and 20 days are listed, ordered by expiry date ascending.

**Given** a policy expiring in 10 days is marked as Renewed  
**When** the Upcoming Renewals page is loaded  
**Then** that policy does not appear in the list.

---

## AC-US-10 — View Expired Policies

**Given** a policy whose expiry date is in the past and is not marked as Renewed  
**When** the agent views the policy list or the Expired filter  
**Then** that policy is shown with status "Expired" and included in the expired count on the dashboard.

**Given** a policy whose expiry date is in the past and IS marked as Renewed  
**When** the agent views the policy list or the Expired filter  
**Then** that policy is shown with status "Renewed" and NOT included in the expired count.

---

## AC-US-11 — Mark a Policy as Renewed

**Given** a policy whose original expiry date is yesterday  
**When** the agent sets its renewal status to RENEWED and saves  
**Then** the policy displays as "Renewed" in the list, the detail page and the dashboard. It is NOT counted as Expired. This behaviour is stable regardless of how much time passes.

---

## AC-US-12 — View Dashboard Statistics

**Given** the database contains: 3 Active, 2 Expiring Soon, 1 Expired and 1 Renewed policy  
**When** the agent loads the home page  
**Then** the dashboard shows: Total = 7, Active = 3, Expiring Soon = 2, Expired = 1, Renewed = 1.

**Given** a new policy is added (Active status)  
**When** the home page is reloaded  
**Then** Total increases by 1 and Active increases by 1.

---

## AC-US-13 — Validate Required Fields

**Given** the create-policy form is displayed  
**When** the agent submits with Customer Name blank  
**Then** an error message "Customer name is required" (or equivalent) appears adjacent to the Customer Name field; the record is not saved.

**When** the agent submits with Policy Number blank  
**Then** an error message "Policy number is required" appears adjacent to the Policy Number field; the record is not saved.

**When** the agent submits with Start Date blank  
**Then** an error message about the required start date appears; the record is not saved.

**When** the agent submits with Expiry Date earlier than Start Date  
**Then** a date-logic error message appears; the record is not saved.

**When** the agent submits with Premium = -100  
**Then** an error message "Premium must be zero or greater" (or equivalent) appears; the record is not saved.
