package com.example.vcriateassessment.controller;

import com.example.vcriateassessment.model.AuthCredential;
import com.example.vcriateassessment.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicController {
    private final LoginService loginService;

    @Autowired
    public PublicController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<String> login(@RequestBody AuthCredential authCredential) {
        try {
            Authentication authentication = loginService.authenticateUser(authCredential);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return ResponseEntity.ok("Authentication Successful");
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Authentication Failed: " + e.getMessage());
        }
    }
}
