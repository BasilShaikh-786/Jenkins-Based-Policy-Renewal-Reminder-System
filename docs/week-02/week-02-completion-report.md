# Week 2 Completion Report

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C  
**Week:** 2 — Agile Planning and DevOps Workflow  
**Date:** October 2026

---

## 1. Summary of Work Completed

Week 2 translated the Week 1 requirements and problem statement into a complete Agile planning and DevOps workflow framework. The following work was completed:

- 13 user stories (US-01 through US-13) were written, covering all 15 functional requirements.
- Acceptance criteria were written using Given/When/Then format for all 13 stories.
- A requirements traceability matrix was produced, mapping all 15 FRs to user stories, acceptance criteria and test cases.
- A product backlog of 40 tasks (BL-01 through BL-40) was created across 10 epics, spanning all 15 project weeks.
- A 15-week sprint plan and roadmap was produced.
- The Kanban workflow was defined with five columns and a WIP limit of 2.
- The Definition of Done was documented with criteria for documentation, coding and DevOps tasks.
- The complete intended DevOps lifecycle was described and mapped to tools.
- A Mermaid flowchart of the DevOps lifecycle was created.
- A task dependency document was created, identifying the critical path and key blocking risks.

---

## 2. Decisions Made

| Decision | Rationale |
|---|---|
| WIP limit set to 2 | Appropriate for a single developer; prevents context switching |
| All 13 stories are Must or Should have | No Could-have stories are needed for MVP; some Should-have stories (filter) can slip without breaking core functionality |
| Failed test must prevent deployment | Critical DevOps safety rule; protects the deployed environment |
| GitHub Projects board not created automatically | Requires manual creation by the student (instructions provided in kanban-workflow.md) |

---

## 3. Documents Created

| File | Purpose |
|---|---|
| `docs/week-02/user-stories.md` | 13 user stories with all required fields |
| `docs/week-02/acceptance-criteria.md` | Given/When/Then criteria for all 13 stories |
| `docs/week-02/requirements-traceability.md` | Matrix mapping FR → Story → AC → Test case |
| `docs/week-02/product-backlog.md` | 40 tasks across 10 epics |
| `docs/week-02/sprint-plan.md` | 15-week roadmap with dependencies and milestones |
| `docs/week-02/kanban-workflow.md` | Kanban board definition, rules and WIP limit |
| `docs/week-02/definition-of-done.md` | Full Definition of Done with 21 criteria |
| `docs/week-02/devops-lifecycle.md` | DevOps pipeline stages, tools and failure policy |
| `docs/week-02/devops-lifecycle.mmd` | Mermaid flowchart diagram |
| `docs/week-02/project-task-dependencies.md` | Dependency table and critical path |
| `docs/week-02/week-02-completion-report.md` | This report |

---

## 4. Consistency Review

- ✅ All 15 functional requirements (FR-01 to FR-15) trace to at least one user story.
- ✅ All 13 user stories trace to at least one acceptance criterion in Given/When/Then format.
- ✅ Product backlog tasks reference the correct story IDs.
- ✅ Sprint plan week assignments are consistent with the backlog priority order.
- ✅ DevOps tools match the technology stack defined in the project scope.
- ✅ No document claims the pipeline is currently operational.
- ✅ Scope exclusions (email, SMS, RBAC, payments) are not included in any story or backlog task.

---

## 5. Items Requiring Manual Action by Student

| Action | Location |
|---|---|
| Create GitHub Projects Kanban board | Instructions in `kanban-workflow.md` |
| Install Java JDK before Week 3 coding begins | Instructions in `docs/week-03/setup-instructions.md` (created in Week 3) |

---

## 6. Unresolved Questions

The same open questions from Week 1 remain. No new ambiguities were introduced by Week 2.

---

## 7. Completion Checklist

| Item | Status |
|---|---|
| User stories written for all FRs | ✅ Complete |
| Given/When/Then acceptance criteria | ✅ Complete |
| Requirements traceability matrix | ✅ Complete |
| Product backlog (40 tasks) | ✅ Complete |
| 15-week roadmap | ✅ Complete |
| Kanban workflow defined | ✅ Complete |
| Definition of Done documented | ✅ Complete |
| DevOps lifecycle described | ✅ Complete |
| Mermaid diagram created | ✅ Complete |
| Task dependencies documented | ✅ Complete |
| Consistency with Week 1 checked | ✅ Reviewed |

---

**Week 2 status: COMPLETE**

Proceed to Week 3: Requirements, Architecture and Technology Setup.
