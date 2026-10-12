# Assumptions and Constraints

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## How to Read This Document

- **Confirmed** — the fact has been established and is not expected to change.
- **Provisional** — a working assumption made to allow the project to proceed; must be reviewed with the faculty supervisor.
- **Pending** — a decision that has not yet been made and is blocking or may affect implementation.

---

## Section 1: Assumptions

### A-01 — Policy Types

| Attribute | Detail |
|---|---|
| **Assumption** | The system will support a fixed set of policy types: Health, Life, Vehicle, Home and Travel. |
| **Rationale** | A fixed list prevents invalid free-text entries and simplifies filtering. |
| **Status** | **Provisional** — the actual list of policy types used by the insurance office may differ. |
| **Impact if wrong** | The dropdown list in the create/edit form needs to be updated. No structural code change is required. |

---

### A-02 — User Roles (Initial Version)

| Attribute | Detail |
|---|---|
| **Assumption** | The initial version will not enforce login or role-based access control. All users who can access the application URL will have the same level of access. |
| **Rationale** | Adding Spring Security increases complexity significantly. Role-based access is planned as a future enhancement. |
| **Status** | **Provisional** — pending faculty confirmation that a role-less initial version is acceptable for Weeks 1–6. |
| **Impact if wrong** | Spring Security must be added to the Week 3 skeleton, increasing implementation time. |

---

### A-03 — Database (Initial Version)

| Attribute | Detail |
|---|---|
| **Assumption** | The H2 in-memory database is used for the initial version (Weeks 1–6). Data does not persist between application restarts. |
| **Rationale** | H2 requires no external installation and is ideal for development and demonstration. |
| **Status** | **Confirmed** — suitable for academic demonstration. |
| **Impact if wrong** | If a persistent database (e.g., MySQL or PostgreSQL) is required for demonstration, the data source configuration and connection details must be added. JPA entity code does not need to change. |

---

### A-04 — Renewal Alert Window

| Attribute | Detail |
|---|---|
| **Assumption** | A policy is considered "Expiring Soon" if its expiry date falls within 30 days of today. This value is configurable via `application.properties`. |
| **Rationale** | 30 days is a common industry standard for advance renewal notice. |
| **Status** | **Provisional** — may be adjusted based on faculty guidance or demonstration requirements. |
| **Impact if wrong** | Change a single property value; no code restructuring required. |

---

### A-05 — Development Environment

| Attribute | Detail |
|---|---|
| **Assumption** | The student will install a compatible Java LTS JDK (Java 17 or Java 21) and use the Maven Wrapper included in the project. No additional manual Maven installation is required after JDK setup. |
| **Rationale** | Java is not currently on PATH on this machine. The Maven Wrapper eliminates the need to install Maven separately. |
| **Status** | **Pending** — Java JDK must be downloaded and installed by the student before the application can be built. See `docs/week-03/setup-instructions.md`. |
| **Impact if delayed** | The application cannot be compiled or run until JDK is installed. All documentation and code can still be created in advance. |

---

### A-06 — Academic Demonstration Environment

| Attribute | Detail |
|---|---|
| **Assumption** | Demonstrations will take place on the student's laptop using the embedded H2 database and no external services. The application will be accessed via `http://localhost:8080` in a web browser. |
| **Rationale** | Academic demonstrations are typically lab-based, using local development environments. |
| **Status** | **Provisional** — confirm with faculty whether a server or cloud deployment is expected for any evaluation milestone. |

---

### A-07 — Data Privacy

| Attribute | Detail |
|---|---|
| **Assumption** | Only synthetic (fictitious) customer data will be used throughout development and testing. No real policyholder names, emails, national identity numbers or financial information will be entered into the system at any stage. |
| **Rationale** | Legal, ethical and academic policy requirement. |
| **Status** | **Confirmed** — must not be changed at any stage without formal ethical approval. |

---

### A-08 — Renewal Status Logic

| Attribute | Detail |
|---|---|
| **Assumption** | Renewal status is a combination of a stored value (`RENEWED`) and derived values computed from dates. When an agent marks a policy as Renewed, the stored status field is set to `RENEWED` and this overrides date-based classification permanently for that record. |
| **Rationale** | This is the simplest approach that prevents a renewed policy from being reclassified as Expired as time passes. |
| **Status** | **Provisional** — the exact data model and status-derivation algorithm will be finalised in the Week 3 SRS and architecture documents. |

---

### A-09 — Email and SMS

| Attribute | Detail |
|---|---|
| **Assumption** | The application will not send emails or SMS messages in the initial version (Weeks 1–6). The "reminder" in the project title refers to the system's ability to display which policies require attention, not to automated outbound notifications. |
| **Rationale** | Automated notifications require an email server or third-party SMS gateway, adding configuration complexity and external dependencies that are not appropriate for the initial academic phase. |
| **Status** | **Confirmed** for Weeks 1–6. Automated notifications are classified as a future enhancement. |

---

### A-10 — Single-Developer Project

| Attribute | Detail |
|---|---|
| **Assumption** | This is an individual student project. Collaboration workflows (branch protection rules, pull request reviews from multiple reviewers) will be simulated using personal feature branches to demonstrate the process. |
| **Rationale** | The project brief is for an individual submission. |
| **Status** | **Confirmed**. |

---

## Section 2: Constraints

### C-01 — Time Constraint

The project must be completed within a 15-week academic semester. Complex features (microservices, real integrations, mobile applications) are explicitly excluded because they cannot be delivered within this timeline by a single beginner student.

---

### C-02 — Technology Constraint

The technology stack (Java, Spring Boot, Maven, Thymeleaf, H2, JUnit, Git, Jenkins, Selenium, Docker, Ansible/Puppet) is prescribed by the project brief and cannot be arbitrarily changed. Any proposed change must be discussed with the faculty supervisor.

---

### C-03 — No External Services

The application must not depend on any external API, cloud service, payment gateway or SMS/email provider that is not available free of charge and without a production account for academic use.

---

### C-04 — Academic Scope

The project is an academic demonstration, not a production system. It is not subject to insurance industry regulatory requirements, data protection legislation compliance audits or commercial service-level agreements. However, basic good practices (data privacy, no secrets in source control) must still be observed.

---

### C-05 — Environment Compatibility

The application must run on the student's Windows development machine. Deployment to Linux servers will be addressed in later weeks (Docker/Ansible phase). Cross-platform compatibility is ensured by using the Maven Wrapper and avoiding OS-specific scripts in the main application.

---

## Assumptions and Constraints Summary

| ID | Type | Description | Status |
|---|---|---|---|
| A-01 | Assumption | Fixed policy types list | Provisional |
| A-02 | Assumption | No login/RBAC in initial version | Provisional |
| A-03 | Assumption | H2 in-memory database | Confirmed |
| A-04 | Assumption | 30-day renewal alert window | Provisional |
| A-05 | Assumption | JDK to be installed by student | Pending |
| A-06 | Assumption | Localhost demonstration | Provisional |
| A-07 | Assumption | Synthetic data only | Confirmed |
| A-08 | Assumption | Stored RENEWED overrides date logic | Provisional |
| A-09 | Assumption | No email/SMS in initial version | Confirmed |
| A-10 | Assumption | Individual project | Confirmed |
| C-01 | Constraint | 15-week timeline | Fixed |
| C-02 | Constraint | Prescribed technology stack | Fixed |
| C-03 | Constraint | No external services | Fixed |
| C-04 | Constraint | Academic scope only | Fixed |
| C-05 | Constraint | Windows development machine | Fixed |
