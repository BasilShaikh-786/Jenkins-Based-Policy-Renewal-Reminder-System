# Week 3 Completion Report

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: Computer Engineering, CMPN C  
**Week:** 3 — Requirements Specification, System Architecture, Database Design & Initial Application Skeleton  
**Date:** October 2026  

---

## 1. Summary of Work Completed

Week 3 bridged the conceptual and Agile planning phases into detailed engineering specifications, architectural designs, and a fully functional Spring Boot application skeleton. Key achievements include:

1. **Software Requirements Specification (SRS):** Authored an IEEE-aligned SRS covering System Purpose, 15 Functional Requirements (FR-01 to FR-15), 10 Non-Functional Requirements (NFR-01 to NFR-10), and 7 core Business Rules (BR-01 to BR-07).
2. **Use Case Modeling:** Authored formal specifications for 8 use cases (UC-01 through UC-08) covering policy creation, search, edit, renewal, upcoming alerts, and error scenarios.
3. **Software Architecture Documentation & Diagrams:** Documented the monolithic layered architecture (Presentation, Business/Service, Persistence, and Database layers), with design rationale and a Mermaid flowchart diagram (`architecture-diagram.mmd`).
4. **Database Design & ER Diagram:** Defined the `POLICY` entity schema, documented the design rationale behind dynamic status derivation vs. stored status, drafted column constraints, and created the Mermaid ER diagram (`database-er-diagram.mmd`).
5. **Page, Navigation & Route Design:** Documented the full sitemap, UI wireframe layouts, and RESTful/MVC route table covering all GET and POST operations, HTTP status handling, and flash message patterns.
6. **Requirements Consistency Review:** Conducted a comprehensive cross-document audit verifying alignment between SRS, Use Cases, Database Schema, and Routes.
7. **Environment Readiness Inspection:** Inspected the local machine environment, identified installed tools (Windows 11, Git 2.52, and workspace JDK 21), and generated setup guidance.
8. **Spring Boot Application Skeleton Implementation:**
   - `pom.xml`: Maven build configuration with Spring Boot 3.x, Spring Data JPA, Thymeleaf, Validation, and H2 database.
   - `PolicyRenewalApplication.java`: Main application entry point.
   - `Policy.java`: JPA entity with Jakarta Bean Validation constraints and lifecycle callbacks (`@PrePersist`, `@PreUpdate`).
   - `PolicyRepository.java`: Spring Data JPA repository interface with custom JPQL queries for status-based counts and date-window queries.
   - `PolicyService.java`: Comprehensive service layer implementing all business rules (BR-01 through BR-07) and dynamic status derivation algorithm.
   - `HomeController.java`: Dashboard controller delivering KPI metric counts and urgent renewal alerts.
   - `PolicyController.java`: Full CRUD and renewal controller with POST/Redirect/GET pattern and flash messaging.
   - `application.properties`: Configuration for H2 database, Thymeleaf, and alert window thresholds.
   - `data.sql`: 10 realistic synthetic policy records representing all lifecycle states.
   - Modern UI Layer: Sleek, responsive CSS design system (`style.css`), reusable layout fragments, and 6 Thymeleaf templates (`index.html`, `policies/list.html`, `policies/form.html`, `policies/detail.html`, `policies/upcoming.html`, `error/404.html`, `error/500.html`).
   - Test Suite: Context load test (`PolicyRenewalApplicationTests.java`), business rules unit tests (`PolicyServiceTest.java`), and MVC web tests (`PolicyControllerTest.java`, `HomeControllerTest.java`).
   - DevOps Pipeline: Declarative `Jenkinsfile` configuring compilation, automated JUnit testing, JAR packaging, and test report archiving.

---

## 2. Key Architectural Decisions

| Decision | Selected Option | Rationale |
|---|---|---|
| **Status Architecture (BR-03)** | Dynamic runtime derivation | Storing all statuses leads to stale data over time. By calculating ACTIVE, EXPIRING_SOON, and EXPIRED dynamically from the expiry date and only persisting the explicit RENEWED flag, status is always 100% accurate without background cron maintenance. |
| **Monolith vs Microservices** | Layered Monolith | Fits academic scope, simplifies deployment, and eliminates distributed networking overhead while preserving clean layer separation. |
| **Financial Calculations** | `BigDecimal` for premium amounts | Eliminates binary floating-point rounding errors common to `double` and `float`. |
| **Form Submission Pattern** | POST / Redirect / GET | Prevents duplicate form submissions on browser page refresh. |
| **Build & CI Automation** | Declarative Jenkinsfile | Enforces consistent CI/CD execution across local runs and automated Jenkins agents. |

