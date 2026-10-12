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
- **Dynamic Policy Lifecycle Management:** Tracks policies across derived states (`ACTIVE`, `EXPIRING_SOON`, `EXPIRED`, `RENEWED`) with real-time date evaluation (eliminating stale database status flags).
- **Proactive Expiry Alerts:** Real-time dashboard identifying policies nearing expiration within configurable thresholds (e.g., 30 days).
- **Full Policy CRUD & Search:** Fast lookup by policyholder name or policy business number.
- **Continuous Integration & Delivery (CI/CD):** Automated build verification, unit testing of core business rules, artifact generation, and scheduled reminder execution via Jenkins.

---

## 🏛️ System Architecture

The application is structured as a **Monolithic Layered Architecture**:
1. **Presentation Layer:** Spring MVC Controllers (`HomeController`, `PolicyController`) delivering server-rendered HTML via Thymeleaf with a modern, responsive design system.
2. **Business Service Layer:** `PolicyService` enforcing all 7 core Business Rules (BR-01 through BR-07) and dynamic status calculation.
3. **Data Persistence Layer:** Spring Data JPA (`PolicyRepository`) executing optimized JPQL queries against the underlying database.
4. **Database:** H2 In-Memory database for rapid local development and test execution.

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
│   │   │   ├── PolicyRenewalApplication.java
│   │   │   ├── controller/             # Spring MVC controllers (Home, Policy)
│   │   │   ├── model/                  # JPA entities (Policy)
│   │   │   ├── repository/             # Spring Data repositories
│   │   │   └── service/                # Business logic and status derivation
│   │   └── resources/
│   │       ├── application.properties  # Runtime configuration & H2 setup
│   │       ├── data.sql                # Synthetic seed dataset (10 records)
│   │       ├── static/css/             # Modern CSS stylesheet (style.css)
│   │       └── templates/              # Thymeleaf views (index, policies, error)
│   └── test/java/com/policy/renewal/   # JUnit 5 & MockMvc automated test suites
```

---

## 🚀 Getting Started

### Prerequisites
- **Java JDK 17 or 21 (LTS)** installed and configured in `PATH`.
- **Apache Maven 3.8+** (or configured IDE build tool).
- **Git** version control.

### Running Locally

1. **Verify Java Installation:**
   ```bash
   java -version
   ```

2. **Run Application via Maven:**
   ```bash
   mvn spring-boot:run
   ```

3. **Access the Web Application:**
   - **Dashboard:** Open your browser at [http://localhost:8080](http://localhost:8080)
   - **H2 Database Console:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
     *(JDBC URL: `jdbc:h2:mem:policyrenewaldb`, Username: `sa`, Password: leave blank)*

### Running the Automated Test Suite

```bash
mvn test
```

Tests verify:
- Spring Boot Application Context initialization.
- SRS Business Rules (BR-01 uniqueness, BR-03 status derivation, BR-04 date sequencing).
- Spring MVC web layer route endpoints and form validations.

---

## 📋 Project Roadmap

- **Week 1:** Project charter, problem definition, repository configuration. *(Completed)*
- **Week 2:** Agile backlog, 13 user stories, acceptance criteria, DevOps workflow. *(Completed)*
- **Week 3:** SRS specification, architecture diagrams, DB schema, application skeleton, initial UI. *(Completed)*
- **Weeks 4–6:** Advanced validation, filtering, pagination, and enhanced UI polish.
- **Weeks 7–8:** Jenkins CI/CD pipeline integration, automated build triggers, and scheduled email reminders.
- **Weeks 9–10:** Selenium test automation and end-to-end regression validation.
- **Weeks 11–12:** Docker containerisation and multi-environment deployment.
- **Weeks 13–15:** Final documentation, performance verification, and project demonstration.
