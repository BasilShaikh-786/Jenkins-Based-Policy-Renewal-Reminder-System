# Project Objectives

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## Main Objective

To design, build and deliver a web-based Insurance Policy Renewal Reminder System that enables authorised agents and administrators to manage insurance policy records efficiently, automatically identify upcoming and overdue renewals, and provide portfolio-wide visibility through a dashboard — while demonstrating professional software engineering practices including Agile planning, DevOps tooling and automated testing.

---

## Specific Objectives

### Objective 1 — Policy Record Management
Design and implement a centralised database-backed system that allows agents to create, view, update and search insurance policy records, with all required fields validated at the point of entry.

**Measurable criterion:** All core CRUD operations (Create, Read, Update) work correctly for at least five synthetic policy records entered during a demonstration without errors.

---

### Objective 2 — Automatic Renewal Status Classification
Implement a consistent, date-driven mechanism that classifies each policy as Active, Expiring Soon, Expired or Renewed based on the policy's expiry date and a configurable renewal-alert window (default: 30 days), without requiring manual status updates for classification.

**Measurable criterion:** Given a set of test policies with known dates, every policy is correctly classified by the system. No policy that has been marked as Renewed is misclassified as Expired.

---

### Objective 3 — Dashboard and Reporting
Provide a home page dashboard that displays summary counts of total policies, active policies, policies expiring within the alert window, expired policies and renewed policies, derived from the live database at the time of page load.

**Measurable criterion:** Dashboard counts match the actual number of records in each category during a demonstration with at least ten synthetic policy records.

---

### Objective 4 — Search and Filter
Implement search by customer name and policy number, and filtering by renewal status and expiry window, so that agents can locate any policy record within a reasonable number of steps.

**Measurable criterion:** A search for an existing customer name or policy number returns the correct record. An invalid search returns a meaningful no-results message. Filtering by status returns only matching records.

---

### Objective 5 — Data Validation and Integrity
Enforce server-side validation rules ensuring that required fields are not blank, that policy numbers are unique, that expiry dates cannot precede start dates and that premiums are not negative, with user-friendly error messages returned to the form on failure.

**Measurable criterion:** Submitting a form with any invalid combination of data produces a descriptive error message and does not save incorrect data to the database.

---

### Objective 6 — Automated Build and Test Pipeline
Integrate the Maven build system and JUnit test suite so that the application can be compiled, tested and packaged with a single command. In later project weeks, this pipeline will be connected to Jenkins for continuous integration.

**Measurable criterion:** Running `./mvnw test` from the project root produces a build report indicating that all implemented unit and integration tests pass, with zero compilation errors.

---

### Objective 7 — DevOps Toolchain Integration (Planned)
Progressively integrate Git and GitHub for version control, Jenkins for continuous integration and delivery, Selenium for automated browser testing, Docker for containerisation and Ansible or Puppet for configuration management across the project's 15-week timeline.

**Measurable criterion:** By Week 15, the complete DevOps pipeline operates end-to-end: a code commit triggers a Jenkins build, tests run automatically, the application is packaged into a Docker image and deployed to a provisioned environment without manual intervention.

---

### Objective 8 — Academic Documentation
Produce complete and consistent project documentation covering the problem statement, stakeholder analysis, requirements, Agile planning, architecture, database design, page design, DevOps lifecycle and weekly completion reports.

**Measurable criterion:** All required documents listed in the weekly task specifications are present, internally consistent and free from unexplained contradictions at the time of each submission.

---

## Relationship Between Objectives

```
Main Objective
├── OBJ-1: Policy Record Management (foundation)
├── OBJ-2: Automatic Status Classification (core logic)
├── OBJ-3: Dashboard (visibility)
├── OBJ-4: Search and Filter (usability)
├── OBJ-5: Validation and Integrity (correctness)
├── OBJ-6: Automated Build and Test (quality)
├── OBJ-7: DevOps Toolchain (process)
└── OBJ-8: Documentation (academic compliance)
```

OBJ-1 through OBJ-5 are technical implementation objectives for the application itself.  
OBJ-6 and OBJ-7 are process and DevOps objectives that span the full 15 weeks.  
OBJ-8 is the academic documentation objective that runs in parallel with all other work.
