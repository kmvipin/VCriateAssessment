package com.example.vcriateassessment.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class JwtAuthResponse {
    private String role;
    private String userName;
    private boolean success;
    private String message;

    private String token;
}
