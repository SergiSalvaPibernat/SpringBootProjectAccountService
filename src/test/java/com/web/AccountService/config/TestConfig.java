package com.web.AccountService.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.*;

/**
 * Test configuration to mock external service dependencies
 */
@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public RestTemplate mockRestTemplate() {
        RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);
        
        // Mock successful authentication response
        Mockito.when(mockRestTemplate.postForEntity(
                contains("validate"), 
                any(), 
                eq(Boolean.class)))
                .thenReturn(new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK));
        
        // Mock successful registration response  
        Mockito.when(mockRestTemplate.postForEntity(
                contains("validateRegister"), 
                any(), 
                eq(Boolean.class)))
                .thenReturn(new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK));
        
        return mockRestTemplate;
    }
}