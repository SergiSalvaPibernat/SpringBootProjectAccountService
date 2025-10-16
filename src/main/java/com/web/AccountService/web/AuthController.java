package com.web.AccountService.web;

import com.web.AccountService.dao.LoginRequest;
import com.web.AccountService.security.TokenService;
import jdk.javadoc.doclet.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@RestController
public class AuthController {

    @Autowired
    TokenService tokenService;

    @Autowired
    RestTemplate restTemplate;

    @Value("${resource.url}")
    String url;

    @PostMapping("/token")
    public String token(@RequestBody LoginRequest loginRequest) {

        String token = tokenService.generateToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<Boolean> response = restTemplate.postForEntity(
                url,entity, Boolean.class);

        if(Boolean.FALSE.equals(response.getBody()))
            return "Invalid username or password";

        return token;
    }

    @GetMapping("/")
    public String health() {
        return "Authorization Server is running.";
    }

}