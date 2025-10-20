# CI/CD Pipeline Documentation

## Overview

This repository includes automated CI/CD pipelines that build JAR files and Docker images whenever commits are made.

## Pipeline Workflows

### 1. Main CI/CD Pipeline (`ci-cd.yml`)

**Triggers:**
- Push to `main`, `develop`, or `AcServiceNewURL` branches
- Pull requests to `main` or `develop` branches

**Jobs:**

#### Test Job
- Runs unit tests using Gradle
- Publishes test results
- Uses JDK 21 and caches Gradle dependencies

#### Build Job
- Builds the Spring Boot application
- Generates JAR file (`AccountService-0.0.1-SNAPSHOT.jar`)
- Uploads JAR as artifact (available for 30 days)
- Extracts version information

#### Docker Job
- Downloads the built JAR
- Builds Docker image using the existing Dockerfile
- Optionally pushes to Docker Hub (requires secrets)
- Creates Docker image artifact (available for 7 days)

#### Security Scan Job
- Scans Docker image for vulnerabilities using Trivy
- Uploads results to GitHub Security tab

### 2. Quick Build Pipeline (`quick-build.yml`)

**Triggers:**
- Manual trigger (workflow_dispatch)
- Changes to source code, build.gradle, or Dockerfile

**Purpose:**
- Fast JAR generation during development
- Skips tests and security scans for speed
- Useful for quick iterations

## Setup Instructions

### 1. Docker Hub Integration (Optional)

To enable Docker image pushing to Docker Hub, add these secrets to your repository:

1. Go to your repository settings
2. Navigate to Secrets and Variables → Actions
3. Add the following repository secrets:
   - `DOCKER_USERNAME`: Your Docker Hub username
   - `DOCKER_PASSWORD`: Your Docker Hub password or access token

### 2. Enable Security Scanning

The security scanning is enabled by default and will:
- Scan Docker images for vulnerabilities
- Report findings in the GitHub Security tab
- No additional setup required

## Generated Artifacts

### JAR Files
- **Location**: `build/libs/AccountService-0.0.1-SNAPSHOT.jar`
- **Availability**: 30 days for main pipeline, 5 days for quick build
- **Download**: Go to Actions → Select workflow run → Artifacts section

### Docker Images
- **Local build**: Always generated during pipeline
- **Docker Hub**: Pushed only on main branch (if secrets configured)
- **Artifact**: Compressed image available for download (7 days)

## Local Development

To build locally:

```bash
# Build JAR file
./gradlew bootJar

# Build Docker image
docker build -t accountservice:local .

# Run Docker container
docker run -p 8081:8081 accountservice:local
```

## Pipeline Status

Check the status of your pipelines:
- Go to the "Actions" tab in your GitHub repository
- View running and completed workflows
- Download artifacts from successful builds

## Troubleshooting

### Common Issues

1. **Gradle Permission Denied**
   - The pipeline automatically makes gradlew executable
   - Ensure gradlew file is committed to repository

2. **Docker Build Fails**
   - Check that JAR file is generated correctly
   - Verify Dockerfile references correct JAR name

3. **Tests Failing**
   - Pipeline will stop if tests fail
   - Check test results in the Actions tab

4. **Docker Push Fails**
   - Verify Docker Hub credentials are correctly set
   - Check Docker Hub repository exists and is accessible

### Pipeline Configuration

The pipeline is configured to:
- Use Java 21 (matching your build.gradle)
- Cache Gradle dependencies for faster builds
- Run on Ubuntu latest
- Support multiple branches for development workflow

## Customization

To modify the pipeline:
- Edit `.github/workflows/ci-cd.yml` for main pipeline
- Edit `.github/workflows/quick-build.yml` for development builds
- Adjust branch triggers, add environments, or modify Docker settings as needed