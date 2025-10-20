# Selenium Test Runner Script for Account Service

This script helps run Selenium tests for the Account Service project.

## Prerequisites

Before running Selenium tests, ensure you have:

1. **Java 21** installed
2. **Chrome browser** installed (for ChromeDriver)
3. **Gradle** build tool

## Running Tests

### 1. Build the project first
```bash
./gradlew clean build -x test
```

### 2. Run Integration Tests (Recommended)
```bash
./gradlew test --tests "com.web.AccountService.integration.*"
```

### 3. Run Selenium Tests (requires dependencies)
```bash
# Download dependencies first
./gradlew build -x test

# Run Selenium tests
./gradlew test --tests "com.web.AccountService.selenium.*"
```

### 4. Run All Tests
```bash
./gradlew test
```

## Test Types Created

### Integration Tests (Recommended)
- **File**: `AccountServiceIntegrationTest.java`
- **Purpose**: Tests API endpoints using TestRestTemplate
- **Benefits**: Fast, reliable, no browser dependencies
- **Tests**:
  - Health endpoint functionality
  - Token generation endpoint
  - User registration endpoint
  - Error handling
  - CORS headers
  - Content type validation

### Selenium WebDriver Tests
- **File**: `AccountServiceSeleniumTest.java`
- **Purpose**: Browser automation testing
- **Benefits**: End-to-end testing, real browser interaction
- **Tests**:
  - Health endpoint via browser
  - API endpoint accessibility
  - Page load performance
  - Browser navigation
  - Response time consistency

## Test Configuration

### Environment Setup
- Tests run on random port to avoid conflicts
- Uses `test` profile with mock configurations
- Headless browser mode for CI/CD compatibility

### Browser Options
Tests are configured to run with:
- Headless Chrome (no GUI)
- Optimized for CI/CD pipelines
- Automatic WebDriver management

## CI/CD Integration

The tests are integrated into the GitHub Actions pipeline:

1. **Integration tests** run first (fast feedback)
2. **Selenium tests** run if integration tests pass
3. **Test reports** are uploaded as artifacts

## Troubleshooting

### Common Issues

1. **ChromeDriver not found**
   ```bash
   # WebDriverManager will auto-download, but if issues persist:
   # Ensure Chrome browser is installed
   # Check Chrome version compatibility
   ```

2. **Port conflicts**
   ```bash
   # Tests use random ports, but if conflicts occur:
   # Check no other services are running
   # Restart the test with: ./gradlew clean test
   ```

3. **Selenium dependencies not found**
   ```bash
   # Refresh Gradle dependencies:
   ./gradlew --refresh-dependencies build
   ```

### Running Tests with Different Browsers

To use Firefox instead of Chrome:
```bash
./gradlew test -Dbrowser=firefox --tests "com.web.AccountService.selenium.*"
```

### Running Tests in Non-Headless Mode (for debugging)

Modify the test file to remove `--headless` option from ChromeOptions.

## Test Reports

After running tests, find reports at:
- `build/reports/tests/test/index.html` - HTML test report
- `build/test-results/test/` - JUnit XML results

## Performance Benchmarks

Expected performance targets:
- Health endpoint: < 500ms response time
- Token endpoint: < 2s response time (depends on external service)
- Registration endpoint: < 2s response time (depends on external service)

## Security Considerations

Tests include:
- Authentication flow validation
- Input validation testing
- Error message verification
- CORS configuration testing

## Mock External Services

For reliable testing, consider mocking the external `dataservice` dependency:
```java
@MockBean
private RestTemplate restTemplate;
```

This ensures tests don't depend on external services being available.