# Multi-stage build for optimization
FROM openjdk:21-jdk-slim AS builder


# Set working directory
WORKDIR /app


# Copy Gradle wrapper and build files first (for better caching)
COPY gradle/ gradle/
COPY gradlew gradlew.bat build.gradle settings.gradle ./


# Make gradlew executable
RUN chmod +x gradlew


# Copy source code
COPY src/ src/


# Build the application (skip tests for faster build)
RUN ./gradlew bootJar -x test --no-daemon


# Runtime stage
FROM openjdk:21-jre-slim


# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*


# Create non-root user for security
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring


# Set working directory
WORKDIR /app


# Copy the built JAR from builder stage
COPY --from=builder --chown=spring:spring /app/build/libs/*.jar app.jar


# Expose the port the app runs on
EXPOSE 8080


# Add health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/api/ || exit 1


# JVM optimization for containers
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"


# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]