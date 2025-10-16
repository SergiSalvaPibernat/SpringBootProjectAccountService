package com.web.AccountService.web;

import com.web.AccountService.dao.LoginRequest;
import com.web.AccountService.dao.Register;
import com.web.AccountService.security.TokenService;
import jdk.javadoc.doclet.Reporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    TokenService tokenService;

    @Autowired
    RestTemplate restTemplate;

    //@Value("${resource.url}")
    //String url;
    @Value("${resource.url}")
    String url;

    /*@PostMapping("/token")
    public String token(@RequestBody LoginRequest loginRequest) {
        System.out.println("Username " +loginRequest.getName());
        System.out.println("Password " + loginRequest.getPassword());
        String token = tokenService.generateToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);

        String completeUrl = url + "validate";

        ResponseEntity<Boolean> response = restTemplate.postForEntity(
                completeUrl,entity, Boolean.class);
        System.out.println("Username " +loginRequest.getName());
        System.out.println("Password " + loginRequest.getPassword());

        if(Boolean.FALSE.equals(response.getBody()))
            return "Invalid username or password";

        return token;
    }*/
    @PostMapping("/token")
    public ResponseEntity<String> token(@RequestBody LoginRequest loginRequest) {
        try {
            // First validate user credentials
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<LoginRequest> entity = new HttpEntity<>(loginRequest, headers);
            String completeUrl = url + "validate";

            ResponseEntity<Boolean> response = restTemplate.postForEntity(
                    completeUrl, entity, Boolean.class);

            if(Boolean.TRUE.equals(response.getBody())) {
                // Generate token only if validation succeeds
                String token = tokenService.generateToken();
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
        } catch (Exception e) {
            System.out.println("Error validating credentials: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed");
        }
    }

    /*@PostMapping("/register")
    public String registerToken(@RequestBody Register register) {

        String token = tokenService.generateToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<Register> entity = new HttpEntity<>(register, headers);

        String completeUrl = url + "validateRegister";

        ResponseEntity<Boolean> response = restTemplate.postForEntity(
                completeUrl,entity, Boolean.class);

        if(Boolean.FALSE.equals(response.getBody()))
            return "Email already used";

        return token;
    }*/
    @PostMapping("/register")
    public ResponseEntity<String> registerToken(@RequestBody Register register) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Register> entity = new HttpEntity<>(register, headers);
            String completeUrl = url + "validateRegister";

            ResponseEntity<Boolean> response = restTemplate.postForEntity(
                    completeUrl, entity, Boolean.class);

            if(Boolean.TRUE.equals(response.getBody())) {
                // Generate token only if registration succeeds
                String token = tokenService.generateToken();
                return ResponseEntity.ok(token);
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already used");
            }
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
    }

    @GetMapping("/")
    public String health() {
        return "Authorization Server is running.";
    }

}