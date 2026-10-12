# Product Backlog

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## Priority Scale
- **Must** — Required for the MVP. Cannot be deferred.
- **Should** — Important; include if time permits.
- **Could** — Nice to have; defer if time is limited.

## Effort Scale
- **Small** — Less than half a day of focused work.
- **Medium** — Half a day to one full day.
- **Large** — More than one day; may span multiple sessions.

---

## EPIC 1: Documentation and Planning (Weeks 1–3)

| Task ID | Story ID | Description | Priority | Effort | Dependencies | Deliverable | Acceptance Condition |
|---|---|---|---|---|---|---|---|
| BL-01 | — | Write Week 1 documentation | Must | Large | None | 9 Week 1 docs | All files present and internally consistent |
| BL-02 | — | Write Week 2 Agile planning docs | Must | Large | BL-01 | 11 Week 2 docs | All files present; consistent with Week 1 |
| BL-03 | — | Write Week 3 SRS and architecture | Must | Large | BL-02 | SRS, architecture, DB design, page design | Consistent with all previous docs |
| BL-04 | — | Complete environment readiness check | Must | Small | None | environment-readiness.md | All tool statuses recorded accurately |
| BL-05 | — | Write Week 3 completion report | Must | Small | BL-03, BL-10 | week-03-completion-report.md | All checklist items addressed |

---

## EPIC 2: Application Skeleton (Week 3)

| Task ID | Story ID | Description | Priority | Effort | Dependencies | Deliverable | Acceptance Condition |
|---|---|---|---|---|---|---|---|
| BL-06 | All | Create Maven project structure | Must | Small | JDK installed | `pom.xml`, directory layout | `./mvnw validate` passes |
| BL-07 | All | Create Spring Boot entry point | Must | Small | BL-06 | `PolicyRenewalApplication.java` | Application starts without errors |
| BL-08 | All | Configure application properties and H2 | Must | Small | BL-06 | `application.properties` | H2 console accessible at `/h2-console` |
| BL-09 | All | Create Thymeleaf home page | Must | Small | BL-07 | `index.html`, `HomeController.java` | Browser shows home page at `localhost:8080` |
| BL-10 | All | Write application context test | Must | Small | BL-07 | `PolicyRenewalApplicationTests.java` | Test passes: `./mvnw test` |
| BL-11 | All | Create `.gitignore` | Must | Small | BL-06 | `.gitignore` | Target dir and IDE files excluded |
| BL-12 | All | Write README | Must | Small | BL-06 | `README.md` | Startup instructions correct |

---

## EPIC 3: Policy Entity and Repository (Week 3–4)

| Task ID | Story ID | Description | Priority | Effort | Dependencies | Deliverable | Acceptance Condition |
|---|---|---|---|---|---|---|---|
| BL-13 | US-01–US-12 | Create Policy JPA entity | Must | Small | BL-06 | `Policy.java` | Entity maps to H2 table on startup |
| BL-14 | US-01–US-12 | Create Policy repository interface | Must | Small | BL-13 | `PolicyRepository.java` | Spring Data auto-implements queries |
| BL-15 | US-02, US-09 | Add custom repository queries | Should | Medium | BL-14 | Query methods in repo | Queries return correct results in tests |

---

## EPIC 4: Policy Management Features (Weeks 4–6)

| Task ID | Story ID | Description | Priority | Effort | Dependencies | Deliverable | Acceptance Condition |
|---|---|---|---|---|---|---|---|
| BL-16 | US-01 | Implement Create Policy feature | Must | Medium | BL-13, BL-14 | Form, Controller, Service method | TC-US-01 passes |
| BL-17 | US-02 | Implement View All Policies list | Must | Medium | BL-16 | List page and controller | TC-US-02 passes |
| BL-18 | US-03 | Implement View Policy Details | Must | Small | BL-17 | Detail page and controller | TC-US-03 passes |
| BL-19 | US-04 | Implement Edit Policy | Must | Medium | BL-18 | Edit form and controller | TC-US-04 passes |
| BL-20 | US-11 | Implement Mark as Renewed action | Must | Small | BL-19 | Renewed status update endpoint | TC-US-11 passes |
| BL-21 | US-09 | Implement Upcoming Renewals view | Must | Medium | BL-20 | Renewal list page and service query | TC-US-09 passes |
| BL-22 | US-12 | Implement Dashboard statistics | Must | Medium | BL-20 | Dashboard page with live counts | TC-US-12 passes |
| BL-23 | US-06, US-07 | Implement Search functionality | Must | Medium | BL-17 | Search form and filtered results | TC-US-06, TC-US-07 pass |
| BL-24 | US-08 | Implement Status filter | Should | Medium | BL-17 | Filter controls on list page | TC-US-08 passes |
| BL-25 | US-13 | Implement all form validation | Must | Medium | BL-16, BL-19 | Validation annotations and error messages | TC-FR-13 to TC-FR-15 pass |

