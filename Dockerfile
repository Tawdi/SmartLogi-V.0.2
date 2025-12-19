# Phase 1: Build phase (JDK + Maven)
FROM maven:3.8.4-openjdk-17-slim AS builder
WORKDIR /app

# Copy Maven wrapper and pom.xml first for dependency caching
COPY pom.xml .
#COPY .mvn/ .mvn/
#COPY mvnw .

# Download dependencies (caches well)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the app (skip tests if desired: -DskipTests)
RUN mvn clean package -Dmaven.test.skip

# Phase 2: Run phase (Slim JRE only)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Recommended container-aware JVM options
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]