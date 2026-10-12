# Non-Functional Requirements

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## How to Read This Document

Non-functional requirements (NFRs) describe **how** the system must behave, as opposed to **what** the system must do. They cover qualities such as performance, usability, reliability and security.

Each NFR has a unique identifier (e.g., **NFR-01**).

---

## NFR-01 — Usability

| Field | Detail |
|---|---|
| **Requirement ID** | NFR-01 |
| **Category** | Usability |
| **Description** | The application interface must be understandable and navigable by a user with basic computer literacy, without requiring training beyond a brief orientation. |
| **Criteria** | Navigation labels are in plain English. Error messages describe what went wrong and how to fix it. Form labels clearly indicate which fields are required. A user should be able to find, view and update any policy within three page navigations from the home page. |
| **Verification** | Informal usability review during demonstration. |

---

## NFR-02 — Maintainability

| Field | Detail |
|---|---|
| **Requirement ID** | NFR-02 |
| **Category** | Maintainability |
| **Description** | The codebase must follow the layered architecture (Controller → Service → Repository → Entity) and use meaningful names for classes, methods and variables, so that a competent Java developer unfamiliar with the project can understand and modify any module within a reasonable amount of time. |
| **Criteria** | No business logic in controllers. No database queries in service classes (only repository calls). Clear Java naming conventions followed throughout. Maven build file lists all dependencies with explicit version management. |
| **Verification** | Code review during academic evaluation. |

---

## NFR-03 — Reliability

| Field | Detail |
|---|---|
| **Requirement ID** | NFR-03 |
| **Category** | Reliability |
| **Description** | The application must remain available and produce consistent, correct results throughout a demonstration session. |
| **Criteria** | The application does not crash or produce unhandled exceptions during normal use. Invalid user input is caught by validation and does not cause a 500 error page. Navigating to a non-existent policy ID produces a handled error page, not a stack trace. |
| **Verification** | Manual testing of error paths during development. Integration test coverage of controller layer. |

---

## NFR-04 — Security (Academic Scope)

| Field | Detail |
|---|---|
| **Requirement ID** | NFR-04 |
| **Category** | Security |
| **Description** | Within the academic scope, the application must not expose sensitive data carelessly or introduce obvious security vulnerabilities. Production-grade authentication is not required in the initial version. |
| **Criteria** | No real customer data is used at any stage. No passwords, API keys or database credentials are stored in version-controlled source files (these are placed in properties files excluded by `.gitignore`). H2 console access is limited to the development profile. Thymeleaf templates use its built-in output escaping so user input is not rendered as raw HTML. |
| **Verification** | Inspection of `.gitignore`, application properties and Thymeleaf template syntax. |

---

## NFR-05 — Testability

| Field | Detail |
|---|---|
| **Requirement ID** | NFR-05 |
| **Category** | Testability |
| **Description** | The application must be structured so that unit tests can be written for service-layer business logic and integration tests can be written for the controller layer without requiring a running external database or network service. |
| **Criteria** | Service classes do not depend on static methods or hard-coded singletons. Dependencies are injected using Spring's constructor injection or `@Autowired`. H2 in-memory database is used for integration tests. All tests can be run with a single Maven command (`./mvnw test`). |
| **Verification** | Running `./mvnw test` and confirming all tests pass with no external services running. |

---

## NFR-06 — Data Validation

| Field | Detail |
|---|---|
| **Requirement ID** | NFR-06 |
| **Category** | Data Validation |
| **Description** | All data entering the system through a form must be validated on the server side before it is saved, regardless of any client-side checks. |
| **Criteria** | Validation uses Spring Boot Validation annotations (`@NotBlank`, `@NotNull`, `@Min`, `@Email`, custom cross-field validators). Validation errors are surfaced to the user at the field level, not as a generic error page. No invalid data can be saved to the database via the web interface. |
| **Verification** | Test cases TC-FR-13 through TC-FR-15. |

---

## NFR-07 — Performance (Academic Scope)

| Field | Detail |
|---|---|
| **Requirement ID** | NFR-07 |
| **Category** | Performance |
| **Description** | The application must respond within acceptable time limits for a small academic demonstration dataset. No specific load or stress targets are set, because the application is not intended for production use. |
| **Criteria** | With up to 100 synthetic policy records in the H2 database, any page (list, dashboard, search result) should load and render within 5 seconds on the development machine, under normal conditions and without concurrent load. This is a reasonable academic benchmark, not a production SLA. |
| **Verification** | Manual observation during demonstration. |

---

## NFR-08 — Portability and Build Reproducibility

| Field | Detail |
|---|---|
| **Requirement ID** | NFR-08 |
| **Category** | Portability |
| **Description** | Any developer with a compatible Java JDK installed should be able to build and run the application using only the project repository, without installing separate tools, by using the Maven Wrapper included in the repository. |
| **Criteria** | The project includes `mvnw` and `mvnw.cmd` wrapper scripts. The `pom.xml` pins all dependency versions using the Spring Boot parent POM. The application starts with `./mvnw spring-boot:run` or `mvnw.cmd spring-boot:run` on Windows without any additional configuration. |
| **Verification** | Cloning the repository on a machine with only a JDK installed and successfully starting the application with the wrapper. |

---

## NFR-09 — Accessibility (Basic)

| Field | Detail |
|---|---|
| **Requirement ID** | NFR-09 |
| **Category** | Accessibility |
| **Description** | The application should follow basic HTML accessibility conventions so that it is at least minimally usable with a screen reader or keyboard navigation. |
| **Criteria** | All form fields have associated `<label>` elements. All meaningful images (if any) have `alt` attributes. Navigation links are keyboard-focusable. Colour is not the only means of conveying status information — status labels include text. |
| **Verification** | Code review of Thymeleaf templates. |

---

## NFR-10 — Consistency of Status Classification

| Field | Detail |
|---|---|
| **Requirement ID** | NFR-10 |
| **Category** | Data Correctness |
| **Description** | The renewal status classification algorithm must be consistent and predictable. The same policy record must always produce the same displayed status given the same date inputs and stored values. |
| **Criteria** | Status is derived by the service layer using the following priority order: (1) If stored renewal status is RENEWED → display Renewed. (2) Else if expiry date is in the past → display Expired. (3) Else if expiry date is within the alert window → display Expiring Soon. (4) Otherwise → display Active. This algorithm is implemented in one place only (the service layer) and is not duplicated in the controller, repository or template. |
| **Verification** | Unit tests for the status-derivation algorithm with boundary-condition test cases. |

---

## NFR Summary Table

| ID | Category | Priority | Verified By |
|---|---|---|---|
| NFR-01 | Usability | High | Demonstration review |
| NFR-02 | Maintainability | High | Code review |
| NFR-03 | Reliability | High | Integration tests |
| NFR-04 | Security | High | Inspection |
| NFR-05 | Testability | High | `mvnw test` |
| NFR-06 | Data Validation | High | Test cases TC-FR-13–15 |
| NFR-07 | Performance | Medium | Manual observation |
| NFR-08 | Portability | High | Clean-clone build |
| NFR-09 | Accessibility | Low | Template review |
| NFR-10 | Status Consistency | High | Unit tests |
