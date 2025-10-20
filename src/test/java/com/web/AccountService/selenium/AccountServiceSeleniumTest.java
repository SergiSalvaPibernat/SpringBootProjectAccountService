package com.web.AccountService.selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

/**
 * Selenium WebDriver tests for Account Service
 * Tests the API endpoints through browser automation
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountServiceSeleniumTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeAll
    static void setUpWebDriver() {
        // Setup Chrome WebDriver with modern approach
        try {
            WebDriverManager.chromedriver().setup();
        } catch (Exception e) {
            System.out.println("WebDriverManager setup failed, falling back to system property");
            // Fallback in case WebDriverManager fails
        }
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Run headless for CI/CD
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-web-security");
        options.addArguments("--allow-running-insecure-content");
        options.addArguments("--window-size=1920,1080");
        
        try {
            driver = new ChromeDriver(options);
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        } catch (Exception e) {
            System.out.println("ChromeDriver initialization failed: " + e.getMessage());
            throw new RuntimeException("Failed to initialize WebDriver", e);
        }
    }

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/account";
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test Health Endpoint via Browser")
    void testHealthEndpointViaBrowser() {
        // Navigate to health endpoint
        driver.get(baseUrl + "/");
        
        // Verify page loads and contains expected content
        String pageSource = driver.getPageSource();
        Assertions.assertTrue(pageSource.contains("Authorization Server is running."), 
            "Health endpoint should return service status message");
        
        // Verify page title (if any)
        String title = driver.getTitle();
        Assertions.assertNotNull(title, "Page should have a title");
        
        System.out.println("Health endpoint response: " + driver.findElement(By.tagName("body")).getText());
    }

    @Test
    @Order(2)
    @DisplayName("Test API Endpoint Accessibility")
    void testApiEndpointAccessibility() {
        // Test that token endpoint is accessible (will show method not allowed for GET)
        driver.get(baseUrl + "/token");
        
        String pageSource = driver.getPageSource();
        // Should show some error or method not allowed since we're using GET instead of POST
        Assertions.assertFalse(pageSource.isEmpty(), "Token endpoint should be accessible");
        
        System.out.println("Token endpoint GET response: " + driver.findElement(By.tagName("body")).getText());
    }

    @Test
    @Order(3)
    @DisplayName("Test Register Endpoint Accessibility")
    void testRegisterEndpointAccessibility() {
        // Test that register endpoint is accessible
        driver.get(baseUrl + "/register");
        
        String pageSource = driver.getPageSource();
        Assertions.assertFalse(pageSource.isEmpty(), "Register endpoint should be accessible");
        
        System.out.println("Register endpoint GET response: " + driver.findElement(By.tagName("body")).getText());
    }

    @Test
    @Order(4)
    @DisplayName("Test Invalid Endpoint Behavior")
    void testInvalidEndpointBehavior() {
        // Navigate to non-existent endpoint
        driver.get(baseUrl + "/nonexistent");
        
        String pageSource = driver.getPageSource();
        
        // For secured endpoints not in permitAll(), Spring Security may return different responses
        // We just verify that we get some response (not empty) and it's handled properly
        boolean hasResponse = pageSource != null && !pageSource.trim().isEmpty();
        
        Assertions.assertTrue(hasResponse, "Invalid endpoint should return some response");
        
        // Get the page title to verify browser handling
        String title = driver.getTitle();
        Assertions.assertNotNull(title, "Page should have a title");
        
        System.out.println("Invalid endpoint response: " + driver.findElement(By.tagName("body")).getText());
    }

    @Test
    @Order(5)
    @DisplayName("Test Page Load Performance")
    void testPageLoadPerformance() {
        long startTime = System.currentTimeMillis();
        
        driver.get(baseUrl + "/");
        
        // Wait for page to fully load
        WebElement body = driver.findElement(By.tagName("body"));
        Assertions.assertNotNull(body, "Page body should load");
        
        long loadTime = System.currentTimeMillis() - startTime;
        
        // Health endpoint should load quickly (under 5 seconds)
        Assertions.assertTrue(loadTime < 5000, 
            "Health endpoint should load within 5 seconds, took: " + loadTime + "ms");
        
        System.out.println("Page load time: " + loadTime + "ms");
    }

    @Test
    @Order(6)
    @DisplayName("Test Browser Navigation and Back Button")
    void testBrowserNavigation() {
        // Navigate to health endpoint
        driver.get(baseUrl + "/");
        String healthResponse = driver.findElement(By.tagName("body")).getText();
        
        // Navigate to another endpoint
        driver.get(baseUrl + "/token");
        String tokenResponse = driver.findElement(By.tagName("body")).getText();
        
        // Use browser back button
        driver.navigate().back();
        String backResponse = driver.findElement(By.tagName("body")).getText();
        
        // Should be back to health endpoint
        Assertions.assertEquals(healthResponse, backResponse, 
            "Back navigation should return to health endpoint");
    }

    @Test
    @Order(7)
    @DisplayName("Test Page Source Content")
    void testPageSourceContent() {
        driver.get(baseUrl + "/");
        
        String pageSource = driver.getPageSource();
        
        // Verify HTML structure
        Assertions.assertTrue(pageSource.contains("<html"), "Should contain HTML tags");
        Assertions.assertTrue(pageSource.contains("<body"), "Should contain body tag");
        
        // Verify content
        Assertions.assertTrue(pageSource.contains("Authorization Server is running."), 
            "Should contain service status message");
    }

    @Test
    @Order(8)
    @DisplayName("Test Multiple Endpoint Visits")
    void testMultipleEndpointVisits() {
        String[] endpoints = {"/", "/token", "/register"};
        
        for (String endpoint : endpoints) {
            driver.get(baseUrl + endpoint);
            
            // Verify page loads without throwing exceptions
            String pageSource = driver.getPageSource();
            Assertions.assertFalse(pageSource.isEmpty(), 
                "Endpoint " + endpoint + " should return content");
            
            // Verify we can get the body text
            WebElement body = driver.findElement(By.tagName("body"));
            Assertions.assertNotNull(body.getText(), 
                "Endpoint " + endpoint + " should have body content");
            
            System.out.println("Endpoint " + endpoint + " response: " + body.getText());
        }
    }

    @Test
    @Order(9)
    @DisplayName("Test JavaScript Console Errors")
    void testJavaScriptConsoleErrors() {
        driver.get(baseUrl + "/");
        
        // Note: In a real application with frontend, you might check for JS errors
        // For this API-only service, we just verify the page loads
        
        String pageSource = driver.getPageSource();
        Assertions.assertFalse(pageSource.isEmpty(), "Page should load without critical errors");
        
        // Check if there's any error indication in the response
        boolean hasError = pageSource.toLowerCase().contains("error") && 
                          !pageSource.contains("Authorization Server is running.");
        
        Assertions.assertFalse(hasError, "Page should not contain unexpected errors");
    }

    @Test
    @Order(10)
    @DisplayName("Test Response Time Consistency")
    void testResponseTimeConsistency() {
        int numberOfRequests = 3;
        long totalTime = 0;
        
        for (int i = 0; i < numberOfRequests; i++) {
            long startTime = System.currentTimeMillis();
            
            driver.get(baseUrl + "/");
            driver.findElement(By.tagName("body")); // Ensure page is loaded
            
            long responseTime = System.currentTimeMillis() - startTime;
            totalTime += responseTime;
            
            System.out.println("Request " + (i + 1) + " response time: " + responseTime + "ms");
        }
        
        long averageTime = totalTime / numberOfRequests;
        
        // Average response time should be reasonable (under 3 seconds)
        Assertions.assertTrue(averageTime < 3000, 
            "Average response time should be under 3 seconds, got: " + averageTime + "ms");
        
        System.out.println("Average response time: " + averageTime + "ms");
    }
}