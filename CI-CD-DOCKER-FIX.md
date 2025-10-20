# CI/CD Docker Image Error - Troubleshooting Guide

## Error Description
```
Unable to find image 'accountservice:latest' locally
docker: Error response from daemon: pull access denied for accountservice, repository does not exist or may require 'docker login': denied: requested access to the resource is denied
```

## Root Cause Analysis

The error occurs because:
1. **Image Not Built**: The Docker image `accountservice:latest` was not successfully built in the previous step
2. **Build Context Issue**: The Docker build step may have failed silently 
3. **Image Loading Issue**: The image was built but not properly loaded into the local Docker daemon

## Solutions Applied

### 1. Fixed Docker Build Step
- Added `load: true` to ensure the image is loaded into the local Docker daemon
- Added verification step to confirm the image exists before testing

### 2. Enhanced Error Handling
- Added image existence check before running container
- Improved error messages and logging
- Added proper container cleanup

### 3. Better Testing Sequence
```yaml
- name: Build Docker image
  uses: docker/build-push-action@v5
  with:
    context: .
    push: false
    load: true  # KEY FIX: Ensures image is loaded locally
    tags: |
      accountservice:latest
      accountservice:${{ needs.build.outputs.version }}

- name: Verify Docker image was built
  run: |
    docker images | grep accountservice || exit 1

- name: Test Docker image
  run: |
    # Check if image exists first
    if ! docker images | grep -q accountservice:latest; then
      echo "❌ Docker image not found!"
      exit 1
    fi
    # Then run container...
```

## Alternative Pipeline Options

### Option 1: Use Simple CI Pipeline
The project includes `simple-ci.yml` which uses a more straightforward Docker build approach:
```bash
docker build -t accountservice:latest .
```

### Option 2: Skip Docker Testing (JAR-only)
For environments where Docker testing is problematic, use:
```yaml
- name: Build application
  run: ./gradlew clean test bootJar
```

## Verification Steps

### Local Testing
To test Docker build locally:
```bash
# Build the JAR
./gradlew clean bootJar

# Build Docker image
docker build -t accountservice:latest .

# Test the image
docker run -d --name test -p 8081:8081 accountservice:latest
sleep 10
curl http://localhost:8081/account/
docker stop test && docker rm test
```

### CI/CD Pipeline Testing
1. Check the "Build Docker Image" step logs for any build errors
2. Verify the "Verify Docker image was built" step passes
3. Check that `docker images` shows the accountservice image

## Application Configuration

The application is configured as:
- **Port**: 8081 (matches Dockerfile EXPOSE)
- **Context Path**: `/account`
- **Health Endpoint**: `http://localhost:8081/account/`

## Additional Fixes Applied

1. **Updated build step** to include `load: true`
2. **Added image verification** before testing
3. **Enhanced error messages** for better debugging
4. **Added health check** with correct context path
5. **Improved container cleanup** logic

## Pipeline Workflow

```
1. Test (Unit + Integration tests)
   ↓
2. Build (Create JAR)
   ↓  
3. Build Docker Image (with verification)
   ↓
4. Test Docker Image (with health check)
   ↓
5. Save Docker Image Artifact
```

## Expected Success Output

```
✅ Container is running successfully
✅ Application health check passed
✅ Docker image found successfully
```

This fix ensures reliable Docker image creation and testing in the CI/CD pipeline.