---

## 3. Documents and Code Artifacts Created

### Documentation (docs/week-03/)
| File | Description |
|---|---|
| `software-requirements-specification.md` | Complete IEEE-compliant SRS document |
| `use-cases.md` | Detailed use cases UC-01 through UC-08 |
| `software-architecture.md` | Architectural styles, layers, and data flow |
| `architecture-diagram.mmd` | Mermaid architecture diagram |
| `database-design.md` | Database table design, field constraints, status algorithm |
| `database-er-diagram.mmd` | Mermaid ER diagram |
| `page-and-navigation-design.md` | Wireframes, navigation flows, and UI layouts |
| `api-and-route-design.md` | Route table, HTTP methods, and query parameters |
| `requirements-review.md` | Cross-document consistency verification matrix |
| `environment-readiness.md` | Automated local environment diagnostic report |
| `setup-instructions.md` | Step-by-step setup and execution instructions |
| `week-03-completion-report.md` | This summary completion report |

### Application Code (src/ & root)
| File | Description |
|---|---|
| `pom.xml` | Maven dependency management and build definitions |
| `src/main/java/.../PolicyRenewalApplication.java` | Spring Boot main application class |
| `src/main/java/.../model/Policy.java` | JPA entity with validation and audit timestamps |
| `src/main/java/.../repository/PolicyRepository.java` | Spring Data JPA repository with custom JPQL |
| `src/main/java/.../service/PolicyService.java` | Core business logic and status derivation service |
| `src/main/java/.../controller/HomeController.java` | Dashboard metrics controller |
| `src/main/java/.../controller/PolicyController.java` | Policy CRUD and renewal workflow controller |
| `src/main/resources/application.properties` | App properties, H2 in-memory DB configuration |
| `src/main/resources/data.sql` | 10 synthetic seed policy records |
| `src/main/resources/static/css/style.css` | Modern CSS design system and responsive styles |
| `src/main/resources/templates/fragments/layout.html` | Shared navbar, header, footer, and alerts |
| `src/main/resources/templates/index.html` | Dashboard with live KPI counters and quick alerts |
| `src/main/resources/templates/policies/list.html` | Policy directory with search and filter chips |
| `src/main/resources/templates/policies/form.html` | Create and edit policy form with validation errors |
| `src/main/resources/templates/policies/detail.html` | Detailed policy view with renewal action |
| `src/main/resources/templates/policies/upcoming.html` | Prioritized upcoming renewals list |
| `src/main/resources/templates/error/404.html` | Custom user-friendly 404 page |
| `src/main/resources/templates/error/500.html` | Custom user-friendly 500 error page |
| `src/test/java/.../PolicyRenewalApplicationTests.java` | Application context loading test |
| `src/test/java/.../service/PolicyServiceTest.java` | Unit tests for BR-01, BR-03, BR-04, BR-06 |
| `src/test/java/.../controller/PolicyControllerTest.java` | WebMvcTest for CRUD and renewal endpoints |
| `src/test/java/.../controller/HomeControllerTest.java` | WebMvcTest for dashboard home view |
| `Jenkinsfile` | Declarative CI/CD pipeline script |

---

## 4. Verification and Consistency Audit

- ✅ **Traceability:** Every functional requirement in the SRS is backed by repository queries, service methods, and controller endpoints.
- ✅ **Single Source of Truth:** Business Rule BR-03 (status derivation) is exclusively implemented in `PolicyService.getDerivedStatus()`, satisfying NFR-10.
- ✅ **Data Integrity:** `Policy.java` uses `BigDecimal` for premium calculations and `LocalDate` for all dates, preventing currency rounding discrepancies and time zone date shifting.
- ✅ **Validation:** Cross-field date check (BR-04) and uniqueness check (BR-01) are enforced in `PolicyService.save()` and tested via unit tests.

---

## 5. Next Steps for Student (Week 4 Transition)

1. Ensure JDK 21 bin directory is added to system `PATH` (or use local path: `.\jdk-21.0.12_windows-x64_bin\jdk-21.0.12\bin`).
2. Run the application via `mvn spring-boot:run` or your IDE (IntelliJ IDEA / VS Code).
3. Access `http://localhost:8080/` in the browser to view the interactive dashboard with initial seed data.
4. Access `http://localhost:8080/h2-console` to inspect the in-memory H2 database directly.
5. Commit all Week 3 deliverables to Git:
   ```bash
   git add .
   git commit -m "feat: complete week 3 SRS, architecture, database design and initial application skeleton"
   git push origin main
   ```
