FROM eclipse-temurin:21-jre-jammy

# Create app user for security
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

# Make sure the software on the container is up to date
RUN apt-get update && apt-get -y upgrade && \
    apt-get -y install curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Create app directory
RUN mkdir /app && chown appuser:appgroup /app

# Set the working directory to /app
WORKDIR /app

# Copy the JAR file into the container
# Use a more flexible approach that works with any JAR file
COPY build/libs/*.jar accountservice.jar

# Change ownership of the JAR file
RUN chown appuser:appgroup accountservice.jar

# Define which port number can be mapped to this container
EXPOSE 8081

# Switch to non-root user for security
USER appuser

# Health check for Docker
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8081/account/ || exit 1

# How to run the application
ENTRYPOINT [ "java", "-jar", "accountservice.jar" ]