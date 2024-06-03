package com.example.vcriateassessment.model;

import lombok.Data;

@Data
public class ApiResponse {
    private String message;
    private boolean success;

    public ApiResponse(boolean success, String message) {
        this.message = message;
        this.success = success;
    }

    public ApiResponse(){}
}
