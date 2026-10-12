# DevOps Lifecycle Design

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

> **Important:** This document describes the **intended** DevOps workflow for this project. The complete pipeline has not yet been implemented. Tools and stages will be introduced progressively across the 15-week project. This document is the design blueprint, not a report of current status.

---

## What Is DevOps?

**DevOps** is a set of practices that combines software **Dev**elopment and IT **Op**eration**s**. The goal is to shorten the time between writing code and delivering it to users, while ensuring the software remains correct and reliable at every stage.

Instead of developers writing code in isolation and then handing it to a separate team to deploy, DevOps automates the steps between "code is written" and "code is running" through a pipeline of tools.

---

## The DevOps Lifecycle for This Project

The lifecycle consists of nine stages. Each stage feeds into the next:

```
Plan → Code → Version Control → Review → Build → Test → Package → Deploy → Verify/Monitor
                                                                                    ↓
                                                                             Roll Back (if needed)
```

---

## Stage-by-Stage Explanation

### Stage 1 — Plan
**What happens:** Requirements, user stories and sprint tasks are defined.  
**Tools:** This project's documentation files (SRS, backlog, sprint plan), GitHub Issues.  
**Academic week:** Weeks 1–3.  
**Output:** A clear, prioritised list of what needs to be built.

---

### Stage 2 — Code
**What happens:** The developer writes Java source code, Thymeleaf templates and configuration files.  
**Tools:** IDE (IntelliJ IDEA, VS Code or Eclipse), Java 17/21, Spring Boot.  
**Academic week:** Weeks 3–6.  
**Output:** Working source code in the project directory.

---

### Stage 3 — Version Control
**What happens:** Code changes are saved to a Git repository. Each change is committed with a descriptive message. Feature branches are used so that incomplete work does not affect the main branch.  
**Tools:** Git, GitHub.  
**Academic week:** Week 4 onward.  
**Output:** A complete history of all changes; every version of the code is recoverable.

---

### Stage 4 — Review
**What happens:** Before a feature branch is merged into the main branch, it is reviewed (either by a team member or by the student themselves using a pull request). The review checks that the code meets the Definition of Done.  
**Tools:** GitHub Pull Requests.  
**Academic week:** Week 4 onward.  
**Output:** Reviewed, approved code merged to the main branch.

---

### Stage 5 — Build
**What happens:** Jenkins automatically detects the new commit on the main branch (or a configured trigger) and runs the Maven build. Maven compiles the Java source code, checks for compilation errors and downloads any missing dependencies.  
**Tools:** Jenkins, Maven (via Maven Wrapper `mvnw`).  
**Academic week:** Week 7 onward.  
**Output:** A compiled application class set or a `.jar` file.  
**Failure behaviour:** If compilation fails, Jenkins marks the build as FAILED. No further stages are executed. The developer is notified.

---

### Stage 6 — Automated Testing
**What happens:** Jenkins runs all JUnit unit tests and integration tests. In a later week, Selenium browser tests are also executed.  
**Tools:** JUnit 5, Spring Boot Test, Selenium WebDriver.  
**Academic week:** Weeks 7–10.  
**Output:** A test report showing which tests passed and which failed.  
**Failure behaviour:** If any required test fails, Jenkins marks the build as FAILED. Deployment does **not** proceed. This is a critical safety rule: broken code must never reach the deployed environment.

---

### Stage 7 — Package
**What happens:** Maven assembles the compiled application and all its dependencies into a single executable file called a **JAR** (Java ARchive). This self-contained file includes the embedded Tomcat web server.  
**Tools:** Maven (`mvn package`), Spring Boot Maven Plugin.  
**Academic week:** Week 8 onward.  
**Output:** A `.jar` file (e.g., `policy-renewal-system-1.0.0.jar`).

---

### Stage 8 — Deploy
**What happens:** The packaged JAR is deployed to a running environment. Initially this is the development machine. In later weeks, the JAR is built into a Docker container image and the container is started automatically.  
**Tools:** Jenkins (deploy stage), Docker (`docker build`, `docker run`).  
**Academic week:** Week 8 (basic deploy), Week 12 (Docker-based deploy).  
**Output:** A running instance of the application accessible at `http://localhost:8080`.

---

### Stage 9 — Verify / Monitor
**What happens:** After deployment, the system is verified by running Selenium browser tests against the live application. The application's health endpoint (`/actuator/health` or equivalent) can be checked to confirm it is responding.  
**Tools:** Selenium WebDriver, Spring Boot Actuator (optional).  
**Academic week:** Week 10 onward.  
**Output:** Confirmation that the deployed application is working correctly.

---

### Stage 10 — Recover / Roll Back
**What happens:** If a deployment fails the post-deployment verification, the previous working version is restored. This can be done by re-deploying the previous Docker image or by running the previous JAR.  
**Tools:** Docker (image tags for versioning), Jenkins (build history).  
**Academic week:** Week 14 (Ansible/Puppet health check and recovery).  
**Output:** The application is restored to the last known good state.

---

## Tool-to-Stage Mapping

| Stage | Tool | Introduced In Week |
|---|---|---|
| Plan | Documentation, GitHub Issues | 1–3 |
| Code | Java, Spring Boot, IDE | 3–6 |
| Version Control | Git, GitHub | 4 |
| Review | GitHub Pull Requests | 4 |
| Build | Jenkins, Maven | 7 |
| Test (Unit/Integration) | JUnit, Spring Boot Test | 3+ |
| Test (Browser) | Selenium | 9–10 |
| Package | Maven, Spring Boot Plugin | 8 |
| Deploy | Jenkins, Docker | 8, 12 |
| Verify/Monitor | Selenium, Spring Boot Actuator | 10+ |
| Recover/Roll Back | Docker image tags, Ansible/Puppet | 14 |

---

## Build and Test Failure Policy

This rule is **mandatory** and must be enforced by the Jenkins pipeline:

> **A failed required test or compilation error must prevent the application from being deployed.**

This means:
- If `mvn test` fails → the pipeline stops at the Test stage. The package and deploy stages do not run.
- If `mvn compile` fails → the pipeline stops at the Build stage.
- If a Selenium test fails → the deploy stage does not proceed.

This policy protects the deployed environment from receiving broken software.

---

## Diagram

See `devops-lifecycle.mmd` in this folder for the Mermaid diagram of this lifecycle.

---

## Current Implementation Status

| Stage | Status |
|---|---|
| Plan | ✅ Complete (Weeks 1–3) |
| Code | 🔄 Skeleton created (Week 3); full implementation in Weeks 4–6 |
| Version Control | 📋 Not started — Week 4 |
| Review | 📋 Not started — Week 4 |
| Build | 📋 Not started — Week 7 |
| Test (JUnit) | 🔄 Context test only (Week 3) |
| Test (Selenium) | 📋 Not started — Week 9 |
| Package | 📋 Not started — Week 8 |
| Deploy | 📋 Not started — Week 8 |
| Verify | 📋 Not started — Week 10 |
| Recover | 📋 Not started — Week 14 |
