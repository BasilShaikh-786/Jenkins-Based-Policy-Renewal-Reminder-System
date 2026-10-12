# Sprint Plan and 15-Week Project Roadmap

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## Introduction

This document organises the project's product backlog into a realistic 15-week plan for a single beginner student. Each week has a clear focus, a set of tasks to complete and defined outputs. Tasks from the backlog are grouped into logical phases.

> **Note:** Academic projects do not use sprints of fixed equal length in the same way as commercial Agile teams. This plan uses a week-by-week format that is more appropriate for an academic semester. Each week is treated as a sprint of one week's duration.

---

## Phase 1: Foundation (Weeks 1–3)

### Week 1 — Problem Definition and Scope
**Focus:** Understand the problem and document it clearly.  
**Tasks:** BL-01  
**Outputs:** 9 Week 1 documentation files  
**Milestone:** Problem statement approved by faculty  
**Status:** ✅ Complete

---

### Week 2 — Agile Planning and DevOps Workflow
**Focus:** Convert requirements into stories and plan the project.  
**Tasks:** BL-02  
**Outputs:** 11 Week 2 documentation files  
**Milestone:** Product backlog and roadmap reviewed  
**Status:** ✅ Complete

---

### Week 3 — Requirements, Architecture and Technology Setup
**Focus:** Detailed design and initial application skeleton.  
**Tasks:** BL-03, BL-04, BL-05, BL-06, BL-07, BL-08, BL-09, BL-10, BL-11, BL-12, BL-13, BL-14  
**Outputs:** SRS, architecture docs, DB design, Spring Boot skeleton, README  
**Milestone:** Application starts with `./mvnw spring-boot:run`; context test passes  
**Blocker:** Java JDK must be installed before BL-06 through BL-10 can be executed  
**Status:** 🔄 In Progress

---

## Phase 2: Application Development (Weeks 4–6)

### Week 4 — Git/GitHub Setup and Create Policy Feature
**Focus:** Initialise Git repository, push to GitHub, implement Create Policy form.  
**Tasks:** BL-26, BL-27, BL-28, BL-16  
**Outputs:** GitHub repository, first feature branch, Create Policy form working  
**Dependencies:** Java JDK installed; Week 3 skeleton complete  
**Milestone:** First feature merged via pull request; policy can be created

---

### Week 5 — Policy CRUD: View, Detail, Edit
**Focus:** Implement View All, View Detail and Edit Policy features.  
**Tasks:** BL-17, BL-18, BL-19, BL-15  
**Outputs:** Working list page, detail page, edit form  
**Dependencies:** Week 4 complete  
**Milestone:** Three-screen CRUD (Create/Read/Update) operational

---

### Week 6 — MVP Completion and Collaboration
**Focus:** Implement Upcoming Renewals, Dashboard, Search, Mark as Renewed. Complete MVP.  
**Tasks:** BL-20, BL-21, BL-22, BL-23, BL-24, BL-25  
**Outputs:** Fully functional MVP; all FR-01 through FR-15 implemented  
**Dependencies:** Week 5 complete  
**Milestone:** All functional requirements demonstrated successfully with synthetic data

---

## Phase 3: Continuous Integration (Weeks 7–8)

### Week 7 — Jenkins Installation and Initial Pipeline
**Focus:** Install Jenkins; create first pipeline that builds the application.  
**Tasks:** BL-29, BL-30 (partial)  
**Outputs:** Running Jenkins instance; pipeline that runs `mvn clean test`  
**Dependencies:** Week 6 complete; GitHub repository set up  
**Milestone:** Jenkins builds the project automatically on a code push

---

### Week 8 — Pipeline as Code and Deployment
**Focus:** Write a `Jenkinsfile`; configure the pipeline to build, test and deploy.  
**Tasks:** BL-30 (complete), BL-31  
**Outputs:** `Jenkinsfile` in repository; automated deployment step  
**Dependencies:** Week 7 complete  
**Milestone:** Committing code triggers a full build-test-deploy pipeline automatically

