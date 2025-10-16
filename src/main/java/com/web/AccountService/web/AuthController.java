package com.web.AccountService.web;

import com.web.AccountService.dao.LoginRequest;
import com.web.AccountService.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class AuthController {

    @Autowired
    TokenService tokenService;

    @PostMapping("/token")
    public String token(@RequestBody LoginRequest loginRequest) {
        //we need to verify that this user exists in BBDD

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(), null,
                Collections.emptyList());
        return tokenService.generateToken();
    }

    @GetMapping("/")
    public String health() {
        return "Authorization Server is running.";
    }

}