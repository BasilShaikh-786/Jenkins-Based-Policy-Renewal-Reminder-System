# Software Architecture

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## 1. Architecture Style

This application uses a **monolithic layered architecture**. This means all parts of the application live in a single deployable unit (one `.jar` file) and are organised into distinct horizontal layers. Each layer has a single responsibility and communicates only with the layer directly below it.

This is the correct architecture for this academic project. A monolith is simpler to build, test, deploy and explain than a distributed or microservices architecture.

---

## 2. Architecture Layers

The application is divided into six layers, from the user-facing top to the database at the bottom:

### Layer 1 — Presentation Layer (Thymeleaf Templates)
**What it is:** The HTML pages that the user sees in their web browser.  
**Technology:** Thymeleaf template engine + HTML + CSS.  
**Responsibility:** Displaying data and collecting user input through forms. Contains no business logic.  
**Files:** `src/main/resources/templates/*.html`

---

### Layer 2 — Controller Layer
**What it is:** Java classes that receive HTTP requests from the browser, call the service layer and decide which page to show.  
**Technology:** Spring MVC (`@Controller`, `@GetMapping`, `@PostMapping`).  
**Responsibility:** Routing, request handling, calling service methods, passing data to templates, redirecting after form submissions.  
**Rule:** Controllers must not contain business logic. They must not access the database directly. They call a service and pass the result to a template.  
**Package:** `com.policy.renewal.controller`

---

### Layer 3 — Service Layer
**What it is:** Java classes containing all business logic.  
**Technology:** Plain Java classes annotated with `@Service`.  
**Responsibility:** Implementing business rules (status classification algorithm, date validation, duplicate check), orchestrating repository calls, and transforming data before it reaches the controller or template.  
**Rule:** All business logic lives here. The status classification algorithm (BR-03) is implemented only in this layer.  
**Package:** `com.policy.renewal.service`

---

### Layer 4 — Repository Layer
**What it is:** Interfaces that define how to read and write data to the database.  
**Technology:** Spring Data JPA (`JpaRepository`, custom query methods).  
**Responsibility:** Database operations only — find, save, update. No business logic.  
**Package:** `com.policy.renewal.repository`

---

### Layer 5 — Model / Entity Layer
**What it is:** Java classes that represent the data stored in the database.  
**Technology:** JPA entities (`@Entity`, `@Table`, `@Column`).  
**Responsibility:** Defining the structure of stored data, constraints and relationships.  
**Package:** `com.policy.renewal.model`

---

### Layer 6 — Database
**What it is:** The storage system where all policy records are persisted.  
**Technology:** H2 in-memory database (initial version). Spring Boot auto-configures H2 when it is on the classpath.  
**Responsibility:** Storing and retrieving data as instructed by the repository layer.

---

## 3. Request Flow (Example: User Views Policy List)

```
Browser (User)
    ↓  GET /policies
PolicyController.listPolicies()
    ↓  calls
PolicyService.getAllPolicies()
    ↓  calls
PolicyRepository.findAll()
    ↓  returns List<Policy> (raw entities)
PolicyService
    ↓  enriches each Policy with derived status (BR-03)
    ↓  returns enriched list
PolicyController
    ↓  adds list to Model
Thymeleaf renders policies/list.html
    ↓
Browser displays the policy list
```

---

## 4. Package Structure

```
com.policy.renewal
├── PolicyRenewalApplication.java       ← Spring Boot entry point (@SpringBootApplication)
├── controller
│   ├── HomeController.java             ← Dashboard and home page
│   └── PolicyController.java           ← Policy CRUD, search, filter, upcoming renewals
├── service
│   └── PolicyService.java              ← All business logic including status derivation
├── repository
│   └── PolicyRepository.java           ← Spring Data JPA interface
├── model
│   └── Policy.java                     ← JPA entity class
└── config
    └── (future: SecurityConfig, etc.)  ← Application configuration classes
```

---

## 5. Technology Mapping

| Concern | Technology | Why |
|---|---|---|
| Web framework | Spring Boot | Convention over configuration; embedded Tomcat |
| Routing | Spring MVC | Annotation-driven HTTP routing |
| Template engine | Thymeleaf | Server-side rendering; native Spring integration |
| Data access | Spring Data JPA | Eliminates boilerplate DAO code |
| ORM | Hibernate (via JPA) | Translates Java objects to SQL |
| Database (dev) | H2 | No installation; resets cleanly; fast |
| Build | Maven | Standard Java build tool; dependency management |
| Testing | JUnit 5 + Spring Boot Test | Standard testing framework |
| Validation | Spring Boot Validation (Hibernate Validator) | Annotation-driven field and cross-field validation |

---

## 6. Design Decisions and Trade-offs

| Decision | Chosen Approach | Alternative | Reason for Choice |
|---|---|---|---|
| Architecture | Monolith | Microservices | Simpler; appropriate for academic scale |
| Status storage | Store RENEWED only; derive others from dates | Store all statuses | Avoids stale cached status; fewer inconsistencies |
| Template engine | Thymeleaf (server-rendered) | React/Vue (SPA) | Simpler; no JavaScript framework required; better Spring Boot integration |
| Database | H2 (in-memory) | MySQL/PostgreSQL | No installation required; resets between test runs |
| Validation | Server-side (Spring Validation) | Client-side only (HTML5/JS) | More reliable; cannot be bypassed by disabling JavaScript |

---

## 7. Architecture Diagram

See `architecture-diagram.mmd` in this folder for the Mermaid diagram.

---

## 8. Compliance with NFR-02 (Maintainability)

The architecture enforces the following maintainability rules:

- Business logic is **only** in the service layer.
- Database queries are **only** in the repository layer.
- Controllers are **thin** — they receive a request, call a service and return a view.
- Templates are **logic-free** — they display data; they do not compute status.

A developer new to this project can understand any single layer without needing to understand all layers simultaneously.
