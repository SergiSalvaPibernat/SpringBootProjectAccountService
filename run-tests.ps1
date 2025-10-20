# Account Service Test Runner
# PowerShell script to run different types of tests

param(
    [Parameter()]
    [ValidateSet("all", "unit", "integration", "selenium", "smoke")]
    [string]$TestType = "all",
    
    [Parameter()]
    [switch]$NoBuild,
    
    [Parameter()]
    [switch]$Verbose
)

Write-Host "Account Service Test Runner" -ForegroundColor Green
Write-Host "=========================" -ForegroundColor Green

# Set location to project directory
$ProjectDir = Split-Path -Parent $MyInvocation.MyCommand.Definition

if (Test-Path "$ProjectDir\gradlew.bat") {
    $GradleCmd = "$ProjectDir\gradlew.bat"
} else {
    $GradleCmd = "gradle"
}

# Build project first unless skipped
if (-not $NoBuild) {
    Write-Host "Building project..." -ForegroundColor Yellow
    & $GradleCmd clean build -x test
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Build failed!"
        exit 1
    }
    Write-Host "Build completed successfully!" -ForegroundColor Green
}

# Set verbose flag
$VerboseFlag = if ($Verbose) { "--info" } else { "" }

switch ($TestType) {
    "smoke" {
        Write-Host "Running smoke tests..." -ForegroundColor Cyan
        & $GradleCmd test $VerboseFlag --tests "com.web.AccountService.integration.AccountServiceSmokeTest"
    }
    
    "unit" {
        Write-Host "Running unit tests..." -ForegroundColor Cyan
        & $GradleCmd test $VerboseFlag --tests "com.web.AccountService.AccountServiceApplicationTests"
    }
    
    "integration" {
        Write-Host "Running integration tests..." -ForegroundColor Cyan
        & $GradleCmd test $VerboseFlag --tests "com.web.AccountService.integration.*"
    }
    
    "selenium" {
        Write-Host "Running Selenium WebDriver tests..." -ForegroundColor Cyan
        Write-Host "Note: Make sure Chrome browser is installed" -ForegroundColor Yellow
        & $GradleCmd test $VerboseFlag --tests "com.web.AccountService.selenium.*"
    }
    
    "all" {
        Write-Host "Running all tests..." -ForegroundColor Cyan
        
        Write-Host "1/4 - Running smoke tests..." -ForegroundColor Yellow
        & $GradleCmd test $VerboseFlag --tests "com.web.AccountService.integration.AccountServiceSmokeTest"
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "2/4 - Running unit tests..." -ForegroundColor Yellow
            & $GradleCmd test $VerboseFlag --tests "com.web.AccountService.AccountServiceApplicationTests"
        }
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "3/4 - Running integration tests..." -ForegroundColor Yellow
            & $GradleCmd test $VerboseFlag --tests "com.web.AccountService.integration.AccountServiceIntegrationTest"
        }
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "4/4 - Running Selenium tests..." -ForegroundColor Yellow
            & $GradleCmd test $VerboseFlag --tests "com.web.AccountService.selenium.*"
        }
    }
}

# Check test results
if ($LASTEXITCODE -eq 0) {
    Write-Host "" 
    Write-Host "All tests completed successfully! ✅" -ForegroundColor Green
    Write-Host ""
    Write-Host "Test reports available at:" -ForegroundColor Cyan
    Write-Host "- HTML Report: build/reports/tests/test/index.html" -ForegroundColor White
    Write-Host "- JUnit XML: build/test-results/test/" -ForegroundColor White
} else {
    Write-Host ""
    Write-Host "Some tests failed! ❌" -ForegroundColor Red
    Write-Host "Check the test reports for details:" -ForegroundColor Yellow
    Write-Host "- HTML Report: build/reports/tests/test/index.html" -ForegroundColor White
    exit 1
}

Write-Host ""
Write-Host "Usage examples:" -ForegroundColor Cyan
Write-Host "  .\run-tests.ps1                    # Run all tests" -ForegroundColor White
Write-Host "  .\run-tests.ps1 -TestType smoke    # Run only smoke tests" -ForegroundColor White
Write-Host "  .\run-tests.ps1 -TestType selenium # Run only Selenium tests" -ForegroundColor White
Write-Host "  .\run-tests.ps1 -NoBuild           # Skip build step" -ForegroundColor White
Write-Host "  .\run-tests.ps1 -Verbose           # Enable verbose output" -ForegroundColor White