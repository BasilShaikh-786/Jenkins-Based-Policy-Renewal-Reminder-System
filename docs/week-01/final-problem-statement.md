# Final Problem Statement

**Project Title:** Jenkins-Based Policy Renewal Reminder System  
**Student Name:** Basil Shaikh  
**Roll Number:** 23102C0026  
**Branch:** Computer Engineering, CMPN C  
**Institution:** [Your College Name]  
**Academic Year:** 2026–2027  
**Date:** October 2026

---

## 1. Introduction

Insurance is a cornerstone of modern financial planning and risk management. Millions of individuals, families and businesses rely on insurance policies to protect themselves against unforeseen events such as illness, accidents, property damage and loss of life. For these protections to remain effective, insurance policies must be actively maintained and renewed before their expiry dates.

In a typical insurance office, agents and administrators are responsible for managing a large portfolio of policies across many customers. The management of renewal deadlines is a time-sensitive and error-prone process that, if handled poorly, can result in customers losing their coverage — often without realising it until a claim is needed.

This project proposes and implements a software solution to this problem, combining modern web application development with professional DevOps practices to deliver a reliable, maintainable and testable Insurance Policy Renewal Reminder System.

---

## 2. Background

### 2.1 The Policy Renewal Challenge

Every insurance policy has a defined period of coverage, ending on an expiry date. Renewing a policy before this date is the responsibility of the customer, typically assisted and prompted by their insurance agent. In many insurance offices, particularly small and medium-sized operations, tracking these expiry dates relies on:

- Paper-based records or filing systems.
- General-purpose spreadsheets (Microsoft Excel, Google Sheets).
- Agent memory and personal calendars.
- Informal WhatsApp or email reminders between agent and customer.

These manual methods are adequate when an agent manages only a handful of policies. They become increasingly unreliable as the portfolio grows, because:

- Spreadsheets have no automatic sorting by urgency.
- There is no standardised way to classify policies as Active, Expiring Soon, Expired or Renewed.
- Duplicate entries are common and difficult to detect.
- There is no centralised view for a manager to oversee the entire portfolio.
- Manual checking of every record to find those needing attention is inefficient and time-consuming.

### 2.2 The DevOps Opportunity

Modern software teams do not merely write code — they establish repeatable, automated processes for building, testing, deploying and monitoring software. This discipline is called **DevOps** (a combination of Development and Operations). DevOps practices — including version control with Git, continuous integration with Jenkins, automated testing with Selenium and containerisation with Docker — ensure that software is delivered reliably, quickly and consistently.

This project uses the Insurance Policy Renewal Reminder System as a platform to learn and apply these professional DevOps practices in a structured academic context.

---

## 3. Problem Statement

Insurance agents and administrators in small and medium-sized insurance offices do not have access to a dedicated, centralised tool for managing policy renewal deadlines. As a result, they rely on manual methods (paper records, spreadsheets, personal calendars) to track expiry dates across a growing portfolio of policies. This leads to missed renewals, data inconsistencies, duplicate records, delayed discovery of expired policies and inadequate management oversight.

The absence of a structured, automated renewal-monitoring solution places insurance customers at risk of experiencing gaps in coverage and exposes insurance offices to reputational harm, reduced customer retention and potential legal liability.

Furthermore, there is an educational gap: many computer engineering students complete their degrees without practical exposure to the DevOps practices and toolchains that modern software teams use. This project addresses both problems simultaneously.

---

## 4. Proposed Solution

This project proposes the design and development of a **Jenkins-Based Policy Renewal Reminder System**: a web application that centralises insurance policy records and automatically identifies policies requiring renewal attention, while being built and maintained using a complete DevOps toolchain.

### 4.1 Application Features (Initial Version)

The application will allow agents and administrators to:

- Create and maintain a central database of insurance policy records.
- View all policies in a searchable, filterable list.
- Automatically identify and display policies whose expiry dates fall within a configurable alert window (default: 30 days).
- Automatically identify expired policies and distinguish them from renewed policies.
- View a dashboard displaying live counts of policies by status.
- Search for any policy by customer name or policy number.
- Validate all data entries, preventing duplicate policy numbers, invalid dates and other data errors.

