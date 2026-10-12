# Jenkins-Based Policy Renewal Reminder System

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Build](https://img.shields.io/badge/Build-Maven-blue.svg)](https://maven.apache.org/)
[![Database](https://img.shields.io/badge/Database-H2%20In--Memory-lightblue.svg)](https://www.h2database.com/)
[![CI/CD](https://img.shields.io/badge/DevOps-Jenkins-red.svg)](https://www.jenkins.io/)

Final-Year Engineering Capstone Project  
**Student:** Basil Shaikh  
**Roll Number:** 23102C0026  
**Branch:** Computer Engineering, CMPN C  

---

## 📖 Project Overview

The **Jenkins-Based Policy Renewal Reminder System** is an automated insurance operations management and proactive notification platform. It addresses the common industry failure of unrenewed policies and lapses by coupling a robust Spring Boot web management application with an automated DevOps CI/CD and notification pipeline powered by Jenkins.

### Key Capabilities
- **Renewal Dashboard:** Real-time metrics computed directly via database queries: Total Policies, Active Policies, Policies Expiring within 7 Days, Policies Expiring within 30 Days, Expired Policies, and Renewed Policies.
- **Policy Expiry Reminder Engine:** Evaluates active policies approaching expiration across configurable discrete reminder windows (`30`, `7`, and `1` day). Correctly ignores already expired policies, already renewed policies, and safely handles missing expiry dates.
- **Renewal Cycle Duplicate Prevention:** Ensures at most one successful reminder per policy, per window, per renewal cycle.
- **Safe Simulation / Dry-Run Notification Mode:** Reminders execute in safe dry-run mode without requiring SMTP credentials or sending real emails. Every reminder attempt is logged and tracked with `SIMULATED`, `SENT`, or `FAILED` audit status.
- **Automated Scheduling & DevOps Triggers:** Configurable background daily reminder check (`0 0 8 * * ?`) with atomic concurrency locks, plus an independent REST endpoint (`POST /api/reminders/trigger`) and web UI button for on-demand execution.
- **Dynamic Policy Lifecycle Management:** Tracks policies across derived states (`ACTIVE`, `EXPIRING_SOON`, `EXPIRED`, `RENEWED`) with real-time date evaluation.
- **Full Policy CRUD & Search:** Fast lookup by policyholder name, policy number, or email.

---

## 🏛️ System Architecture

The application is structured as a **Monolithic Layered Architecture**:
1. **Presentation Layer:** Spring MVC Controllers (`HomeController`, `PolicyController`, `ReminderController`) delivering server-rendered HTML via Thymeleaf with a modern, responsive design system.
2. **Business Service Layer:**
   - `PolicyService`: Enforces core Business Rules (BR-01 through BR-07), dynamic status derivation, and database count metrics.
   - `PolicyReminderService`: Evaluates policy expiration eligibility against discrete windows, coordinates duplicate suppression, and logs execution audit trails.
   - `NotificationService`: Interface abstraction currently implemented by `SimulationNotificationService` for safe dry-run logging.
3. **Data Persistence Layer:** Spring Data JPA (`PolicyRepository`, `ReminderRecordRepository`) executing optimized JPQL queries against H2.
4. **Database:** Embedded H2 in-memory database (`POLICY` and `REMINDER_RECORD` tables).
5. **Scheduler:** `ReminderScheduler` running automated daily checks with concurrency guard (`AtomicBoolean`).

---

## 📁 Repository Structure

```
├── .gitignore                          # Git ignore configuration
├── Jenkinsfile                         # Declarative Jenkins CI/CD pipeline
├── pom.xml                             # Maven build and dependency configuration
├── README.md                           # Project documentation overview
├── docs/                               # Engineering documentation
│   ├── week-01/                        # Problem definition, charter, tech stack
│   ├── week-02/                        # Agile plan, user stories, DevOps workflow
│   └── week-03/                        # SRS, Architecture, DB design, Reports
├── src/
│   ├── main/
│   │   ├── java/com/policy/renewal/
│   │   │   ├── PolicyRenewalApplication.java   # App entry point with @EnableScheduling & Clock bean
│   │   │   ├── controller/                     # Spring MVC controllers (Home, Policy, Reminder)
│   │   │   ├── model/                          # JPA entities (Policy, ReminderRecord)
│   │   │   ├── repository/                     # Spring Data JPA repositories (Policy, ReminderRecord)
│   │   │   ├── scheduler/                      # Background cron scheduler (ReminderScheduler)
│   │   │   └── service/                        # Business services, reminder engine & notifications
│   │   │       ├── PolicyService.java
│   │   │       ├── PolicyReminderService.java
│   │   │       ├── ReminderRunReport.java
│   │   │       └── notification/               # Safe simulation notification service
│   │   └── resources/
│   │       ├── application.properties          # Runtime config, reminder windows & cron setup
│   │       ├── data.sql                        # Synthetic seed dataset (10 records)
│   │       ├── static/css/                     # Modern CSS stylesheet (style.css)
│   │       └── templates/                      # Thymeleaf views (index, policies, reminders, error)
│   └── test/java/com/policy/renewal/           # Automated test suite (44 tests, 100% passing)
```

---

## 🚀 Getting Started

### Prerequisites
- **Java JDK 17 or 21 (LTS)** installed and configured in `PATH`.
- **Apache Maven 3.8+** (or configured IDE build tool).
- **Git** version control.

### Running Locally (PowerShell Command)

To run the application using Maven:
```powershell
mvn spring-boot:run
```

Or run the packaged JAR artifact directly:
```powershell
java -jar target/policy-renewal-system-1.0.0-SNAPSHOT.jar
```

### Accessing the Web Application
- **Dashboard:** Open your browser at [http://localhost:8081](http://localhost:8081)
- **Reminder Logs & Audit Trail:** [http://localhost:8081/reminders](http://localhost:8081/reminders)
- **Upcoming Renewals Queue:** [http://localhost:8081/policies/upcoming](http://localhost:8081/policies/upcoming)
- **H2 Database Console:** [http://localhost:8081/h2-console](http://localhost:8081/h2-console)  
  *(JDBC URL: `jdbc:h2:mem:policyrenewaldb`, Username: `sa`, Password: leave blank)*

---

## 🧪 Automated Testing

Run the full automated test suite (44 tests):
```powershell
mvn clean test
```

### What Tests Verify:
1. **Reminder Windows:** Policies expiring in 30, 7, and 1 day match their discrete windows.
2. **Exclusion Logic:** Expired policies, already renewed policies, and policies outside configured windows (e.g., 15 or 45 days) are safely ignored.
3. **Missing Expiry Dates:** Policies with null expiry dates are handled safely without exceptions.
4. **Duplicate Prevention:** Subsequent executions within the same renewal cycle skip already notified policies.
5. **Dry-Run / Simulation:** Simulates dispatches and persists `SIMULATED` audit records.
6. **Failure Handling:** Notification failures record `FAILED` status and error messages without marking them sent.
7. **Scheduler Concurrency:** Prevents overlapping execution runs.
8. **Dashboard Statistics:** Live JPQL counts for 7-day, 30-day, active, expired, and total policies.
9. **Spring MVC & API Endpoints:** Controller routing, form validation, and JSON REST trigger responses.

---

## 🔔 Testing the Reminder Engine Manually

1. **Start the Application:**
   ```powershell
   mvn spring-boot:run
   ```
2. **Visit the Dashboard:** Navigate to `http://localhost:8081`. Notice the KPI cards: Total, Active, Expiring in 7 Days, Expiring in 30 Days, Expired, and Renewed.
3. **Trigger Simulation Check:**
   - Click the **"▶ Run Reminder Check Now"** button on the top banner of the Dashboard or the Reminder Logs page.
   - Alternatively, trigger via PowerShell / curl:
     ```powershell
     Invoke-RestMethod -Uri "http://localhost:8081/api/reminders/trigger" -Method POST
     ```
4. **Inspect Console Logs:**
   Notice the simulation log entries:
   `[SIMULATION] Renewal Reminder Dispatched -> Policy: POL-XXXX | Customer: ... | Window: ...`
5. **Inspect the Audit Trail:**
   Navigate to `http://localhost:8081/reminders` to view the audit table recording Log ID, timestamp, policy number, recipient, reminder window, cycle expiry date, and status (`SIMULATED`).
6. **Verify Duplicate Prevention:**
   Click the **"▶ Run Reminder Check Now"** button again. Notice the flash message and logs report that eligible policies were identified as already reminded and skipped (`Skipped (Duplicate)`).
7. **Inspect Policy Details:**
   Open any policy detail page (e.g. `http://localhost:8081/policies/1`) to view the policy-specific reminder notification history table.

---

## ⚠️ Current System Limitations

1. **Simulation Mode Only:** Real outbound emails are not sent. The notification engine runs strictly in dry-run simulation mode (`notification.simulation-mode=true`) to prevent accidental email dispatch without configured SMTP credentials.
2. **In-Memory Database:** Data resides in an H2 in-memory database (`jdbc:h2:mem:policyrenewaldb`). All state is reset upon restarting the application server. Initial synthetic test data is reloaded from `data.sql` at boot.
3. **Single System Timezone:** Scheduling uses system default timezone (`Clock.systemDefaultZone()`). Multi-timezone policyholder configurations are planned for a subsequent milestone.

