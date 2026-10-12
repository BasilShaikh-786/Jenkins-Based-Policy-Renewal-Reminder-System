# Software Requirements Specification (SRS)

**Document Title:** Software Requirements Specification  
**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C  
**Version:** 1.0  
**Date:** October 2026  
**Status:** Draft — Pending Faculty Review

---

## 1. Introduction

### 1.1 Purpose

This Software Requirements Specification (SRS) defines the complete requirements for the Jenkins-Based Policy Renewal Reminder System. It is the primary reference document for design, implementation and testing decisions. All subsequent design, code and test artefacts must be consistent with this document.

### 1.2 Scope

The system is a web-based application that allows insurance agents and administrators to manage insurance policy records and monitor upcoming renewals. It is built using Java, Spring Boot, Thymeleaf and H2. It serves as the foundation for a 15-week DevOps project that will progressively integrate Git, Jenkins, Selenium, Docker and Ansible/Puppet.

### 1.3 Intended Users

| User | Access Level |
|---|---|
| System Administrator | Full read and write access |
| Insurance Agent / Policy Manager | Full read and write access |
| Read-Only Viewer / Manager | Read access only (future: enforced via RBAC) |

### 1.4 Operating Environment

- **Development OS:** Windows 11 (Microsoft Windows NT 10.0.26200)
- **Runtime:** Java LTS (Java 17 or Java 21 — to be confirmed at installation time)
- **Web framework:** Spring Boot 3.x
- **Build tool:** Maven with Maven Wrapper
- **Database:** H2 in-memory (embedded, no external installation required)
- **Access:** Via web browser at `http://localhost:8080` (development)
- **Deployment target (later weeks):** Docker container; server provisioned by Ansible/Puppet

### 1.5 Document Conventions

- **FR-xx** — Functional Requirement
- **NFR-xx** — Non-Functional Requirement
- **BR-xx** — Business Rule
- **US-xx** — User Story
- **AC-US-xx** — Acceptance Criteria for User Story

---

## 2. Functional Requirements

The functional requirements are fully documented in `docs/week-01/functional-requirements.md`. They are summarised here for completeness and cross-referenced to business rules.

| ID | Description | Priority | Business Rules |
|---|---|---|---|
| FR-01 | Create a new insurance policy | High | BR-01, BR-02, BR-04, BR-05 |
| FR-02 | View all policies as a list | High | BR-03, BR-06 |
| FR-03 | View individual policy details | High | — |
| FR-04 | Update policy information | High | BR-01, BR-02, BR-04, BR-05 |
| FR-05 | Prevent duplicate policy numbers | High | BR-01 |
| FR-06 | Search by customer name | High | — |
| FR-07 | Search by policy number | High | — |
| FR-08 | Filter by renewal status | Medium | BR-03 |
| FR-09 | View upcoming renewals (configurable window) | High | BR-03, BR-06 |
| FR-10 | View expired policies | High | BR-03 |
| FR-11 | Mark a policy as Renewed | High | BR-03, BR-07 |
| FR-12 | Display dashboard statistics | High | BR-03, BR-06, BR-07 |
| FR-13 | Validate required fields | High | BR-02 |
| FR-14 | Validate date logic (expiry ≥ start) | High | BR-04 |
| FR-15 | Validate premium ≥ 0 | High | BR-05 |

---

## 3. Non-Functional Requirements

Summarised from `docs/week-01/non-functional-requirements.md`.

| ID | Category | Key Criterion |
|---|---|---|
| NFR-01 | Usability | Any policy reachable within 3 navigations from home |
| NFR-02 | Maintainability | Layered architecture; no business logic in controllers |
| NFR-03 | Reliability | No unhandled exceptions during normal operation |
| NFR-04 | Security | No secrets in source control; Thymeleaf output escaping |
| NFR-05 | Testability | All tests run with `./mvnw test`; no external services needed |
| NFR-06 | Data Validation | Server-side validation; field-level error messages |
| NFR-07 | Performance | ≤ 5 seconds page load for ≤ 100 records |
| NFR-08 | Portability | Maven Wrapper; no manual Maven install required |
| NFR-09 | Accessibility | Labelled form fields; text status labels |
| NFR-10 | Status Consistency | One algorithm, one location; consistent classification |

---

## 4. Business Rules

Business rules are constraints that the system must enforce regardless of user input.

### BR-01 — Policy Number Uniqueness
Every policy number stored in the database must be unique. The system must reject any create or update operation that would result in two records sharing the same policy number.

### BR-02 — Required Fields
The following fields are mandatory for every policy record: Policy Number, Customer Name, Policy Type, Start Date, Expiry Date, Premium Amount. Customer Email and Notes are optional.

