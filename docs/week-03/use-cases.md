# Use Cases

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## UC-01 — Create a Policy

**Actor:** Agent or Admin  
**Precondition:** The user has navigated to the Add Policy page (`/policies/new`).  
**Trigger:** The user fills in the form and clicks Submit.

### Normal Flow
1. User opens the Add Policy page.
2. User enters: Policy Number, Customer Name, Customer Email (optional), Policy Type, Start Date, Expiry Date, Premium Amount, Notes (optional).
3. User clicks "Save Policy".
4. System validates all required fields are present and non-blank.
5. System validates expiry date is not earlier than start date.
6. System validates premium is ≥ 0.
7. System checks that the policy number does not already exist.
8. System saves the record to the database with creation timestamp.
9. System redirects the user to the policy detail page with a success message.

### Alternative Flow A — Validation Failure
4a. One or more required fields are blank or invalid.  
4b. System returns the form with field-level error messages highlighting each problem.  
4c. The record is not saved. The user corrects the errors and resubmits.

### Alternative Flow B — Duplicate Policy Number
7a. The entered policy number already exists.  
7b. System returns the form with an error message: "Policy number [X] is already in use."  
7c. The user enters a different policy number and resubmits.

**Postcondition:** A new valid policy record exists in the database and is visible in the policy list.

---

## UC-02 — Search for a Policy

**Actor:** Agent, Admin or Viewer  
**Precondition:** The user is on the Policies page (`/policies`).  
**Trigger:** The user enters a search term in the search box and submits.

### Normal Flow — By Customer Name
1. User enters a customer name (or partial name) in the search field.
2. User clicks "Search".
3. System queries the database for policies where the customer name contains the search term (case-insensitive).
4. System displays a filtered list of matching policies.

### Normal Flow — By Policy Number
1. User enters a policy number in the search field.
2. System queries the database for policies whose policy number contains the entered text.
3. System displays matching results.

### Alternative Flow — No Results
3a. No matching policies are found.  
3b. System displays a "No matching policies found" message. The page does not show a blank table.

### Alternative Flow — Empty Search Term
2a. User submits the search with an empty field.  
2b. System displays the full policy list (treats empty search as no filter).

**Postcondition:** The policy list displays only policies matching the search term, or a no-results message.

---

## UC-03 — Update Policy Details

**Actor:** Agent or Admin  
**Precondition:** The user is on the policy detail page for an existing policy.  
**Trigger:** The user clicks "Edit".

### Normal Flow
1. User clicks "Edit" on the detail page.
2. System loads the edit form at `/policies/{id}/edit`, pre-populated with the current field values.
3. User modifies one or more fields.
4. User clicks "Save Changes".
5. System validates the updated data (same rules as creation).
6. System saves the updated record and updates the `updatedAt` timestamp.
7. System redirects to the detail page with a success message.

### Alternative Flow — Validation Failure
5a. The updated data fails validation.  
5b. System returns the edit form with field-level error messages.  
5c. The original record in the database is unchanged.

### Alternative Flow — Duplicate Policy Number on Update
5a. The user changed the policy number to one that belongs to a different existing record.  
5b. System returns the edit form with a duplicate-number error.  
5c. No update is saved.

**Postcondition:** The policy record reflects the new values; the list and dashboard are updated accordingly.

---

## UC-04 — View Upcoming Renewals

**Actor:** Agent or Admin  
**Precondition:** At least one policy exists in the database.  
**Trigger:** The user navigates to the Upcoming Renewals page.

### Normal Flow
1. User navigates to `/policies/upcoming`.
2. System reads the configurable renewal alert window (default: 30 days) from `application.properties`.
3. System queries the database for policies where: `expiryDate` is between today and today + alertWindowDays, AND `renewalStatus` is not `RENEWED`.
4. System displays the results sorted by expiry date ascending, showing days remaining for each policy.

### Alternative Flow — No Upcoming Renewals
3a. No policies match the criteria.  
3b. System displays "No policies are approaching renewal within [N] days." No empty table.

**Postcondition:** The user has a prioritised list of policies requiring attention.

---

## UC-05 — View Dashboard

**Actor:** Agent, Admin or Viewer  
**Precondition:** None.  
**Trigger:** The user navigates to the home page (`/`).

### Normal Flow
1. User opens the application home page.
2. System queries the database and computes:
   - Total policies
   - Active policies
   - Policies expiring within the alert window (not Renewed)
   - Expired policies (not Renewed)
   - Renewed policies
3. System displays the counts as summary cards.
4. System displays a "Quick Actions" area with links to Add Policy, View All Policies and Upcoming Renewals.

### Alternative Flow — No Policies Yet
2a. The database is empty.  
2b. All counts show zero. A prompt is shown to add the first policy.

**Postcondition:** The user has an overview of the portfolio's health.

---

## UC-06 — Handle Invalid Data (Form Submission Error)

**Actor:** Any user submitting a form  
**Precondition:** The user is on the Add Policy or Edit Policy page.  
**Trigger:** The user submits the form with invalid or incomplete data.

### Normal Flow
1. User submits the form.
2. System runs server-side validation.
3. One or more validation rules fail.
4. System re-renders the form, preserving the user's entered values.
5. Each field that failed validation shows a descriptive error message immediately below it.
6. The user corrects the errors.
7. The user resubmits. Validation passes. Record is saved.

### Failure Condition
If the application displays a raw stack trace or HTTP 500 error page instead of a handled error message, this is a defect that must be fixed before the feature is marked as Done.

**Postcondition:** Invalid data was not saved to the database. The user received clear guidance on how to correct their input.
