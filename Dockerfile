# Use Eclipse Temurin JDK 11 (official OpenJDK builds)
FROM eclipse-temurin:11-jre-jammy

# Set working directory
WORKDIR /app

# Install curl for health check
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy the JAR file to the container
COPY target/digital-wallet-1.0.0.jar app.jar

# Create a non-root user to run the application
RUN groupadd -r spring && useradd --no-log-init -r -g spring spring
USER spring

# Expose port 8080
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]