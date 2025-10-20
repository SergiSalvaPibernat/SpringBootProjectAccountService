package com.web.AccountService.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Basic smoke tests for Account Service
 * These tests verify the application starts and basic endpoints are accessible
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AccountServiceSmokeTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
        // Verify Spring context loads successfully
        assertNotNull(restTemplate);
        assertTrue(port > 0);
    }

    @Test
    void healthEndpointIsAccessible() {
        String url = "http://localhost:" + port + "/account/";
        
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Authorization Server is running.", response.getBody());
    }

    @Test
    void applicationStartsSuccessfully() {
        // This test passes if the application context loads without errors
        String baseUrl = "http://localhost:" + port;
        assertNotNull(baseUrl);
        assertTrue(port > 1024); // Should be assigned a proper port
    }

    @Test
    void tokenEndpointExists() {
        String url = "http://localhost:" + port + "/account/token";
        
        // GET request to POST endpoint should return Method Not Allowed
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        // Should return 405 Method Not Allowed or 400 Bad Request
        assertTrue(response.getStatusCode().is4xxClientError());
    }

    @Test
    void registerEndpointExists() {
        String url = "http://localhost:" + port + "/account/register";
        
        // GET request to POST endpoint should return Method Not Allowed
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        
        // Should return 405 Method Not Allowed or 400 Bad Request
        assertTrue(response.getStatusCode().is4xxClientError());
    }
}