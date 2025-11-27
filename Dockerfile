# Stage 1: Build the JAR using Maven
FROM maven:3.9.3-eclipse-temurin-11 AS build

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies first (cached)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

# Stage 2: Run the JAR using a lightweight JRE
FROM eclipse-temurin:11-jre-jammy

WORKDIR /app

# Install curl for health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Create non-root user
RUN groupadd -r spring && useradd --no-log-init -r -g spring spring
USER spring

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
