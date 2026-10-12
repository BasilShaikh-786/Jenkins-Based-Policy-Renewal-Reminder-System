# Requirements Review

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## Purpose

This document records the cross-document consistency review performed at the end of Week 3. It confirms that the SRS, architecture, database design, page design and route design are all mutually consistent and correctly derived from the Week 1 and Week 2 documents.

---

## Review Findings

| Check | Result | Notes |
|---|---|---|
| All FR-01 to FR-15 are covered in the SRS | ✅ Pass | Confirmed in SRS Section 2 |
| All NFR-01 to NFR-10 are addressed in the architecture | ✅ Pass | NFR-02 (layers), NFR-04 (security), NFR-05 (testability) all addressed |
| Business rules BR-01 to BR-07 are consistent with FR requirements | ✅ Pass | BR-03 (status) aligns with FR-11, NFR-10; BR-04 with FR-14 |
| SRS scope matches Week 1 scope document | ✅ Pass | No new features added; exclusions consistent |
| Architecture layers match NFR-02 (no logic in controllers) | ✅ Pass | Documented explicitly in architecture document |
| Database fields support all functional requirements | ✅ Pass | All required fields present; `renewalStatus` design addresses FR-11 |
| `renewalStatus` design prevents FR-11 / NFR-10 violation | ✅ Pass | Only `RENEWED` is stored; all other statuses are derived |
| Page design covers all routes in route design | ✅ Pass | 9 routes match 7 pages (some pages handle GET + POST) |
| Route design is Thymeleaf-compatible (no REST JSON) | ✅ Pass | All routes return HTML views or redirects |
| Sample data uses synthetic values only | ✅ Pass | Names are fictitious; no real personal data |
| No feature deferred to later weeks is implemented in Week 3 | ✅ Pass | Delete, expired page, Actuator are marked as future |

---

## Issues Found and Resolved

| Issue | Resolution |
|---|---|
| Initial design had `renewalStatus` storing all four states | Changed to store only `RENEWED`; all other statuses derived from dates. Documented rationale in database-design.md. |
| Route for "Mark as Renewed" was missing | Added Route #8: `POST /policies/{id}/renew` |
| No empty-state handling documented for pages | Added empty-state descriptions to Page 2 (list) and Page 6 (upcoming renewals) |

---

## Open Questions (Not Blocking)

These questions do not block Week 3 work but should be discussed with the faculty supervisor:

1. **Role-based access control** (A-02) — confirmed as future enhancement; no impact on current design.
2. **Policy type list** (A-01) — using Health, Life, Vehicle, Home, Travel; confirm with faculty.
3. **Renewal alert window** (A-04) — defaulting to 30 days; confirm if a different default is preferred.

---

## Sign-Off

The SRS, architecture, database design, page design and route design are internally consistent as of October 2026. No contradictions were found that would block implementation.

Review performed by: Basil Shaikh | Date: October 2026
