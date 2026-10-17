# Jenkins-Based Policy Renewal Reminder System

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.2.x-green.svg)](https://spring.io/projects/spring-security)
[![Build](https://img.shields.io/badge/Build-Maven-blue.svg)](https://maven.apache.org/)
[![Database](https://img.shields.io/badge/Database-H2%20Persistent%20File-blue.svg)](https://www.h2database.com/)
[![CI/CD](https://img.shields.io/badge/DevOps-Jenkins-red.svg)](https://www.jenkins.io/)
[![Tests](https://img.shields.io/badge/Tests-172%20Passing-brightgreen.svg)](https://junit.org/junit5/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

Final-Year Engineering Capstone Project  
**Student:** Basil Shaikh  
**Roll Number:** 23102C0026  
**Branch:** Computer Engineering, CMPN C  

---

## 📖 Project Overview

The **Jenkins-Based Policy Renewal Reminder System** is an enterprise-grade insurance policy lifecycle management and automated reminder dispatch platform. It addresses the critical industry challenge of policy lapses and revenue loss by coupling a robust Spring Boot 3 web application with an automated DevOps CI/CD pipeline powered by Jenkins.

### Key Capabilities
- **Docker Containerization & Deployment Automation (Milestone 6):** Multi-stage production Dockerfile packaging Java 21 runtime with a secure non-root user (`appuser:1001`), persistent host volume mounting (`./data:/app/data`), automated container health checks (`/health`), and Docker Compose orchestration.
- **Database Backup & Recovery (Milestone 5):** Live, non-blocking online database backups using native H2 compressed SQL archives. Automated safe recovery verification restores into isolated in-memory test databases with zero risk to production files.
- **Centralized System Audit Trail (Milestone 5):** Immutable database audit log (`AUDIT_LOG`) tracking all policy mutations, user registrations, administrative password resets, and backup operations.
- **Automated CI/CD DevOps Endpoints:** Programmatic REST APIs (`/api/backups/create`, `/api/backups/{fileName}/verify`, `/api/reminders/trigger`) protected by HTTP Basic Authentication for Jenkins pipeline execution without CSRF overhead.
- **Role-Based Access Control (Milestone 4):** Comprehensive authentication and authorization separating **ADMIN** and **STAFF** responsibilities.
- **BCrypt Password Security:** All user credentials hashed using standard BCrypt with secure salt generation. Plaintext passwords are never stored in memory or on disk.
- **Safe First-Admin Setup & Recovery:** No hardcoded credentials. Safe one-time CLI admin password recovery (`--recover-admin`) without wiping database tables.
- **CSRF & Session Security:** CSRF tokens required on all mutating web requests (`POST`, `DELETE`). Session fixation protection (`migrateSession`) and complete cookie invalidation on logout.
- **Renewal Dashboard:** Real-time metrics computed directly via database queries: Total Policies, Active Policies, Policies Expiring within 7 Days, Policies Expiring within 30 Days, Expired Policies, and Renewed Policies.
- **Policy Expiry Reminder Engine:** Evaluates active policies approaching expiration across configurable discrete reminder windows (`30`, `7`, and `1` day). Correctly ignores already expired policies, already renewed policies, and safely handles missing expiry dates.
- **Renewal Cycle Duplicate Prevention:** Ensures at most one successful reminder per policy, per window, per renewal cycle.
- **Safe Simulation / Dry-Run Notification Mode:** Reminders execute in safe dry-run mode without requiring SMTP credentials or sending real emails. Every reminder attempt is logged and tracked with `SIMULATED`, `SENT`, or `FAILED` audit status.
- **Automated Scheduling & DevOps Triggers:** Configurable background daily reminder check (`0 0 8 * * ?`) with atomic concurrency locks, running seamlessly without requiring an interactive logged-in user.
- **Persistent Database Storage:** All policy records, reminder records, user accounts, and audit logs survive application restarts in a file-based H2 database.

---

## 🏛️ System Architecture

The application follows a clean **Monolithic Layered Architecture**:

```
┌───────────────────────────────────────────────────────────────────────────┐
│                           Presentation Layer                              │
│  Thymeleaf Templates (HTML5 + Responsive CSS)                             │
│  Controllers: HomeController, PolicyController, ReminderController,       │
│               AuthController, UserController                              │
└─────────────────────────────────────┬─────────────────────────────────────┘
                                      │
┌─────────────────────────────────────▼─────────────────────────────────────┐
│                       Security & Authorization                            │
│  Spring Security Filter Chain: CSRF, Session Fixation, Form/Basic Auth   │
│  CustomUserDetailsService, SecurityConfig (RBAC URL & Method Security)    │
│  SecurityDataInitializer (Zero-hardcoded-secret bootstrap)                │
└─────────────────────────────────────┬─────────────────────────────────────┘
                                      │
┌─────────────────────────────────────▼─────────────────────────────────────┐
│                          Business Service Layer                           │
│  PolicyService (Business rules BR-01 to BR-07, status derivation)        │
│  PolicyReminderService (Window eligibility, cycle deduplication)          │
│  UserService (Account management, BCrypt validation)                      │
│  NotificationService (Dry-run simulation mode abstraction)                │
│  ReminderScheduler (Background automated cron execution)                  │
└─────────────────────────────────────┬─────────────────────────────────────┘
                                      │
┌─────────────────────────────────────▼─────────────────────────────────────┐
│                         Data Persistence Layer                            │
│  Spring Data JPA Repositories: PolicyRepository,                          │
│                                ReminderRecordRepository, UserRepository   │
│  Database: Embedded Persistent H2 (data/policyrenewaldb.mv.db)            │
└───────────────────────────────────────────────────────────────────────────┘
```

---

## 🔒 Milestone 4: Security, Authentication, and RBAC

### 1. Role-Based Access Control (RBAC) Matrix

The system strictly enforces dual-role authorization both at the HTTP filter chain level (`SecurityConfig`) and at the controller method level (`@PreAuthorize`):

| Operation / View | Route | HTTP Method | `ADMIN` | `STAFF` | Anonymous |
|------------------|-------|-------------|:-------:|:-------:|:---------:|
| Public Login Page | `/login` | `GET` | ✅ | ✅ | ✅ |
| Access Denied View | `/access-denied` | `GET` | ✅ | ✅ | ✅ |
| Static Assets (CSS/JS) | `/css/**`, `/images/**` | `GET` | ✅ | ✅ | ✅ |
| Overview Dashboard | `/` | `GET` | ✅ | ✅ | ❌ (Redirects) |
| Policies Directory | `/policies` | `GET` | ✅ | ✅ *(Masked Email)* | ❌ (Redirects) |
| Upcoming Renewals Queue | `/policies/upcoming` | `GET` | ✅ | ✅ *(Masked Email)* | ❌ (Redirects) |
| Mark Policy as Renewed | `/policies/{id}/renew` | `POST` | ✅ | ✅ | ❌ (401/403) |
| Add Policy Form | `/policies/new` | `GET` | ✅ | ❌ (403) | ❌ (Redirects) |
| Submit New Policy | `/policies` | `POST` | ✅ | ❌ (403) | ❌ (401/403) |
| Full Policy Details & Dossier | `/policies/{id}` | `GET` | ✅ | ❌ (403) | ❌ (Redirects) |
| Edit Policy Form | `/policies/{id}/edit` | `GET` | ✅ | ❌ (403) | ❌ (Redirects) |
| Submit Policy Edit | `/policies/{id}` | `POST` | ✅ | ❌ (403) | ❌ (401/403) |
| Delete Policy | `/policies/{id}/delete` | `POST` | ✅ | ❌ (403) | ❌ (401/403) |
| Reminder Audit Logs | `/reminders` | `GET` | ✅ | ❌ (403) | ❌ (Redirects) |
| Web Reminder Trigger | `/reminders/trigger` | `POST` | ✅ | ❌ (403) | ❌ (401/403) |
| CI/CD API Reminder Trigger | `/api/reminders/trigger`| `POST` | ✅ *(Basic Auth)* | ❌ (403) | ❌ (401) |
| User Accounts Directory | `/users` | `GET` | ✅ | ❌ (403) | ❌ (Redirects) |
| Add User Form | `/users/new` | `GET` | ✅ | ❌ (403) | ❌ (Redirects) |
| Submit New User | `/users` | `POST` | ✅ | ❌ (403) | ❌ (401/403) |
| H2 Database Web Console | `/h2-console/**` | Any | ✅ | ❌ (403) | ❌ (401/403) |

> **Privacy Protection:** In the policy tables viewed by `STAFF`, customer email addresses are rendered as `[Restricted - Admin]`, and direct links to policy dossiers are disabled.

---

### 2. Safe First-Admin Setup Procedure

In adherence to security best practices, **no hardcoded default administrative passwords exist in version control**.

When starting the application on a fresh database:

#### Method A: Setting the Admin Password via Environment Variable (Recommended for Production & CI)
Before launching the application, set the `APP_INITIAL_ADMIN_PASSWORD` environment variable:

```powershell
# Windows PowerShell
$env:APP_INITIAL_ADMIN_PASSWORD = "MyCustomSecureAdminPassword@2026"
mvn spring-boot:run
```

```bash
# Linux / macOS
export APP_INITIAL_ADMIN_PASSWORD="MyCustomSecureAdminPassword@2026"
mvn spring-boot:run
```

#### Method B: Automatic Cryptographic Password Generation
If no password environment variable is provided, `SecurityDataInitializer` automatically generates a cryptographically random 16-character one-time password and displays a prominent startup banner in the console logs:

```
================================================================================
[SECURITY NOTICE] NO EXISTING ADMIN ACCOUNT FOUND IN DATABASE
A one-time temporary bootstrap administrator account has been generated:
   Username: admin
   Password: <randomly-generated-password>
Please log in and create permanent user credentials immediately.
To pre-set admin credentials, export APP_INITIAL_ADMIN_PASSWORD before launching.
================================================================================
```

#### Idempotent Protection
Once an administrator account is saved into the persistent H2 database (`APP_USER` table), subsequent application restarts detect the existing admin and **skip initialization entirely**, preserving existing user passwords.

---

### 3. Creating User Accounts & Role Assignment

Administrators can provision new user accounts directly through the UI:

1. Sign in with an account having the **ADMIN** role.
2. Click **👥 Users** in the navigation bar or visit [http://localhost:8081/users](http://localhost:8081/users).
3. Click **➕ Add New User** ([http://localhost:8081/users/new](http://localhost:8081/users/new)).
4. Complete the server-validated registration form:
   - **Username:** 3 to 50 characters (must be unique).
   - **Password:** Minimum 6 characters (automatically hashed with BCrypt).
   - **Full Name:** Required display name (max 100 characters).
   - **Email:** Optional contact address.
   - **Role:** Select `ADMIN` (full privileges) or `STAFF` (operational view and renewals).
5. Click **Create Account**. The user is immediately saved to the database and can log in.

---

### 4. CSRF, Session, and Error Security

- **CSRF Protection:** Thymeleaf automatically injects synchronized CSRF tokens into all HTML forms (`<input type="hidden" name="_csrf" ...>`). POST or DELETE requests without valid tokens are immediately blocked with HTTP `403 Forbidden`.
- **API Exemption:** `/api/**` REST endpoints are exempted from CSRF requirements to support automated stateless HTTP Basic requests from Jenkins pipelines.
- **Session Security:**
  - `sessionFixation().migrateSession()` ensures session IDs are re-generated upon successful authentication, defeating session fixation attacks.
  - Logging out invalidates the HTTP session, clears the `SecurityContext`, and deletes the `JSESSIONID` cookie.
- **Safe Error Handling:** Unauthorized attempts to access administrative pages are routed to `/access-denied`, returning HTTP `403 Forbidden` and displaying a user-friendly error card without leaking system internals or stack traces.

---

### 5. Non-Interactive Background Scheduling

Automated daily reminder execution (`ReminderScheduler`) runs on background worker threads managed by Spring's task scheduler.
- Reminders execute **independently of any active web session** or logged-in user.
- Scheduled checks use the configured timezone (`scheduler.timezone=Asia/Kolkata`).
- Internal concurrency locks (`AtomicBoolean`) prevent overlapping runs.

---

## 💾 Database Configuration & Persistent Storage

### Database Location

```
data/
├── .gitkeep               ← directory placeholder (tracked by Git)
├── policyrenewaldb.mv.db  ← persistent database file (gitignored)
└── policyrenewaldb.trace.db ← H2 trace log (gitignored)
```

### Table Schema Summary

1. **`APP_USER`:** Authenticated user accounts, BCrypt password hashes, assigned role (`ADMIN`, `STAFF`), and account status flags.
2. **`POLICY`:** Insurance policy specifications, policyholder contact details, coverage dates, premium amounts, renewal status, and agent notes.
3. **`REMINDER_RECORD`:** Immutable audit trail of every reminder attempt, logging policy ID, reminder window (`30`, `7`, `1`), cycle expiry date, processed timestamp, and delivery status (`SIMULATED`, `SENT`, `FAILED`).
4. **`AUDIT_LOG`:** Immutable system audit trail logging administrative actions, policy mutations, user management, security events, and database backup verification.

### H2 Web Console Access

The H2 web console is available at [http://localhost:8081/h2-console](http://localhost:8081/h2-console).
- **Access Rule:** Strictly restricted to authenticated users with the **ADMIN** role.
- **JDBC URL:** `jdbc:h2:file:./data/policyrenewaldb`
- **Username:** `sa`
- **Password:** *(leave blank)*

---

## 💾 Milestone 5: Database Backup, Safe Recovery Verification, and System Audit Trail

### 1. Database Backup & Safe Disaster Recovery Architecture

- **Live Compressed Online Backup:** Uses H2's native online `SCRIPT TO '...' COMPRESSION ZIP` command, generating compressed, timestamped SQL archives without locking or taking the live database offline.
- **Dedicated Backup Storage:** Archives are saved to `./data/backups/policyrenewaldb_backup_YYYYMMDD_HHMMSS.zip` with file size and timestamp tracking.
- **Safe, Non-Destructive Recovery Verification:** Restores the backup archive into an **isolated temporary in-memory database** (`jdbc:h2:mem:verify_...;DB_CLOSE_DELAY=0`), executing DDL and querying record counts across all core tables (`POLICY`, `APP_USER`, `REMINDER_RECORD`, `AUDIT_LOG`). **The live production database file is never overwritten, modified, or placed at risk during verification.**
- **Path Traversal Protection:** All file lookup, verification, and deletion routines strictly validate paths against directory traversal attacks.

### 2. Centralized System Audit Trail

The system provides an immutable audit trail (`AUDIT_LOG` table) tracking lifecycle actions across the platform:
- **Logged Actions:** `POLICY_CREATED`, `POLICY_UPDATED`, `POLICY_DELETED`, `POLICY_RENEWED`, `USER_REGISTERED`, `ADMIN_PASSWORD_RESET`, `BACKUP_CREATED`, `BACKUP_VERIFIED`, `BACKUP_DELETED`.
- **Actor Resolution:** Automatically derives the current authenticated user from Spring Security `SecurityContextHolder`. Background jobs or CLI tasks log as `SYSTEM`.
- **Fail-Safe Persistence:** Uses `Propagation.REQUIRES_NEW` with try-catch safeguards, ensuring that audit persistence never compromises core business workflows.
- **Administrative Audit Console:** Accessible at `/audit` for users with the `ADMIN` role, featuring category filter chips (All, Policies, Users, Database, Security).

### 3. Programmatic DevOps CI/CD REST APIs

In addition to the interactive web console (`/backups` and `/audit`), Milestone 5 exposes authenticated REST endpoints for automated DevOps and Jenkins pipeline triggers:
- `POST /api/backups/create`: Creates a live compressed database backup and returns metadata JSON.
- `POST /api/backups/{fileName}/verify`: Runs isolated recovery verification and returns JSON table counts.
- Protected by HTTP Basic Authentication (`ADMIN` role) with CSRF exemption.

---

## 🐳 Milestone 6: Docker Containerization and Deployment Automation

### 1. Production Container Architecture

Milestone 6 packages the application into an enterprise-ready OCI container image using Docker:

- **Multi-Stage Build (`Dockerfile`):**
  - **Stage 1 (Builder):** Uses `maven:3.9.6-eclipse-temurin-21-jammy` to compile source code and package the Spring Boot executable JAR in an isolated environment.
  - **Stage 2 (Runtime):** Uses minimal `eclipse-temurin:21-jre-jammy` (Ubuntu Jammy base with OpenJDK 21 LTS runtime), reducing the final attack surface.
- **Least-Privilege Security (Non-Root User):**
  - Runs strictly as non-root system user `appuser` (UID 1001, GID 1001). Never runs as `root`.
  - Application binaries and storage directories are chowned to `appuser:appgroup`.
- **Persistent Database Volume Mount:**
  - Container declares volume mount point at `/app/data`.
  - Host directory `./data` is mapped directly to `/app/data` (`./data:/app/data`).
  - **Zero Data Loss Guarantee:** Persistent H2 database files (`policyrenewaldb.mv.db`, `policyrenewaldb.trace.db`) and backup archives remain safely stored on the host filesystem. Containers can be stopped, destroyed, or recreated without data loss.
- **Container Health Check Probe:**
  - Automated probe queries the lightweight public endpoint: `GET http://localhost:8081/health`.
  - Verifies both Spring Boot application health and live database connection validity (`dataSource.getConnection().isValid(2)`).
  - Configured with `interval: 30s`, `timeout: 5s`, `retries: 3`, and `start_period: 40s`.
- **Externalized Configuration & Secret Management:**
  - Standardized `.env.example` template provided.
  - All environment settings (`SERVER_PORT`, `SPRING_DATASOURCE_URL`, `REMINDER_SCHEDULE_ENABLED`, `APP_INITIAL_ADMIN_PASSWORD`) are externalized via environment variables.

---

## 📁 Repository Structure

```
├── .dockerignore                       # Build context exclusions (lean, fast container builds)
├── .env.example                        # Template for externalized environment configuration
├── .gitignore                          # Git exclusions (data DB files, backups, .env, target/)
├── Dockerfile                          # Multi-stage production container image definition (Milestone 6)
├── docker-compose.yml                  # Service orchestration with persistent mount (Milestone 6)
├── Jenkinsfile                         # Declarative Jenkins CI/CD pipeline (Milestones 1-6 stages)
├── pom.xml                             # Maven build with Spring Boot 3.2.5 & Spring Security
├── README.md                           # Comprehensive documentation & operations manual
├── data/                               # Persistent H2 storage & backups (gitignored)
│   ├── backups/                        # Compressed database backup archives
│   ├── policyrenewaldb.mv.db           # Persistent live database file
│   └── policyrenewaldb.trace.db        # H2 trace log
├── docs/                               # Capstone engineering project documentation
├── src/
│   ├── main/
│   │   ├── java/com/policy/renewal/
│   │   │   ├── PolicyRenewalApplication.java   # App entry point, Clock bean, @EnableScheduling
│   │   │   ├── controller/                     # Spring MVC controllers
│   │   │   │   ├── AuthController.java         # /login and /access-denied
│   │   │   │   ├── AuditLogController.java     # /audit system audit trail inspection (Milestone 5)
│   │   │   │   ├── BackupController.java       # /backups console & /api/backups CI/CD (Milestone 5)
│   │   │   │   ├── HealthController.java       # /health public container probe (Milestone 6)
│   │   │   │   ├── HomeController.java         # / overview dashboard
│   │   │   │   ├── PolicyController.java       # /policies CRUD and renewals
│   │   │   │   ├── ReminderController.java     # /reminders audit logs and triggers
│   │   │   │   └── UserController.java         # /users account administration
│   │   │   ├── dto/                            # Data Transfer Objects
│   │   │   │   ├── BackupInfo.java             # Backup archive metadata (Milestone 5)
│   │   │   │   ├── BackupVerificationResult.java # Recovery verification report (Milestone 5)
│   │   │   │   └── UserRegistrationDto.java    # User creation form validation backing object
│   │   │   ├── model/                          # JPA entities & Enums
│   │   │   │   ├── AuditLog.java               # AUDIT_LOG entity (Milestone 5)
│   │   │   │   ├── Policy.java
│   │   │   │   ├── ReminderRecord.java
│   │   │   │   ├── Role.java                   # ADMIN, STAFF enum with authorities
│   │   │   │   └── User.java                   # APP_USER entity with BCrypt password
│   │   │   ├── repository/                     # Spring Data JPA repositories
│   │   │   │   ├── AuditLogRepository.java     # Audit log queries (Milestone 5)
│   │   │   │   ├── PolicyRepository.java
│   │   │   │   ├── ReminderRecordRepository.java
│   │   │   │   └── UserRepository.java
│   │   │   ├── scheduler/                      # Background cron scheduler
│   │   │   │   └── ReminderScheduler.java      # Daily 08:00 AM IST reminder job
│   │   │   ├── security/                       # Spring Security configuration
│   │   │   │   ├── AdminRecoveryResult.java    # Admin recovery outcome DTO
│   │   │   │   ├── AdminRecoveryRunner.java    # Non-web CLI runner (--recover-admin)
│   │   │   │   ├── AdminRecoveryService.java   # Safe admin password update service
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   ├── SecurityConfig.java         # Route rules, CSRF, sessions, BCrypt
│   │   │   │   └── SecurityDataInitializer.java# Safe first-admin bootstrap runner
│   │   │   └── service/                        # Business logic & services
│   │   │       ├── AuditLogService.java        # System audit trail service (Milestone 5)
│   │   │       ├── DatabaseBackupService.java  # Safe online backup & recovery (Milestone 5)
│   │   │       ├── PolicyService.java
│   │   │       ├── PolicyReminderService.java
│   │   │       ├── ReminderRunReport.java
│   │   │       ├── UserService.java            # Account persistence and BCrypt encoding
│   │   │       └── notification/               # Simulation notification service
│   │   └── resources/
│   │       ├── application.properties          # Persistent H2 config, port 8081, IST timezone
│   │       ├── data.sql                        # Synthetic seed policy records
│   │       ├── static/css/style.css            # Responsive design system
│   │       └── templates/                      # Thymeleaf HTML views
│   │           ├── audit/list.html             # System audit trail viewer (Milestone 5)
│   │           ├── auth/login.html             # Custom login page
│   │           ├── backups/list.html           # Database backup & recovery console (Milestone 5)
│   │           ├── error/403.html              # Custom Access Denied view
│   │           ├── fragments/layout.html       # Shared navbar with role-aware profile strip
│   │           ├── policies/                   # Policy list, upcoming, form, detail
│   │           ├── reminders/list.html         # Reminder audit trail view
│   │           └── users/                      # User list and registration form
│   └── test/
│       ├── java/com/policy/renewal/            # Automated test suite (172 tests, 100% passing)
│       │   ├── DatabaseConfigurationTest.java
│       │   ├── PolicyRenewalApplicationTests.java
│       │   ├── ReminderIntegrationTest.java
│       │   ├── controller/                     # Controller WebMvcTests
│       │   │   ├── AuditLogControllerTest.java # Audit log controller tests (Milestone 5)
│       │   │   ├── AuthControllerTest.java
│       │   │   ├── BackupControllerTest.java   # Backup controller tests (Milestone 5)
│       │   │   ├── HealthControllerTest.java   # Container health check tests (Milestone 6)
│       │   │   ├── HomeControllerTest.java
│       │   │   ├── PolicyControllerTest.java
│       │   │   ├── ReminderControllerTest.java
│       │   │   └── UserControllerTest.java
│       │   ├── scheduler/                      # Scheduler & cron tests
│       │   │   ├── ReminderSchedulerTest.java
│       │   │   └── SchedulingConfigurationTest.java
│       │   ├── security/                       # Security unit and integration tests
│       │   │   ├── AdminRecoveryIntegrationTest.java
│       │   │   ├── AdminRecoveryServiceTest.java
│       │   │   ├── CustomUserDetailsServiceTest.java
│       │   │   ├── PolicyRenewalApplicationRecoveryCliTest.java
│       │   │   ├── SecurityDataInitializerTest.java
│       │   │   └── SecurityIntegrationTest.java# Full auth, logout, RBAC & CSRF tests
│       │   └── service/                        # Service layer business logic tests
│       │       ├── AuditLogIntegrationTest.java# End-to-end audit trail tests (Milestone 5)
│       │       ├── AuditLogServiceTest.java    # Audit log service unit tests (Milestone 5)
│       │       ├── DatabaseBackupServiceTest.java # Backup & recovery verification tests (Milestone 5)
│       │       ├── PolicyReminderServiceTest.java
│       │       ├── PolicyServiceTest.java
│       │       └── UserServiceTest.java
│       └── resources/
│           └── application.properties          # In-memory H2, test credentials, simulation mode
```

---

## 🚀 Getting Started

### Prerequisites
- **Java JDK 17 or 21 (LTS)**
- **Apache Maven 3.8+**
- **Git**

### Running Locally

1. **Optionally set an administrative password:**
   ```powershell
   $env:APP_INITIAL_ADMIN_PASSWORD = "MyAdminPassword@123"
   ```
2. **Start the application:**
   ```powershell
   mvn spring-boot:run
   ```
3. **Open in browser:** Navigate to [http://localhost:8081](http://localhost:8081).
4. **Sign in:** Enter your configured administrator username and password (or the temporary password printed in the console banner on first boot).

---

## 🐳 Docker Containerization & Deployment (Milestone 6)

The application includes production-grade containerization powered by a multi-stage `Dockerfile` and `docker-compose.yml` specification.

### Container Architecture Highlights
- **Multi-Stage Base Images:** Build stage uses `maven:3.9.6-eclipse-temurin-21-jammy`; runtime stage uses `eclipse-temurin:21-jre-jammy` (Ubuntu Jammy minimal base with OpenJDK 21 LTS).
- **Least-Privilege Security:** Runs under an unprivileged user `appuser:appgroup` (UID/GID `1001`), protecting the host from root container escapes.
- **Persistent Storage:** Declares `/app/data` volume mount. When running via Docker Compose (`./data:/app/data`), the host H2 database (`data/policyrenewaldb.mv.db`) and compressed backup archives (`data/backups/`) are persisted across container restarts and rebuilds. **The existing database is never deleted, reset, or overwritten.**
- **Port Mapping:** Exposes port `8081` mapping directly to host port `8081`.
- **Health Check Probe:** Configured Docker `HEALTHCHECK` with 30s interval querying `GET /health` (`wget -qO- http://localhost:8081/health || exit 1`). The endpoint verifies database responsiveness and returns HTTP 200 with `status: UP` without requiring authentication.
- **Externalized Configuration:** All operational parameters (ports, cron schedules, admin credentials, JVM container memory limits) are externalized via environment variables and `.env.example`.

### Docker Prerequisites
1. **Docker Desktop for Windows** (version 4.x or higher with WSL 2 backend).
2. Verify Docker CLI is accessible:
   ```powershell
   docker --version
   docker compose version
   ```
3. Verify Docker Desktop daemon is running:
   ```powershell
   docker info
   ```

### Windows PowerShell Container Lifecycle Commands

1. **Environment Setup (One-Time):**
   ```powershell
   # Copy sample environment configuration template
   Copy-Item .env.example .env
   ```

2. **Build the Container Image:**
   ```powershell
   # Build using docker compose
   docker compose build

   # Or build directly with docker CLI with custom tag
   docker build -t policy-renewal-system:latest .
   ```

3. **Start the Application Container (Detached Mode):**
   ```powershell
   docker compose up -d
   ```

4. **Inspect Container Status & Health:**
   ```powershell
   # Check container status and health probe state
   docker compose ps

   # View live application logs
   docker logs -f policy-renewal-app

   # Detailed health check inspection
   docker inspect --format "{{json .State.Health}}" policy-renewal-app | ConvertFrom-Json

   # Test health endpoint from PowerShell host
   Invoke-RestMethod -Uri "http://localhost:8081/health"
   ```

5. **Stop the Running Container:**
   ```powershell
   # Gracefully stop container (preserves database on host)
   docker compose stop
   ```

6. **Restart the Container:**
   ```powershell
   docker compose restart
   ```

7. **Teardown & Clean Up Container (Safe — Host Data Preserved):**
   ```powershell
   # Stops and removes container and network; does NOT delete ./data/
   docker compose down
   ```

8. **Rebuild After Code Changes & Relaunch:**
   ```powershell
   docker compose up -d --build
   ```

---

## 🧪 Automated Testing

Run the complete test suite:
```powershell
mvn clean test
```

Package the executable JAR artifact:
```powershell
mvn clean package
```

### Test Suite Summary (172 Tests Passing)
- **Container Health Monitoring (Milestone 6):** Verifies public unauthenticated access to `/health` and `/actuator/health` returning HTTP 200 with live database connectivity verification, and HTTP 503 Service Unavailable when the database is unreachable.
- **Database Backup & Recovery (Milestone 5):** Confirms live online compressed backup creation, safe recovery verification into isolated in-memory test databases, archive cataloging, path traversal protection, and safe archive deletion.
- **System Audit Trail (Milestone 5):** Verifies immutable audit log persistence across policy CRUD, policy renewals, user registrations, admin password resets, and backup operations.
- **Authentication & Logout (Milestone 4):** Verifies successful login, bad credentials rejection, session fixation migration, and cookie deletion.
- **Safe Admin Recovery (Milestone 4):** Validates command-line-only recovery runner (`--recover-admin`), environment variable validation, and non-destructive password updates.
- **Role Permissions (RBAC):** Confirms ADMIN can perform all operations; verifies STAFF is strictly forbidden (HTTP 403) from policy mutations, customer dossiers, reminder logs, triggers, user administration, database backups, and system audit logs.
- **Unauthorized Requests:** Confirms unauthenticated direct browser requests to protected routes redirect to `/login`, and unauthenticated REST calls return HTTP 401 Unauthorized.
- **CSRF Protection:** Verifies mutating POST requests without CSRF tokens return HTTP 403 Forbidden.
- **CI/CD HTTP Basic Auth:** Confirms `/api/reminders/trigger`, `/api/backups/create`, and `/api/backups/{fileName}/verify` authenticate via HTTP Basic headers without requiring CSRF tokens.
- **Zero Real Email Guarantee:** All tests run with `notification.simulation-mode=true` and `reminder.schedule.enabled=false`.

---

## 🛠️ Troubleshooting

### 1. HTTP 403 Forbidden on form submissions
- **Cause:** Missing or mismatched CSRF token.
- **Fix:** If interacting via browser, ensure forms use standard Thymeleaf `th:action` which automatically renders the hidden `_csrf` input field. If using API/curl, call `/api/**` endpoints which are exempted from CSRF.

### 2. HTTP 403 Forbidden when clicking "View" or "Edit" as STAFF
- **Behavior:** This is the expected security behavior. Policy editing, deletion, and full customer dossiers (including reminder history) are strictly restricted to users with the **ADMIN** role.
- **Fix:** Log in as an administrator to perform policy mutations and inspect full customer dossiers.

### 3. Forgot administrator password
- **Fix:** Run the safe one-time admin password recovery mode via command-line:
  ```powershell
  $env:APP_ADMIN_PASSWORD_RESET_PASSWORD = "MyNewAdminPassword@2026"
  java -jar target/policy-renewal-system-1.0.0-SNAPSHOT.jar --recover-admin
  ```

### 4. Automated CI/CD tool receives 401 Unauthorized
- **Cause:** Automated tools calling `/api/**` endpoints must provide valid HTTP Basic credentials for an account with the ADMIN role.
- **Fix:** Provide credentials in the request header:
  ```powershell
  $pair = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes("admin:MyPassword"))
  Invoke-RestMethod -Uri "http://localhost:8081/api/reminders/trigger" -Method POST -Headers @{Authorization = "Basic $pair"}
  ```

### 5. Docker Desktop daemon is not running
- **Error:** `error during connect: ... The system cannot find the file specified.`
- **Fix:** Start the "Docker Desktop" application from the Windows Start menu. Wait for the whale icon in the Windows taskbar system tray to become steady, then retry running `docker compose up -d`.

---

## 📜 Project Metadata
- **Institution / Academic Context:** Final Year Engineering Project
- **Developer:** Basil Shaikh (Roll No: 23102C0026 &bull; CMPN C)
- **Milestone Completed:** Milestone 6 (Docker Containerization and Deployment Automation)