---

## EPIC 5: Git and GitHub (Week 4)

| Task ID | Story ID | Description | Priority | Effort | Dependencies | Deliverable | Acceptance Condition |
|---|---|---|---|---|---|---|---|
| BL-26 | — | Initialise Git repository | Must | Small | BL-11 | `.git` directory, initial commit | `git log` shows first commit |
| BL-27 | — | Push to GitHub | Must | Small | BL-26 | Remote repository with all files | GitHub repo accessible |
| BL-28 | — | Create feature branch workflow | Must | Small | BL-27 | Feature branches and PRs | Merge via pull request |

---

## EPIC 6: Jenkins CI (Weeks 7–8)

| Task ID | Story ID | Description | Priority | Effort | Dependencies | Deliverable | Acceptance Condition |
|---|---|---|---|---|---|---|---|
| BL-29 | — | Install Jenkins | Must | Medium | BL-27 | Running Jenkins instance | Jenkins UI accessible |
| BL-30 | — | Create Jenkins pipeline | Must | Large | BL-29 | `Jenkinsfile` | Pipeline runs build and tests |
| BL-31 | — | Deploy via Jenkins | Must | Large | BL-30 | Automated deployment | Application running after pipeline |

---

## EPIC 7: Selenium Testing (Weeks 9–10)

| Task ID | Story ID | Description | Priority | Effort | Dependencies | Deliverable | Acceptance Condition |
|---|---|---|---|---|---|---|---|
| BL-32 | US-01–US-12 | Design Selenium test cases | Must | Large | BL-16–BL-25 | Test class files | Tests run without errors |
| BL-33 | — | Integrate Selenium with Jenkins | Must | Medium | BL-30, BL-32 | Pipeline stage for browser tests | Tests run in Jenkins |

---

## EPIC 8: Docker (Weeks 11–12)

| Task ID | Story ID | Description | Priority | Effort | Dependencies | Deliverable | Acceptance Condition |
|---|---|---|---|---|---|---|---|
| BL-34 | — | Create Dockerfile | Must | Medium | BL-31 | `Dockerfile` | `docker build` succeeds |
| BL-35 | — | Run application in Docker | Must | Medium | BL-34 | Running container | App accessible at mapped port |
| BL-36 | — | Jenkins-Docker CD pipeline | Must | Large | BL-30, BL-35 | Automated Docker deployment | Container starts after commit |

---

## EPIC 9: Ansible/Puppet (Weeks 13–14)

| Task ID | Story ID | Description | Priority | Effort | Dependencies | Deliverable | Acceptance Condition |
|---|---|---|---|---|---|---|---|
| BL-37 | — | Write Ansible playbook or Puppet manifest | Must | Large | BL-36 | Playbook / manifest files | Automated provisioning succeeds |
| BL-38 | — | Automated health check and recovery | Should | Large | BL-37 | Health-check task in playbook | Recovery confirmed |

---

## EPIC 10: Final Validation (Week 15)

| Task ID | Story ID | Description | Priority | Effort | Dependencies | Deliverable | Acceptance Condition |
|---|---|---|---|---|---|---|---|
| BL-39 | — | End-to-end pipeline validation | Must | Medium | All above | Full pipeline run evidence | All stages green |
| BL-40 | — | Final documentation and submission | Must | Medium | BL-39 | All week reports complete | Faculty submission ready |
