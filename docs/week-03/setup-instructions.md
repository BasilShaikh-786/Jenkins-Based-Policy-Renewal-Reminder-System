# Setup Instructions

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C

---

## Step 1 — Install Java JDK 17 or 21

Java is the programming language used by this project. You must install a **JDK** (Java Development Kit), not just a JRE (Java Runtime Environment). The JDK includes the compiler (`javac`) and the runtime.

**Recommended:** Java 21 LTS (Eclipse Temurin distribution — free and open source)

### Download Instructions
1. Open your web browser and go to: https://adoptium.net/temurin/releases/
2. Select:
   - **Version:** 21 (or 17)
   - **Operating System:** Windows
   - **Architecture:** x64
   - **Package Type:** JDK
3. Download the `.msi` installer file.
4. Run the installer. During installation, tick the option **"Set JAVA_HOME variable"** if available.
5. Accept the licence and complete the installation.

### Verify Installation
Open a new PowerShell window (important: must be a NEW window after installation) and run:

```powershell
java -version
javac -version
$env:JAVA_HOME
```

Expected output (example for Java 21):
```
openjdk version "21.0.x" 2024-...
openjdk version "21.0.x" 2024-...
C:\Program Files\Eclipse Adoptium\jdk-21.0.x.x-hotspot
```

If `JAVA_HOME` is not set automatically, follow Step 1b below.

### Step 1b — Set JAVA_HOME Manually (if not set automatically)
1. Search for "Environment Variables" in the Windows Start menu.
2. Click "Edit the system environment variables".
3. Click "Environment Variables".
4. Under "System variables", click "New".
5. Variable name: `JAVA_HOME`
6. Variable value: `C:\Program Files\Eclipse Adoptium\jdk-21.0.x.x-hotspot` (use your actual install path)
7. Click OK.
8. Find the `Path` variable in "System variables", click Edit.
9. Click "New" and add: `%JAVA_HOME%\bin`
10. Click OK on all dialogs.
11. Open a new PowerShell window and run `java -version` to confirm.

---

## Step 2 — Verify Git Installation

Git is already installed (v2.52.0.windows.1 was detected). Confirm it is working:

```powershell
git --version
```

Expected output: `git version 2.52.0.windows.1`

---

## Step 3 — Install an IDE (if not already installed)

An Integrated Development Environment (IDE) makes writing Java code much easier. **IntelliJ IDEA Community Edition** is recommended for Spring Boot projects and is free.

### Download IntelliJ IDEA Community Edition
1. Go to: https://www.jetbrains.com/idea/download/
2. Select: **Community Edition** (free).
3. Download and install.
4. When opening the project, IntelliJ will detect the JDK automatically if JAVA_HOME is set.

**Alternative:** VS Code with the Java Extension Pack (https://code.visualstudio.com/ + Java Extension Pack from the Extensions marketplace).

---

## Step 4 — Use the Maven Wrapper (No Separate Maven Install Required)

The project includes a **Maven Wrapper** (`mvnw.cmd` on Windows). This means you do **not** need to install Maven globally. The wrapper downloads the correct version of Maven automatically the first time it runs.

To use the Maven Wrapper, open PowerShell in the project directory and run:

```powershell
# Check Maven Wrapper works (requires Java to be installed)
.\mvnw --version

# Compile the project
.\mvnw compile

# Run all tests
.\mvnw test

# Start the application
.\mvnw spring-boot:run

# Build a deployable JAR file
.\mvnw package
```

> **Important:** Always use `.\mvnw` (with the leading `.\`) in PowerShell, not just `mvnw`.

---

## Step 5 — Run the Application

After Java is installed and the project is set up:

1. Open PowerShell.
2. Navigate to the project directory (it should already be your working directory).
3. Run:

```powershell
.\mvnw spring-boot:run
```

4. Wait for output similar to:
```
Started PolicyRenewalApplication in 3.2 seconds (process running for 3.5)
```

5. Open your web browser and go to: **http://localhost:8080**
6. You should see the application home page.

---

## Step 6 — Access the H2 Database Console (Development)

The H2 in-memory database has a built-in web console for inspecting data during development.

1. While the application is running, open your browser and go to: **http://localhost:8080/h2-console**
2. Use these connection settings:
   - **JDBC URL:** `jdbc:h2:mem:policydb`
   - **Username:** `sa`
   - **Password:** (leave blank)
3. Click "Connect".
4. You can now run SQL queries against the H2 database.

---

## Step 7 — Stop the Application

To stop the running application, press `Ctrl + C` in the PowerShell window where it is running.

---

## Step 8 — Verify Everything Works (Checklist)

After completing Steps 1–6, run this checklist:

| Check | Command | Expected Result |
|---|---|---|
| Java installed | `java -version` | Prints Java 17 or 21 version |
| Compiler installed | `javac -version` | Prints javac 17 or 21 version |
| JAVA_HOME set | `$env:JAVA_HOME` | Prints JDK installation path |
| Git works | `git --version` | Prints git version |
| Maven Wrapper works | `.\mvnw --version` | Prints Maven 3.x and Java version |
| Tests pass | `.\mvnw test` | Prints "BUILD SUCCESS" |
| App starts | `.\mvnw spring-boot:run` | "Started PolicyRenewalApplication" message |
| Browser access | Open http://localhost:8080 | Home page loads |

---

## Troubleshooting

| Problem | Likely Cause | Solution |
|---|---|---|
| `java` not recognised | JDK not installed or PATH not set | Install JDK; open new PowerShell window after setting PATH |
| `.\mvnw` not recognised | Not in project directory | Use PowerShell and ensure you are in the project root folder |
| Port 8080 already in use | Another application is using port 8080 | Run `netstat -ano \| findstr :8080`, stop the blocking process, or change port in `application.properties` |
| H2 console not accessible | Application not running, or H2 console disabled | Confirm the app is running; confirm `spring.h2.console.enabled=true` in `application.properties` |
| Tests fail | Compilation error or configuration problem | Read the error in PowerShell; check that all source files are saved and that Java version is correct |
