# Environment Readiness Report

**Project:** Jenkins-Based Policy Renewal Reminder System  
**Student:** Basil Shaikh | Roll No: 23102C0026 | Branch: CMPN C  
**Date:** October 2026  
**Checked By:** Automated environment inspection (Antigravity IDE)

---

## Detection Method

The following checks were performed automatically on the development machine using PowerShell commands. Only confirmed results are reported. No tool is marked as installed unless its presence was verified.

---

## Environment Summary Table

| Tool | Purpose | Detected Status | Version / Details | Required Action |
|---|---|---|---|---|
| **Operating System** | Development platform | ✅ Detected | Windows 11 (NT 10.0.26200.0) | None |
| **Git** | Version control | ✅ Installed | git version 2.52.0.windows.1 | None — ready to use |
| **Java JDK** | Run and compile Spring Boot | ❌ Not found in PATH | No JDK detected | **MUST INSTALL before coding** |
| **javac** | Java compiler | ❌ Not found in PATH | Not detected | Installed with JDK |
| **Maven (mvn)** | Build tool | ❌ Not found in PATH | Not detected | Use Maven Wrapper instead (see below) |
| **JAVA_HOME** | JDK location environment variable | ❌ Not set | Empty | Must be set after JDK installation |
| **MAVEN_HOME** | Maven location environment variable | ❌ Not set | Empty | Not required if using Maven Wrapper |
| **Workspace** | Project directory | ✅ Accessible | `C:\Jenkins-Based Policy Renewal Reminder System\` — empty at inspection time | None |
| **IDE / Editor** | Development tool | ⚠️ Not auto-detected | Cannot be detected by automated inspection | Manually confirm (see note below) |
| **Docker** | Containerisation (Weeks 11–12) | 📋 Not checked | Not required yet | Install before Week 11 |
| **Jenkins** | CI/CD (Weeks 7–8) | 📋 Not checked | Not required yet | Install before Week 7 |
| **Selenium** | Browser testing (Weeks 9–10) | 📋 Not checked | Not required yet | Configure before Week 9 |

---

## Critical Blocker

> ⚠️ **Java JDK is not installed or not on the system PATH.**

This is the only critical blocker for Week 3. All documentation can be created and reviewed without Java. However, the application **cannot be compiled, tested or run** until a compatible JDK is installed.

**Required JDK version:** Java 17 (LTS) or Java 21 (LTS).  
Spring Boot 3.x requires Java 17 as a minimum. Java 21 is also supported and is the current LTS release.

---

## Port Availability

Port 8080 is the default port for this Spring Boot application. To check if it is in use, run this command manually in PowerShell:

```powershell
netstat -ano | findstr :8080
```

If the command returns output, another application is using port 8080. You can either:
- Stop the other application.
- Change the Spring Boot port by adding `server.port=8081` (or any available port) to `application.properties`.

---

## Manual IDE Detection

To check which IDE is installed, look for these programs in your Windows Start menu or taskbar:
- **IntelliJ IDEA** (recommended for Java/Spring Boot — Community Edition is free)
- **VS Code** (free; requires Java Extension Pack)
- **Eclipse IDE** (free; use the Eclipse IDE for Enterprise Java and Web Developers package)

Run the manually supplied command to verify which is installed:

```powershell
Get-ItemProperty "HKLM:\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\*" | Where-Object { $_.DisplayName -like "*IntelliJ*" -or $_.DisplayName -like "*Eclipse*" -or $_.DisplayName -like "*Visual Studio Code*" } | Select-Object DisplayName, DisplayVersion
```

---

## Verification Commands to Run Manually

After installing the JDK, run each of these commands in a new PowerShell window and record the results:

```powershell
# 1. Check Java version
java -version

# 2. Check Java compiler version
javac -version

# 3. Check JAVA_HOME is set
$env:JAVA_HOME

# 4. After cloning/creating the project, check Maven Wrapper
.\mvnw --version

# 5. Check Git
git --version

# 6. Check available port
netstat -ano | findstr :8080
```

Record the output of each command as evidence for your Week 3 submission.

---

## Summary

| Category | Status |
|---|---|
| OS detected | ✅ Windows 11 |
| Git ready | ✅ v2.52.0 |
| Java / JDK | ❌ Must be installed |
| Maven | ⚠️ Maven Wrapper in project (no global install needed) |
| Port 8080 | ⚠️ Check manually |
| Workspace | ✅ Accessible and writable |
| IDE | ⚠️ Check manually |