---

## Phase 4: Automated Browser Testing (Weeks 9–10)

### Week 9 — Selenium Test Design and Local Execution
**Focus:** Write Selenium browser tests for key application workflows.  
**Tasks:** BL-32  
**Outputs:** Selenium test classes in `src/test/java`  
**Dependencies:** Week 6 MVP complete  
**Milestone:** Selenium tests run locally against the running application

---

### Week 10 — Continuous Testing in Jenkins
**Focus:** Add Selenium tests as a pipeline stage in Jenkins.  
**Tasks:** BL-33  
**Outputs:** Updated `Jenkinsfile` with browser test stage; test report in Jenkins  
**Dependencies:** Weeks 8 and 9 complete  
**Milestone:** A failed Selenium test prevents deployment from proceeding

---

## Phase 5: Containerisation (Weeks 11–12)

### Week 11 — Docker Image and Container Lifecycle
**Focus:** Create a Dockerfile; build and run the application as a Docker container.  
**Tasks:** BL-34, BL-35  
**Outputs:** `Dockerfile`; `docker run` evidence  
**Dependencies:** Week 8 complete; Docker installed  
**Milestone:** Application runs in a Docker container and is accessible at the mapped port

---

### Week 12 — Jenkins-Docker Continuous Deployment
**Focus:** Extend the Jenkins pipeline to build a Docker image and deploy a container.  
**Tasks:** BL-36  
**Outputs:** Updated `Jenkinsfile` with Docker build and run stages  
**Dependencies:** Weeks 10 and 11 complete  
**Milestone:** Full CI/CD: commit → Jenkins builds → tests → Docker image → running container

---

## Phase 6: Configuration Management (Weeks 13–14)

### Week 13 — Ansible or Puppet: Initial Configuration
**Focus:** Write an Ansible playbook or Puppet manifest to provision the server and install Java and Docker.  
**Tasks:** BL-37 (partial)  
**Outputs:** Playbook or manifest file  
**Dependencies:** Week 12 complete; Ansible or Puppet installed  
**Milestone:** Playbook runs without errors on target environment

---

### Week 14 — Automated Provisioning, Health Checks and Recovery
**Focus:** Complete the playbook; add health-check and application restart tasks.  
**Tasks:** BL-37 (complete), BL-38  
**Outputs:** Complete playbook with health-check; recovery demonstrated  
**Dependencies:** Week 13 complete  
**Milestone:** Automated recovery works when the application is manually stopped

---

## Phase 7: Final Validation (Week 15)

### Week 15 — End-to-End Validation and Submission
**Focus:** Run the complete pipeline end-to-end; complete all documentation; prepare submission.  
**Tasks:** BL-39, BL-40  
**Outputs:** Final pipeline run evidence; complete repository; all 15 week reports  
**Dependencies:** All previous weeks complete  
**Milestone:** Faculty submission delivered on time with all required evidence

---

## Task Dependency Summary

```
Week 1 → Week 2 → Week 3 → Week 4 → Week 5 → Week 6
                                                    ↓
                              Week 7 → Week 8 → Week 10
                                                    ↑
                                                 Week 9
                              Week 11 → Week 12
                              Week 13 → Week 14
                                    All → Week 15
```

**Key blockers:**
1. **Java JDK installation** (before Week 3 code tasks can execute).
2. **Git/GitHub setup** (before Week 4 feature branches).
3. **Full MVP** (before Week 7 CI integration is meaningful).
4. **Jenkins running** (before Week 8 pipeline can be written).
5. **Docker installed** (before Week 11 containerisation).

---

## Velocity Note

Each implementation week assumes approximately **8–12 hours** of focused study and coding work. If a week requires more time, the sprint tasks can be split, but the milestone sequence should not be reordered.