### BR-03 — Status Classification Algorithm
Policy renewal status is derived by the service layer using the following priority:

1. If the stored `renewalStatus` field equals `RENEWED` → status is **Renewed**.
2. Else if `expiryDate` is before today → status is **Expired**.
3. Else if `expiryDate` is within the next N days (configurable; default 30) → status is **Expiring Soon**.
4. Otherwise → status is **Active**.

This algorithm must be implemented in one place only (the `PolicyService` class) and must not be duplicated in controllers, repositories or templates.

### BR-04 — Date Order
The expiry date of a policy must not be earlier than its start date. Policies where `expiryDate < startDate` are invalid and must not be saved.

### BR-05 — Premium Validation
The premium amount must be a numeric value greater than or equal to zero. Negative premiums are not permitted.

### BR-06 — Dashboard Accuracy
Dashboard statistics must be derived from the live database at the time of page load. They must not be cached or pre-computed in a way that could become stale.

### BR-07 — Renewed Policies Are Exempt from Expiry Classification
A policy that has been explicitly marked as `RENEWED` must continue to display as Renewed even if its original expiry date has passed. The system must not reclassify it as Expired as time progresses.

---

## 5. Data Requirements

See `docs/week-03/database-design.md` for the complete data model. Key data requirements:

- A single `Policy` entity stores all policy data.
- The primary key is a system-generated integer.
- Policy number is a business key stored as a unique, non-null string.
- Dates are stored as `java.time.LocalDate`.
- Premium is stored as `java.math.BigDecimal` (avoids floating-point precision errors).
- The `renewalStatus` field stores `RENEWED` when explicitly set, or `null`/`ACTIVE` otherwise (derived statuses are not stored).
- Creation and update timestamps are stored automatically.

---

## 6. User Permissions

**Initial version (Week 3):** No authentication or role enforcement. All users accessing the application URL have identical access.

**Future enhancement (planned):** Role-based access control using Spring Security will restrict delete and administrative operations to the Administrator role. This is a provisional assumption pending faculty approval.

---

## 7. Error Handling Expectations

| Scenario | Expected Behaviour |
|---|---|
| Required field is blank | Field-level error message returned on the form; page re-rendered |
| Expiry date before start date | Date-logic error message returned on the form |
| Negative premium | Premium validation error message returned on the form |
| Duplicate policy number | Error message identifying the duplicate; form re-rendered |
| Navigation to non-existent policy ID | Friendly error page (not a stack trace); HTTP 404 or handled exception |
| Internal server error | Friendly error page (not raw stack trace exposed to the user) |
| Empty search results | "No matching policies found" message; no blank page |
| Empty policy list | "No policies have been added yet" message; no blank table |

---

## 8. Security Considerations

- No production credentials, API keys or database passwords are stored in source code or committed to version control.
- H2 console (`/h2-console`) is enabled only when the `dev` Spring profile is active.
- Thymeleaf automatically escapes output in `th:text` expressions, preventing XSS injection.
- The `.gitignore` excludes the `target/` directory, IDE configuration files and any `.env` files.
- All customer data used during development is synthetic and does not represent real persons.

---

## 9. Constraints

| ID | Constraint |
|---|---|
| C-01 | 15-week academic timeline |
| C-02 | Prescribed technology stack (Java, Spring Boot, Maven, Thymeleaf, H2) |
| C-03 | No external runtime dependencies (no email server, no SMS gateway) |
| C-04 | Academic scope — no production regulatory compliance |
| C-05 | Windows development machine |

---

## 10. Limitations

1. The application uses an H2 in-memory database; data does not persist across restarts.
2. There is no authentication or access control in the initial version.
3. The system displays renewal alerts but does not send automatic notifications.
4. Performance targets are appropriate for a small demonstration dataset only.
5. Java JDK must be manually installed before the application can run.

---

## 11. Acceptance Criteria

All acceptance criteria are documented in `docs/week-02/acceptance-criteria.md`. The SRS is considered satisfied when every criterion in that document has been demonstrated to pass against a deployed instance of the application.

---

## 12. Future Enhancements

| Enhancement | When |
|---|---|
| Email/SMS notification to customers | After faculty approval; requires email server or gateway |
| Role-based access control (Spring Security) | Future sprint; see A-02 in assumptions |
| Persistent database (MySQL/PostgreSQL) | Optional; H2 is acceptable for academic demonstration |
| Pagination of policy list | When dataset exceeds 50–100 records |
| Soft-delete (archive) instead of hard-delete | Future sprint |
| Audit logging (who changed what, when) | Future sprint |
| PDF report generation | Future sprint |
