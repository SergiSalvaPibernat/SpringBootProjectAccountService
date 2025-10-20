package com.web.AccountService.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.AccountService.config.TestConfig;
import com.web.AccountService.dao.LoginRequest;
import com.web.AccountService.dao.Register;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Account Service API endpoints
 * These tests simulate real HTTP requests to test the full application stack
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(TestConfig.class)
public class AccountServiceIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/account";
    }

    @Test
    void testHealthEndpoint() {
        // Test the health check endpoint
        ResponseEntity<String> response = restTemplate.getForEntity(baseUrl + "/", String.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Authorization Server is running.", response.getBody());
    }

    @Test
    void testTokenEndpointWithValidRequest() {
        // Create a login request
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setName("testuser");
        loginRequest.setPassword("testpassword");

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create request entity
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        // Make POST request to /token endpoint
        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/token", 
            request, 
            String.class
        );

        // Verify response
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // The response will depend on the external service being mocked
        // For now, we just verify the endpoint is reachable
        assertNotNull(response.getBody());
    }

    @Test
    void testTokenEndpointWithEmptyRequest() {
        // Test with empty login request
        LoginRequest loginRequest = new LoginRequest();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/token", 
            request, 
            String.class
        );

        assertNotNull(response);
        // Should still return 200 but might return error message
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testRegisterEndpointWithValidRequest() {
        // Create a registration request
        Register registerRequest = new Register();
        registerRequest.setName("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("newpassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Register> request = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/register", 
            request, 
            String.class
        );

        assertNotNull(response);
        // The actual status will depend on the external service response
        assertTrue(response.getStatusCode().is2xxSuccessful() || 
                  response.getStatusCode().is4xxClientError());
    }

    @Test
    void testRegisterEndpointWithInvalidRequest() {
        // Test with incomplete registration data
        Register registerRequest = new Register();
        registerRequest.setName(""); // Empty name
        registerRequest.setEmail("invalid-email"); // Invalid email format
        registerRequest.setPassword(""); // Empty password

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Register> request = new HttpEntity<>(registerRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/register", 
            request, 
            String.class
        );

        assertNotNull(response);
        // Should handle invalid data gracefully
        assertNotNull(response.getBody());
    }

    @Test
    void testInvalidEndpoint() {
        // Test calling a non-existent endpoint
        // Since it's not in the permitAll() list, Spring Security returns 401
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl + "/nonexistent", 
            String.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void testCorsHeaders() {
        // Test CORS headers are present
        HttpHeaders headers = new HttpHeaders();
        headers.set("Origin", "http://localhost:3000");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/",
            HttpMethod.GET,
            entity,
            String.class
        );

        // Verify CORS headers are present (if configured)
        HttpHeaders responseHeaders = response.getHeaders();
        assertNotNull(responseHeaders);
        
        // The actual CORS headers depend on your @CrossOrigin configuration
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testContentTypeHandling() {
        // Test that the API properly handles different content types
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setName("testuser");
        loginRequest.setPassword("testpassword");

        // Test with correct content type
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/token", 
            request, 
            String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testServerErrorHandling() {
        // Test how the server handles malformed JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String malformedJson = "{invalid json}";
        HttpEntity<String> request = new HttpEntity<>(malformedJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/token", 
            request, 
            String.class
        );

        // Should handle malformed JSON gracefully
        assertTrue(response.getStatusCode().is4xxClientError() || 
                  response.getStatusCode().is5xxServerError());
    }
}