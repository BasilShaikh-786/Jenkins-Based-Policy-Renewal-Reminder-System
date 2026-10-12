# Requirements Traceability Matrix

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## Purpose

This document maps each functional requirement to its related user stories, acceptance criteria and planned test cases. This ensures that every requirement is covered by at least one story and one acceptance condition, and that nothing is missed.

---

## Traceability Matrix

| Requirement ID | Requirement Summary | Related User Story | Acceptance Criteria | Planned Test Case(s) |
|---|---|---|---|---|
| FR-01 | Create a new policy | US-01 | AC-US-01 | TC-US-01, TC-FR-01 |
| FR-02 | View all policies | US-02 | AC-US-02 | TC-US-02, TC-FR-02 |
| FR-03 | View individual policy details | US-03 | AC-US-03 | TC-US-03, TC-FR-03 |
| FR-04 | Update policy information | US-04 | AC-US-04 | TC-US-04, TC-FR-04 |
| FR-05 | Prevent duplicate policy numbers | US-05, US-01 | AC-US-05 | TC-US-05, TC-FR-05 |
| FR-06 | Search by customer name | US-06 | AC-US-06 | TC-US-06, TC-FR-06 |
| FR-07 | Search by policy number | US-07 | AC-US-07 | TC-US-07, TC-FR-07 |
| FR-08 | Filter by renewal status | US-08 | AC-US-08 | TC-US-08, TC-FR-08 |
| FR-09 | View upcoming renewals | US-09 | AC-US-09 | TC-US-09, TC-FR-09 |
| FR-10 | View expired policies | US-10 | AC-US-10 | TC-US-10, TC-FR-10 |
| FR-11 | Mark a policy as renewed | US-11 | AC-US-11 | TC-US-11, TC-FR-11 |
| FR-12 | Display dashboard statistics | US-12 | AC-US-12 | TC-US-12, TC-FR-12 |
| FR-13 | Validate required fields | US-13, US-01, US-04 | AC-US-13 | TC-FR-13a, TC-FR-13b |
| FR-14 | Validate date logic | US-01, US-04, US-13 | AC-US-01 (date clause) | TC-FR-14 |
| FR-15 | Validate premium amount | US-01, US-04, US-13 | AC-US-01 (premium clause) | TC-FR-15 |

---

## Coverage Analysis

| Metric | Count |
|---|---|
| Total functional requirements | 15 |
| Requirements covered by at least one user story | 15 |
| Requirements covered by at least one acceptance criterion | 15 |
| Requirements with at least one planned test case | 15 |
| **Coverage** | **100%** |

---

## Non-Functional Requirements Traceability

| NFR ID | Category | Verified By | Related Stories |
|---|---|---|---|
| NFR-01 | Usability | Demonstration review | All stories |
| NFR-02 | Maintainability | Code review | All stories |
| NFR-03 | Reliability | Integration tests | US-01, US-04, US-03 |
| NFR-04 | Security | Inspection | US-01, US-04 |
| NFR-05 | Testability | `mvnw test` | All stories |
| NFR-06 | Data Validation | TC-FR-13 to TC-FR-15 | US-13 |
| NFR-07 | Performance | Manual observation | US-02, US-12 |
| NFR-08 | Portability | Clean-clone build | All stories |
| NFR-09 | Accessibility | Template review | All stories |
| NFR-10 | Status Consistency | Unit tests | US-09, US-10, US-11, US-12 |

---

## Traceability Notes

1. FR-13 through FR-15 (validation requirements) are addressed by US-13 but also apply to US-01 and US-04 wherever data is entered. They are not separate stories because validation is a cross-cutting concern applied to all data entry.

2. FR-11 (Mark as Renewed) is critical for preventing incorrect Expired classification. Its traceability to NFR-10 is intentional.

3. No requirement has been identified that is not covered by at least one user story. If new requirements are added, this matrix must be updated before implementation begins.
