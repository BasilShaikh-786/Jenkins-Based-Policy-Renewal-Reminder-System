# Week 1 Completion Report

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C  
**Week:** 1 — Problem Definition and Scope  
**Date:** October 2026

---

## 1. Summary of Work Completed

Week 1 established the entire conceptual and documentary foundation for the project. The following work was completed:

- The core problem (manual policy renewal tracking) was defined and explained in plain English.
- All stakeholders were identified, described and categorised as direct users or indirect stakeholders.
- One main objective and eight specific, measurable objectives were documented.
- The project scope was defined, including initial features, potential future enhancements and explicit exclusions.
- Fifteen functional requirements (FR-01 through FR-15) were documented with IDs, priorities, expected behaviours, acceptance conditions and suggested test cases.
- Ten non-functional requirements (NFR-01 through NFR-10) were documented across categories including usability, maintainability, reliability, security and testability.
- Ten assumptions (A-01 through A-10) and five constraints (C-01 through C-05) were recorded with their status (Confirmed, Provisional or Pending).
- A formal problem statement was produced, covering all required academic sections.

---

## 2. Decisions Made

| Decision | Rationale |
|---|---|
| Use H2 embedded database for initial version | Requires no external installation; ideal for development and demonstration |
| Use monolithic Spring Boot architecture | Appropriate for academic scope; avoids unnecessary complexity |
| No email/SMS in initial scope | Avoids external service dependencies; classified as future enhancement |
| Role-based access deferred | Adds complexity without core value in initial version |
| Default renewal alert window: 30 days | Common industry standard; configurable via application properties |
| RENEWED stored status overrides date classification | Prevents renewed policies from being incorrectly shown as Expired |
| Synthetic data only | Required by academic ethics and data privacy |

---

## 3. Documents Created

| File | Purpose |
|---|---|
| `docs/week-01/problem-definition.md` | Explains the problem in plain English for the reader |
| `docs/week-01/stakeholders.md` | Identifies and describes all stakeholders |
| `docs/week-01/project-objectives.md` | Defines the main and specific project objectives |
| `docs/week-01/project-scope.md` | Defines scope, future enhancements and exclusions |
| `docs/week-01/functional-requirements.md` | FR-01 through FR-15 with full detail |
| `docs/week-01/non-functional-requirements.md` | NFR-01 through NFR-10 with verification methods |
| `docs/week-01/assumptions-and-constraints.md` | A-01–A-10 and C-01–C-05 with status |
| `docs/week-01/final-problem-statement.md` | Formal academic problem statement |
| `docs/week-01/week-01-completion-report.md` | This report |

---

## 4. Unresolved Questions (Requiring Faculty Confirmation)

| Question | Relates To | Impact |
|---|---|---|
| Is role-based access control required in the initial version? | A-02, Scope | If yes, Spring Security must be added to Week 3 skeleton |
| What policy types should be supported? | A-01 | Dropdown list values in create/edit form |
| Is the H2 in-memory database acceptable for evaluation demonstrations? | A-03 | If no, MySQL/PostgreSQL configuration required |
| Is a 30-day renewal alert window acceptable? | A-04 | Configurable property; no structural impact |
| Is a localhost demonstration acceptable for submissions? | A-06 | If a server deployment is required, Docker/Jenkins phase must begin earlier |

---

## 5. Consistency Review

The following cross-document consistency checks were performed:

- ✅ All fifteen functional requirements trace to features defined in the project scope.
- ✅ All non-functional requirements are consistent with the technology stack proposed in the project brief.
- ✅ The final problem statement objectives match the objectives defined in `project-objectives.md`.
- ✅ Scope exclusions (email, SMS, payments, real data) are consistently stated in all relevant documents.
- ✅ Assumption A-08 (renewal status logic) is consistent with FR-11 and NFR-10.
- ✅ No document claims the application is already built or tested at this stage.

---

## 6. Evidence to Save

- All nine documents listed in Section 3 above.
- This report.
- Any faculty feedback received on the provisional assumptions (A-01, A-02, A-04, A-06, A-08).

---

## 7. Completion Checklist

| Item | Status |
|---|---|
| Problem explained in plain English | ✅ Complete |
| Stakeholders identified and documented | ✅ Complete |
| Main and specific objectives defined | ✅ Complete |
| Project scope defined with inclusions and exclusions | ✅ Complete |
| Functional requirements FR-01 to FR-15 documented | ✅ Complete |
| Non-functional requirements NFR-01 to NFR-10 documented | ✅ Complete |
| Assumptions and constraints documented | ✅ Complete |
| Final problem statement produced | ✅ Complete |
| Documents internally consistent | ✅ Reviewed |
| Unresolved questions identified | ✅ Listed above |

---

**Week 1 status: COMPLETE**

Proceed to Week 2: Agile Planning and DevOps Workflow.
