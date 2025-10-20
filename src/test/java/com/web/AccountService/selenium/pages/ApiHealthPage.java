package com.web.AccountService.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;

/**
 * Page Object for API Health Check endpoint
 * Since this is a REST API, we'll test the responses via browser calls
 */
public class ApiHealthPage {
    
    private final WebDriver driver;
    private final WebDriverWait wait;
    
    public ApiHealthPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }
    
    /**
     * Navigate to health check endpoint
     */
    public void navigateToHealth(String baseUrl) {
        driver.get(baseUrl + "/");
    }
    
    /**
     * Get the response text from the health endpoint
     */
    public String getHealthResponse() {
        try {
            // Wait for page body to load
            WebElement body = wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            return body.getText();
        } catch (Exception e) {
            return "Error loading page: " + e.getMessage();
        }
    }
    
    /**
     * Check if the service is running
     */
    public boolean isServiceRunning() {
        String response = getHealthResponse();
        return response.contains("Authorization Server is running");
    }
    
    /**
     * Get page title
     */
    public String getPageTitle() {
        return driver.getTitle();
    }
}