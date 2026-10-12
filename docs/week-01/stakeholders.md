# Stakeholder Analysis

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## Overview

A stakeholder is any person, group or organisation that has an interest in, or is affected by, the outcome of this project. Stakeholders are divided into two categories:

- **Direct users** — people who will actually log in and operate the application.
- **Indirect stakeholders** — people who are affected by the outcomes of the system but do not interact with it directly.

---

## Stakeholder 1: System Administrator

| Attribute | Detail |
|---|---|
| **Role** | System Administrator |
| **Type** | Direct user |
| **Who they are** | The technical person responsible for managing the application, its data and its users. This may be an IT officer in the insurance office or a senior agent assigned administrative duties. |
| **Primary needs** | Ability to create, update and delete policy records; access to all system data; ability to manage user accounts (future phase). |
| **Key concern** | Data accuracy, prevention of duplicates and the ability to correct errors. |
| **Expected interaction** | Full read and write access to all features. In a future phase, role-based access control will restrict certain operations to administrators only. |

---

## Stakeholder 2: Insurance Agent / Policy Manager

| Attribute | Detail |
|---|---|
| **Role** | Insurance Agent / Policy Manager |
| **Type** | Direct user |
| **Who they are** | The primary day-to-day user of the system. They create and update policy records, monitor upcoming renewals and mark policies as renewed after the customer has paid. |
| **Primary needs** | Easy policy creation and editing; a clear list of policies expiring soon; the ability to search for any policy quickly; a simple dashboard showing totals by status. |
| **Key concern** | Not missing a renewal deadline; quickly finding the right policy record when a customer calls. |
| **Expected interaction** | Full read and write access to policy records; dashboard and upcoming renewals view are their most frequently used pages. |

---

## Stakeholder 3: Read-Only Viewer / Reporting Manager

| Attribute | Detail |
|---|---|
| **Role** | Read-Only Viewer / Reporting Manager |
| **Type** | Direct user (restricted) |
| **Who they are** | A branch manager, compliance officer or supervisor who needs to see the state of the policy portfolio but does not need to create or modify records. |
| **Primary needs** | Access to dashboard statistics; ability to view all policies and filter by status or expiry window; ability to view individual policy details. |
| **Key concern** | Getting an accurate and up-to-date picture of the portfolio without risking accidental data changes. |
| **Expected interaction** | Read-only access — can view, search and filter but cannot create, edit or delete. Role-based access control is a planned future enhancement and may not be enforced in the initial version. |

> **Note:** Role-based access control (RBAC) is included in scope as a *should-have* feature for the initial version. If it is not implemented in the current phase, all authenticated users will have the same access level. This is an assumption that requires faculty confirmation. See the Assumptions and Constraints document.

---

## Stakeholder 4: Customer (Insurance Policyholder)

| Attribute | Detail |
|---|---|
| **Role** | Customer / Policyholder |
| **Type** | **Indirect** stakeholder |
| **Who they are** | The person whose insurance policy is stored in the system. The customer does not log in or interact with the application directly. |
| **Primary needs** | To be informed in time if their policy is approaching its renewal date, so that they do not experience a lapse in coverage. |
| **Key concern** | Continuity of insurance protection; personal data privacy. |
| **Expected interaction** | None — the customer does not use the application. An agent uses the system on their behalf. Future enhancements may include automated email notifications to the customer. |
| **Data stored about them** | Customer name, email address, policy type, premium amount and relevant dates. |
| **Privacy consideration** | Customer data must be handled responsibly. No real customer data should be used during development or testing. Synthetic sample data is used throughout. |

---

## Stakeholder 5: Faculty Evaluator / Academic Supervisor

| Attribute | Detail |
|---|---|
| **Role** | Faculty / Academic Evaluator |
| **Type** | Indirect stakeholder |
| **Who they are** | The college faculty member who will assess the project against academic criteria. |
| **Primary needs** | Clear documentation of requirements, architecture and design decisions; evidence that DevOps practices have been correctly applied; a working runnable application at each submission milestone. |
| **Key concern** | Correct application of software engineering principles, Agile methodology and DevOps tools as per the syllabus. |
| **Expected interaction** | Reviews submitted documents and demonstration. |

---

## Stakeholder Summary Table

| Stakeholder | Type | Interacts with System | Primary Concern |
|---|---|---|---|
| System Administrator | Direct user | Yes — full access | Data accuracy and control |
| Insurance Agent / Policy Manager | Direct user | Yes — full access | Renewal monitoring and records |
| Read-Only Viewer / Manager | Direct user | Yes — read-only | Portfolio visibility |
| Customer / Policyholder | Indirect | No | Timely renewal and data privacy |
| Faculty / Academic Evaluator | Indirect | Reviews only | Academic compliance |
