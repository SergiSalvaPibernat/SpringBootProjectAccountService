package com.web.AccountService.web;

import com.web.AccountService.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    TokenService tokenService;

    @PostMapping("/token")
    public String token(Authentication authentication) {
        return tokenService.generateToken(authentication);
    }

    @GetMapping("/")
    public String health() {
        return "Authorization Server is running.";
    }

}