# Page and Navigation Design

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## Navigation Structure

```
Home (Dashboard)
├── Policies (All Policies List)
│   ├── Add Policy (Create Form)
│   ├── Policy Detail (View single policy)
│   │   └── Edit Policy (Edit Form)
│   └── Search/Filter Results (inline with list)
└── Upcoming Renewals (filtered list)
```

All pages share a common navigation bar with links to: Home, All Policies, Add Policy, Upcoming Renewals.

---

## Page 1 — Home / Dashboard (`/`)

**Purpose:** Give the user an immediate overview of the policy portfolio.

**Layout:**
- Navigation bar at top with application title and links.
- Heading: "Policy Renewal Dashboard".
- Row of 5 statistics cards:
  - Total Policies (all)
  - Active (not expiring soon, not expired, not renewed)
  - Expiring Soon (within alert window)
  - Expired (past expiry, not renewed)
  - Renewed
- Quick Actions section: buttons/links for "Add New Policy", "View All Policies", "Upcoming Renewals".
- Brief "about" message if the database is empty, prompting the user to add their first policy.

**Actions:** Navigation links only. No form submission.

**Validation:** None.

**Data displayed:** Live counts from database (service layer query).

---

## Page 2 — All Policies (`/policies`)

**Purpose:** Show all stored policies in a searchable, filterable list.

**Layout:**
- Heading: "All Policies".
- Search bar at top with a text input for name/policy number and a Search button.
- Status filter controls (dropdown or button group): All | Active | Expiring Soon | Expired | Renewed.
- Results table with columns: Policy Number | Customer Name | Type | Expiry Date | Status | Actions.
- Each row has "View" and "Edit" action links.
- If results are empty: "No policies found" message.
- If search returns no results: "No matching policies found for '[term]'" message.
- "Add New Policy" button above the table.

**Actions:**
- Search form submission (GET with `search` query parameter).
- Filter selection (GET with `status` query parameter).
- "View" link → navigates to Policy Detail page.
- "Edit" link → navigates to Edit Policy page.
- "Add New Policy" → navigates to Add Policy page.

**Validation:** None on this page (search is always valid).

---

## Page 3 — Add Policy (`/policies/new`)

**Purpose:** Allow an agent to create a new policy record.

**Layout:**
- Heading: "Add New Policy".
- Form with labelled fields:
  - Policy Number (required, text input)
  - Customer Name (required, text input)
  - Customer Email (optional, email input)
  - Policy Type (required, dropdown: Health | Life | Vehicle | Home | Travel)
  - Start Date (required, date picker)
  - Expiry Date (required, date picker)
  - Premium Amount (required, number input, min = 0)
  - Notes (optional, textarea)
- Submit button: "Save Policy".
- Cancel link: returns to All Policies list.
- Error messages appear below each invalid field.

**Actions:** Form POST to `/policies` → on success, redirect to policy detail or list; on failure, re-render form with errors.

**Validation:**
- Policy Number: required, not blank, must not already exist.
- Customer Name: required, not blank.
- Policy Type: required, must be a valid type.
- Start Date: required, not null.
- Expiry Date: required, not null, must not be before Start Date.
- Premium Amount: required, must be ≥ 0.
- Customer Email: must be valid email format if provided.

---

## Page 4 — Policy Details (`/policies/{id}`)

**Purpose:** Show all details for a single policy.

**Layout:**
- Heading: "Policy Details — [Policy Number]".
- Read-only display of all fields: Policy Number, Customer Name, Customer Email, Policy Type, Start Date, Expiry Date, Premium Amount, Renewal Status (derived), Notes, Created At, Updated At.
- Status badge displayed prominently (colour-coded by status).
- Action buttons:
  - "Edit Policy" → navigates to Edit form.
  - "Back to All Policies" → navigates to list.
- If the policy is not Renewed: "Mark as Renewed" button (submits a POST to update status).
- If the policy is Renewed: "Mark as Renewed" button is hidden or greyed out.

**Actions:**
- "Edit Policy" link.
- "Back to All Policies" link.
- "Mark as Renewed" button (POST to `/policies/{id}/renew`).

**Error state:** If `{id}` does not exist → friendly error page ("Policy not found").

---

## Page 5 — Edit Policy (`/policies/{id}/edit`)

**Purpose:** Allow an agent to edit an existing policy.

**Layout:** Same as Add Policy form, but pre-populated with the current field values.  
Heading: "Edit Policy — [Policy Number]".

**Actions:**
- Form PUT/POST to `/policies/{id}` → on success, redirect to detail page; on failure, re-render with errors.
- "Cancel" link → returns to detail page without saving.

**Validation:** Same rules as Add Policy.

---

## Page 6 — Upcoming Renewals (`/policies/upcoming`)

**Purpose:** Show policies expiring within the alert window that have not been renewed.

**Layout:**
- Heading: "Upcoming Renewals (Next [N] Days)".
- Table with columns: Policy Number | Customer Name | Type | Expiry Date | Days Remaining | Actions.
- Rows sorted by Expiry Date ascending (soonest first).
- Each row has "View" and "Edit" links.
- Empty state: "No policies are approaching renewal within [N] days."

**Actions:** View and Edit links per row. No form submission.

---

## Page 7 — Error States

### 7.1 Policy Not Found
- URL: `/policies/{id}` with a non-existent ID.
- Content: "The requested policy could not be found. It may have been deleted." + "Back to All Policies" link.
- HTTP status: 404 (handled gracefully, not a stack trace page).

### 7.2 Validation Error State
- Applies to Add Policy and Edit Policy pages.
- Form is re-rendered with all user-entered values preserved.
- Each field that failed validation shows a red error message immediately below it.

### 7.3 No Results State
- Applies to All Policies list and Upcoming Renewals.
- A descriptive message in place of an empty table.

---

## Status Badge Colour Scheme

| Status | Badge Colour | Meaning |
|---|---|---|
| Active | Green | Policy is valid and not expiring soon |
| Expiring Soon | Orange/Amber | Policy expires within the alert window |
| Expired | Red | Policy has passed its expiry date and is not renewed |
| Renewed | Blue | Agent has confirmed the policy has been renewed |
