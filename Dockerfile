# ==============================================================================
# Dockerfile — Jenkins-Based Policy Renewal Reminder System
# Milestone 6: Production-Grade Containerization & Deployment Automation
# Student: Basil Shaikh | Roll No: 23102C0026 | CMPN C
# ==============================================================================

# ------------------------------------------------------------------------------
# Stage 1: Build Application Artifact with Maven & OpenJDK 21
# ------------------------------------------------------------------------------
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder

WORKDIR /build

# Copy Maven POM and dependency descriptors
COPY pom.xml .

# Download dependencies in an isolated layer to leverage Docker build cache
RUN mvn dependency:go-offline -B || true

# Copy application source code
COPY src ./src

# Compile and package executable Spring Boot JAR (skipping unit tests as CI tests them separately)
RUN mvn clean package -DskipTests

# ------------------------------------------------------------------------------
# Stage 2: Minimal, Secure Runtime Environment
# ------------------------------------------------------------------------------
FROM eclipse-temurin:21-jre-jammy AS runtime

LABEL maintainer="Basil Shaikh <23102C0026>" \
      description="Jenkins-Based Policy Renewal Reminder System Production Image" \
      version="1.0.0"

# Install lightweight diagnostic tools (curl, wget) for container health checks
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl wget && \
    rm -rf /var/lib/apt/lists/*

# Create dedicated non-root application user and group (UID/GID 1001)
RUN groupadd -r appgroup -g 1001 && \
    useradd -r -g appgroup -u 1001 -m -d /app appuser

WORKDIR /app

# Create persistent storage directories with appropriate non-root permissions
RUN mkdir -p /app/data /app/data/backups && \
    chown -R appuser:appgroup /app

# Copy packaged JAR from builder stage with correct ownership
COPY --from=builder --chown=appuser:appgroup /build/target/policy-renewal-system-*.jar /app/app.jar

# Externalized environment configuration defaults
ENV SERVER_PORT=8081 \
    SPRING_DATASOURCE_URL=jdbc:h2:file:./data/policyrenewaldb \
    REMINDER_SCHEDULE_ENABLED=false \
    NOTIFICATION_SIMULATION_MODE=true \
    SCHEDULER_TIMEZONE=Asia/Kolkata \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# Declare mount point for persistent H2 database storage
VOLUME ["/app/data"]

# Expose Spring Boot application port
EXPOSE 8081

# Switch to non-root application user
USER appuser

# Container health check using verified public /health endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8081/health || curl -f http://localhost:8081/health || exit 1

# Launch executable Spring Boot application
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
