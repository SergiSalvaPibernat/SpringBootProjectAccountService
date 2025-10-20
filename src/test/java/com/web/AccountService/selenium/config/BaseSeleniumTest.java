package com.web.AccountService.selenium.config;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

/**
 * Base class for Selenium integration tests
 * Provides common WebDriver setup and configuration
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseSeleniumTest {

    protected WebDriver driver;
    
    @LocalServerPort
    protected int port;
    
    protected String baseUrl;
    
    // Browser type for testing (can be changed)
    private static final String BROWSER = System.getProperty("browser", "chrome");
    
    @BeforeAll
    static void setupWebDriverManager() {
        // Setup WebDriverManager for automatic driver management
        switch (BROWSER.toLowerCase()) {
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                break;
            case "chrome":
            default:
                WebDriverManager.chromedriver().setup();
                break;
        }
    }
    
    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/account";
        
        // Configure and initialize WebDriver
        switch (BROWSER.toLowerCase()) {
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--headless"); // Run in headless mode for CI/CD
                firefoxOptions.addArguments("--no-sandbox");
                firefoxOptions.addArguments("--disable-dev-shm-usage");
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case "chrome":
            default:
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless"); // Run in headless mode for CI/CD
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--window-size=1920,1080");
                driver = new ChromeDriver(chromeOptions);
                break;
        }
        
        // Set implicit wait
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        
        // Maximize window
        driver.manage().window().maximize();
    }
    
    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    /**
     * Get the full URL for an endpoint
     */
    protected String getFullUrl(String endpoint) {
        return baseUrl + endpoint;
    }
    
    /**
     * Wait for a short period (useful for dynamic content)
     */
    protected void waitForSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}