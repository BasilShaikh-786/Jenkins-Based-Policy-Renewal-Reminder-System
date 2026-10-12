# Definition of Done

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## What Is the Definition of Done?

The **Definition of Done (DoD)** is the agreed checklist that every task must satisfy before it is moved to the "Done" column on the Kanban board. It ensures consistency and prevents tasks from being declared complete when they are only partially finished.

A task is **not Done** just because the code has been written or the document has been created. It is Done only when every applicable criterion on this list has been met and confirmed.

---

## General Definition of Done (All Tasks)

These criteria apply to every task, whether it is a documentation task, a coding task or a configuration task.

| # | Criterion | How to Verify |
|---|---|---|
| 1 | **Acceptance criteria met** | Read each acceptance condition in `acceptance-criteria.md` for the related story. Confirm each one is true. |
| 2 | **No regressions** | All previously passing tests still pass. No new failures were introduced. |
| 3 | **Documentation updated** | Any document affected by the change has been updated. If a new file was created, it is referenced in the appropriate index or report. |
| 4 | **Self-reviewed** | The student has re-read the work and checked it for errors, missing sections and inconsistencies. |
| 5 | **Evidence saved** | Any required evidence (screenshot, test report, build log) has been saved in the appropriate location. |
| 6 | **No secrets introduced** | The work does not add passwords, API keys, real customer data or environment-specific paths to the repository. |

---

## Documentation Tasks — Additional Criteria

These additional criteria apply when the task produces a Markdown document.

| # | Criterion |
|---|---|
| 7 | All required sections are present (as defined in the project brief or task specification). |
| 8 | The document is internally consistent — it does not contradict itself. |
| 9 | The document is consistent with other project documents (requirements match the scope, stories match the requirements, etc.). |
| 10 | All status labels (Confirmed, Provisional, Pending) are correct and up to date. |

---

## Coding Tasks — Additional Criteria

These additional criteria apply when the task produces Java source code, configuration files or template files.

| # | Criterion |
|---|---|
| 11 | The application compiles without errors (`./mvnw compile` exits with code 0). |
| 12 | All existing tests pass (`./mvnw test` exits with code 0). |
| 13 | The specific acceptance test for this task passes (unit test, integration test or manual test as appropriate). |
| 14 | No business logic has been placed in a controller. No database queries have been written in a service class. The layered architecture is maintained. |
| 15 | No hard-coded strings that should be in configuration files (e.g., database URLs, port numbers, timeouts). |
| 16 | Code follows Java naming conventions (camelCase for variables and methods, PascalCase for classes). |
| 17 | New code paths (service methods, validation rules) have at least one associated test. |

---

## Pipeline and DevOps Tasks — Additional Criteria

These additional criteria apply to Jenkins, Docker and Ansible/Puppet tasks.

| # | Criterion |
|---|---|
| 18 | The pipeline stage or configuration script runs without errors. |
| 19 | A build failure causes the pipeline to stop and does not proceed to the deploy stage. |
| 20 | The evidence of the successful run is saved (e.g., Jenkins build log screenshot, Docker container ID). |
| 21 | The `Jenkinsfile` or configuration file is committed to the repository, not configured only through the Jenkins UI. |

---

## Important Rules

1. **A test file existing is not the same as a test passing.** A task involving testing is not Done unless the test has been executed and produced a passing result.

2. **A task cannot be marked Done if a known defect exists in its output**, even if the defect is in a lower-priority area. The defect must either be fixed or formally recorded as a known issue and deferred to a later sprint.

3. **Documentation tasks cannot be marked Done** if they contain placeholder text (e.g., "[TO BE FILLED]"), unresolved notes, or sections that contradict other approved documents.

4. **Moving a task from Review to Done is a deliberate act.** Do not move it automatically. Sit down, read through each criterion and confirm it.

---

## Quick Checklist

Before moving any task to Done, ask these questions:

- [ ] Does the output meet all the stated acceptance criteria?
- [ ] Have I run all tests and confirmed they pass?
- [ ] Have I updated any documentation that was affected?
- [ ] Have I saved any required evidence?
- [ ] Have I introduced any secrets or real data into the repository?
- [ ] Does this change break any previously working feature?