### 4.2 DevOps Toolchain

The application will be built using a professional, industry-standard DevOps pipeline:

| Stage | Tool |
|---|---|
| Version Control | Git and GitHub |
| Build and Test | Maven and JUnit |
| Continuous Integration | Jenkins |
| Browser Automated Testing | Selenium |
| Containerisation | Docker |
| Configuration Management | Ansible or Puppet |

Each tool will be introduced progressively across the 15-week project timeline, ensuring that concepts are understood before they are applied.

---

## 5. Objectives

### Main Objective
To design, build and demonstrate a web-based Insurance Policy Renewal Reminder System that solves the manual policy-tracking problem while applying professional DevOps practices throughout the development lifecycle.

### Specific Objectives
1. **OBJ-1** — Implement centralised policy record management (Create, Read, Update) with server-side data validation.
2. **OBJ-2** — Implement automatic, consistent date-driven renewal status classification (Active, Expiring Soon, Expired, Renewed).
3. **OBJ-3** — Provide a dashboard displaying live portfolio statistics.
4. **OBJ-4** — Implement search by customer name and policy number, and filtering by status.
5. **OBJ-5** — Enforce data integrity through required-field validation, date-logic validation and duplicate-prevention.
6. **OBJ-6** — Build an automated Maven and JUnit pipeline that compiles and tests the application with a single command.
7. **OBJ-7** — Progressively integrate Git, Jenkins, Selenium, Docker and Ansible/Puppet across the 15-week project.
8. **OBJ-8** — Produce complete, consistent academic documentation for each project phase.

---

## 6. Scope

### 6.1 Included in Initial Scope (Weeks 1–6)
- Policy CRUD operations (Create, Read, Update).
- Search and filter functionality.
- Dashboard statistics.
- Server-side data validation.
- H2 embedded database.
- Thymeleaf server-rendered templates.
- Maven build and JUnit test foundation.

### 6.2 Planned for Later Weeks (7–15)
- Git and GitHub version control workflows.
- Jenkins continuous integration pipeline.
- Selenium automated browser tests.
- Docker containerisation.
- Ansible or Puppet configuration management.

### 6.3 Explicitly Excluded
- Real customer personal data.
- Email or SMS notification delivery.
- Payment processing.
- Integration with external insurance-company systems.
- Production cloud infrastructure.
- Role-based access control (may be added as a future enhancement).

---

## 7. Limitations

1. **No automated notifications:** The system displays which policies need renewal but does not automatically contact customers. Email/SMS is a future enhancement.
2. **In-memory database:** The H2 database resets on each application restart. Data does not persist between sessions in the initial version.
3. **No authentication:** The initial version does not implement login or role-based access control.
4. **Single-office scope:** The system is designed for a single insurance office, not a multi-tenant or enterprise environment.
5. **Synthetic data only:** No real personal, financial or insurance data will be used.
6. **Academic performance targets:** Performance targets are appropriate for a small demonstration dataset (up to 100 records), not for production scale.
7. **Java not yet installed:** The development environment requires Java JDK installation before the application can be compiled and run.

---

## 8. Expected Outcomes

By the end of the 15-week project, the following outcomes are expected:

| Outcome | Evidence |
|---|---|
| A working web application for managing insurance policy renewals | Live demonstration on localhost; source code in GitHub repository |
| Automatic renewal status classification | Unit tests for classification logic; demonstration with synthetic data |
| Dashboard displaying live portfolio statistics | Screenshot or live demonstration |
| Functional search and filter | Demonstrated with synthetic test data |
| Data validation preventing invalid entries | Test cases for each validation rule |
| A functioning Jenkins CI/CD pipeline | Jenkins build history screenshot |
| Automated browser tests using Selenium | Test report from Jenkins pipeline |
| Application packaged as a Docker image | Successful `docker run` execution |
| Automated server provisioning using Ansible or Puppet | Successful automated deployment |
| Complete academic documentation for all 15 weeks | Documentation files in the repository |

---

*This document was prepared as part of the Week 1 deliverable for the Jenkins-Based Policy Renewal Reminder System academic project.*
