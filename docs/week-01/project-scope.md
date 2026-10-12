# Project Scope

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## 1. Introduction

This document defines what the project will and will not include. A clearly defined scope prevents misunderstandings, keeps the project manageable within the 15-week academic timeline and ensures that evaluation criteria are met. Features are classified as **Initial Scope**, **Potential Future Enhancements** or **Explicit Exclusions**.

---

## 2. Initial Scope (Weeks 1–6)

The following features are planned for the initial version of the application. They constitute the minimum viable product (MVP).

### 2.1 Policy Management

| Feature | Description |
|---|---|
| **Create a Policy** | An agent can enter all required policy fields via a web form and save the record to the database. Server-side validation ensures required fields are completed, dates are logically valid and premium is not negative. |
| **View All Policies** | A paginated or scrollable list of all stored policies, showing key fields such as policy number, customer name, type, expiry date and current status. |
| **View Individual Policy Details** | A dedicated page displaying the complete details of a single policy, accessed by clicking a record from the list. |
| **Update Policy Information** | An agent can edit the fields of an existing policy using a pre-populated form. Validation is enforced on save. |
| **Prevent Duplicate Policy Numbers** | The system rejects a new policy whose policy number matches one already stored in the database, showing a clear error message. |

### 2.2 Search and Filter

| Feature | Description |
|---|---|
| **Search by Customer Name** | A search box allows the agent to find policies whose customer name contains the entered text (case-insensitive). |
| **Search by Policy Number** | A search box allows the agent to find a policy by entering its exact or partial policy number. |
| **Filter by Renewal Status** | The list can be filtered to show only Active, Expiring Soon, Expired or Renewed policies. |
| **Filter by Expiry Window** | The list can be filtered to show only policies expiring within a specified number of days. |

### 2.3 Renewal Status and Monitoring

| Feature | Description |
|---|---|
| **Identify Upcoming Renewals** | The system automatically identifies and lists policies whose expiry date falls within the configurable renewal-alert window (default: 30 days from today). |
| **Identify Expired Policies** | The system automatically identifies policies whose expiry date has passed and that have not been marked as Renewed. |
| **Track Renewed Policies** | An agent can mark a policy as Renewed. Renewed policies are not reclassified as Expired even if their original expiry date has passed. |

### 2.4 Dashboard

| Feature | Description |
|---|---|
| **Dashboard Statistics** | The home page displays counts of: Total Policies, Active, Expiring Soon, Expired and Renewed. Counts are derived from live database data at page load. |

### 2.5 Validation

| Feature | Description |
|---|---|
| **Required Field Validation** | All mandatory fields (policy number, customer name, policy type, start date, expiry date, premium) must be present and non-blank. |
| **Date Logic Validation** | Expiry date must not be earlier than start date. |
| **Premium Validation** | Premium amount must be zero or greater. |

---

## 3. Potential Future Enhancements

These features are acknowledged but are **not** included in the current project scope. They may be added in later phases if approved by the faculty supervisor.

| Enhancement | Reason Deferred |
|---|---|
| Email notification to customers | Requires an email server, credentials and privacy approval. |
| SMS notification to customers | Requires a third-party SMS gateway and costs. |
| Role-based access control (RBAC) | Additional security complexity; planned as a should-have for a later sprint. |
| Customer-facing self-service portal | Out of scope for a policy-management tool operated by agents. |
| Multiple insurance company integration | Requires external APIs and legal agreements. |
| Premium payment processing | Requires payment gateway integration (Stripe, Razorpay, etc.) — not academic scope. |
| Multi-tenant / multi-office support | Unnecessary complexity for an academic demonstration. |
| PDF certificate generation | Useful but not core to the renewal monitoring problem. |
| Advanced analytics and charting | Nice to have; the dashboard covers the academic requirement. |
| Mobile application | Web browser access is sufficient for the academic context. |

---

## 4. Explicit Exclusions

The following items are explicitly outside the scope of this project and will not be implemented at any stage unless a formal scope change is approved.

| Exclusion | Reason |
|---|---|
| Real customer personal data | Privacy laws and academic ethics — synthetic data only. |
| Production infrastructure (cloud VMs, managed databases) | Not required and outside the academic budget. |
| Real monetary transactions or payments | Legal and regulatory risk; not academic scope. |
| Integration with live insurance-company back-end systems | Requires commercial agreements and APIs unavailable in an academic context. |
| Kubernetes or container orchestration | Overcomplicates the project; Docker alone meets the DevOps scope. |
| Microservices architecture | A monolithic Spring Boot application meets all academic requirements. |
| Compliance with insurance regulatory requirements | Not achievable in an academic timeline and not in the project brief. |

---

## 5. Scope Boundaries

The diagram below illustrates what is inside and outside the system boundary.

```
┌─────────────────────────────────────────────────────┐
│              System Boundary                        │
│                                                     │
│  ┌───────────┐     ┌────────────────────────────┐  │
│  │ Web       │────►│ Spring Boot Application    │  │
│  │ Browser   │     │ (Controller + Service)     │  │
│  │ (Agent /  │◄────│                            │  │
│  │ Admin)    │     │ Spring Data JPA            │  │
│  └───────────┘     │         │                  │  │
│                    │         ▼                  │  │
│                    │   H2 / Future DB           │  │
│                    └────────────────────────────┘  │
└─────────────────────────────────────────────────────┘
          │
          │  OUTSIDE SCOPE
          ▼
   ┌──────────────────────────────────┐
   │ Customer (no direct access)      │
   │ Email / SMS gateway (future)     │
   │ External insurer systems         │
   │ Payment gateways                 │
   └──────────────────────────────────┘
```

---

## 6. Scope Decision Log

| Decision | Rationale | Status |
|---|---|---|
| Use H2 as the initial database | Embedded, requires no installation; ideal for development and demonstration. | Confirmed |
| Monolithic Spring Boot architecture | Appropriate for academic scope; simpler to build, test and explain. | Confirmed |
| No email/SMS in initial scope | Avoids dependency on external services for demonstration; classified as future enhancement. | Confirmed |
| Role-based access deferred | Adds complexity without adding core value at this stage; RBAC is a should-have for a later sprint. | Provisional — pending faculty approval |
| Configurable renewal alert window (default 30 days) | Reasonable industry standard for advance renewal notice. | Provisional |

> Items marked **Provisional** are working assumptions. They should be reviewed with the faculty supervisor before implementation begins.
