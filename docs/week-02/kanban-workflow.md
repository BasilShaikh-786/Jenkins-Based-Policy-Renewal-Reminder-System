# Kanban Workflow

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## What Is a Kanban Board?

A **Kanban board** is a visual tool for tracking the progress of tasks. Tasks are represented as cards. Each card moves from left to right across columns as it progresses from "not started" to "done". The board gives you an instant picture of what is being worked on, what is waiting and what is finished.

---

## Board Columns

The board for this project uses five columns:

### 1. Backlog
**Definition:** All tasks that have been identified but not yet started.  
**Rule:** Tasks enter this column when they are created and added to the product backlog (`product-backlog.md`). A task remains here until it is selected for active work.  
**Contents:** All BL-xx tasks not yet started.

---

### 2. Ready
**Definition:** Tasks that are fully understood, have all their dependencies resolved and can be picked up immediately.  
**Rule:** A task moves from Backlog to Ready only when:
- All dependencies are complete.
- The task description and acceptance criteria are clear.
- Any required tools or resources are available.

**Example:** BL-13 (Create Policy entity) moves to Ready only after BL-06 (Maven project structure) is Done.

---

### 3. In Progress
**Definition:** Tasks currently being actively worked on.  
**Rule:** Maximum **2 tasks** may be In Progress at the same time. This is called the **WIP Limit** (Work In Progress Limit). The WIP limit prevents the student from starting too many tasks at once and finishing none.

**If the limit is reached:** Complete or move to Review one of the current tasks before starting a new one.

---

### 4. Review
**Definition:** Tasks whose main work is done but which need to be checked before being marked complete.  
**Rule:** A task moves to Review when:
- The code or document has been written.
- The acceptance criteria appear to be met.
- Tests have been run (if applicable).

**Review activities:**
- Re-read the acceptance criteria and verify each point.
- Check that documentation has been updated.
- For code tasks: ensure the application still compiles and tests still pass.
- Save any required evidence (screenshots, test reports).

---

### 5. Done
**Definition:** Tasks that are fully complete according to the Definition of Done.  
**Rule:** A task moves to Done only when **all** conditions in the Definition of Done are met. It never moves backward from Done unless a serious defect is discovered that invalidates the original completion.

---

## WIP Limit Policy

| Column | Maximum Tasks |
|---|---|
| Backlog | No limit |
| Ready | No limit |
| **In Progress** | **2** |
| Review | No limit |
| Done | No limit |

Setting an In Progress limit of 2 is realistic for a single developer. It ensures focus and prevents task-switching overhead.

---

## Rules for Moving Cards

| From | To | Rule |
|---|---|---|
| Backlog | Ready | All dependencies are complete; task is fully understood |
| Ready | In Progress | WIP limit not exceeded; student is actively starting this task |
| In Progress | Review | Task is implemented; acceptance criteria appear to be met; tests run |
| Review | Done | All Definition of Done criteria are confirmed; evidence saved |
| Review | In Progress | A review revealed an issue that requires more work |
| Done | In Progress | **Discouraged.** Only if a critical defect makes the task invalid. Requires explanation. |

---

## How to Create the Board

Since GitHub account access is not available during this automated phase, the board cannot be created on GitHub automatically. Follow these manual steps to create it:

1. Open your GitHub repository at `https://github.com/BasilShaikh-786/Jenkins-Based-Policy-Renewal-Reminder-System`.
2. Click the **"Projects"** tab.
3. Click **"New project"**.
4. Select **"Board"** layout.
5. Name it: **"Policy Renewal System — Kanban Board"**.
6. Add five columns in this order: **Backlog**, **Ready**, **In Progress**, **Review**, **Done**.
7. Set the WIP limit for **In Progress** to **2** (GitHub Projects allows WIP limits on columns).
8. Add each backlog task (BL-01 through BL-40) as a card in the Backlog column.
9. Move cards to Ready or In Progress as you begin each week's work.

---

## Visual Example of the Board

```
┌────────────┬────────────┬─────────────────┬─────────────┬────────────┐
│  Backlog   │   Ready    │   In Progress   │   Review    │    Done    │
│            │            │   (max 2)       │             │            │
├────────────┼────────────┼─────────────────┼─────────────┼────────────┤
│ BL-15      │ BL-13      │ BL-07 ◄current  │             │ BL-01      │
│ BL-16      │ BL-14      │ BL-08 ◄current  │             │ BL-02      │
│ BL-17      │            │                 │             │ BL-03      │
│ ...        │            │                 │             │ BL-04      │
│            │            │                 │             │ BL-05      │
│            │            │                 │             │ BL-06      │
└────────────┴────────────┴─────────────────┴─────────────┴────────────┘
```

(This is a hypothetical snapshot during Week 3 work.)
