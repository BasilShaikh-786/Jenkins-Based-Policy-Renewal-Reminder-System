# API and Route Design

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## Overview

This document defines the server-side routes (URLs) for the application. Because this is a server-rendered application using Thymeleaf (not a REST API consumed by a separate frontend), the routes follow the conventions of Spring MVC: GET requests retrieve pages and POST requests submit forms.

There is no JSON API at this stage. All communication is via HTML form submissions and page redirects.

---

## Route Table

| # | Method | URL Path | Controller Method | Description | Input | Success Output | Error Output |
|---|---|---|---|---|---|---|---|
| 1 | GET | `/` | `HomeController.home()` | Dashboard home page | None | Renders `index.html` with stats model | — |
| 2 | GET | `/policies` | `PolicyController.listPolicies()` | All policies list | `?search=` (optional), `?status=` (optional) | Renders `policies/list.html` | — |
| 3 | GET | `/policies/new` | `PolicyController.showCreateForm()` | Show empty Add Policy form | None | Renders `policies/form.html` (empty) | — |
| 4 | POST | `/policies` | `PolicyController.createPolicy()` | Save a new policy | Form body: Policy fields | Redirect to `/policies/{newId}` with success message | Re-render `policies/form.html` with errors |
| 5 | GET | `/policies/{id}` | `PolicyController.viewPolicy()` | View policy details | Path variable: `id` | Renders `policies/detail.html` | Renders `error/404.html` if not found |
| 6 | GET | `/policies/{id}/edit` | `PolicyController.showEditForm()` | Show pre-populated Edit form | Path variable: `id` | Renders `policies/form.html` (pre-filled) | Renders `error/404.html` if not found |
| 7 | POST | `/policies/{id}` | `PolicyController.updatePolicy()` | Save updated policy | Path variable: `id`; Form body: Policy fields | Redirect to `/policies/{id}` with success message | Re-render `policies/form.html` with errors |
| 8 | POST | `/policies/{id}/renew` | `PolicyController.markAsRenewed()` | Mark policy as Renewed | Path variable: `id` | Redirect to `/policies/{id}` with success message | Renders `error/404.html` if not found |
| 9 | GET | `/policies/upcoming` | `PolicyController.upcomingRenewals()` | Upcoming renewals list | None | Renders `policies/upcoming.html` | — |

---

## Route Design Notes

### Why POST for update instead of PUT?
HTML forms only support GET and POST methods. Spring MVC can simulate PUT via a hidden `_method` field, but this adds complexity without benefit for a server-rendered application. Using POST for both create and update is the standard Thymeleaf pattern.

### Why redirect after successful POST?
This is called the **POST/Redirect/GET pattern**. After a successful form submission, the server redirects the browser to a GET page. This prevents the form from being resubmitted if the user refreshes the page. Without this redirect, refreshing after creating a policy would create a duplicate.

### Flash messages
Success messages ("Policy saved successfully") are passed to the next page using Spring's `RedirectAttributes.addFlashAttribute()`. This ensures the message appears once after the redirect and disappears on the next page load.

### Query parameter conventions
- `?search=ahmed` — searches both customer name and policy number for the given text.
- `?status=EXPIRED` — filters the list to show only policies with the derived status EXPIRED.
- Combining both parameters is supported: `?search=ahmed&status=ACTIVE`.

---

## URL Examples (with Synthetic Data)

| Intent | URL |
|---|---|
| Open dashboard | `http://localhost:8080/` |
| View all policies | `http://localhost:8080/policies` |
| Search for "Ahmed" | `http://localhost:8080/policies?search=Ahmed` |
| Filter expired policies | `http://localhost:8080/policies?status=EXPIRED` |
| Add a new policy | `http://localhost:8080/policies/new` |
| View policy with ID 3 | `http://localhost:8080/policies/3` |
| Edit policy with ID 3 | `http://localhost:8080/policies/3/edit` |
| View upcoming renewals | `http://localhost:8080/policies/upcoming` |

---

## Error Handling Routes

| Scenario | HTTP Status | Page Shown |
|---|---|---|
| Invalid policy ID (not found) | 404 | `templates/error/404.html` |
| Unexpected server error | 500 | `templates/error/5xx.html` |

Spring Boot's `@ControllerAdvice` or custom error page configuration will handle these cases. Raw stack traces must not be displayed to the user.

---

## Future Routes (Not Implemented in Week 3)

| Method | URL | Description | Planned Week |
|---|---|---|---|
| POST | `/policies/{id}/delete` | Archive or delete a policy | Week 5 |
| GET | `/policies/expired` | View all expired policies page | Week 5 |
| GET | `/h2-console/**` | H2 database console (dev only) | Available now (dev profile) |
| GET | `/actuator/health` | Health endpoint (Spring Actuator) | Week 10 |
