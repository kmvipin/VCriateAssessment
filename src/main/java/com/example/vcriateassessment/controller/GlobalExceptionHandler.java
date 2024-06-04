package com.example.vcriateassessment.controller;

import com.example.vcriateassessment.model.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.NoSuchFileException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiResponse> exceptionHandler(Exception exception){
        ApiResponse response = new ApiResponse(false,exception.getMessage());
        exception.printStackTrace();
        return ResponseEntity.status(500).body(response);
    }

    @ExceptionHandler(NoSuchFileException.class)
    public ResponseEntity<ApiResponse> noSuchFileException(NoSuchFileException exception){
        ApiResponse response = new ApiResponse(false,"No Such File at "+exception.getReason());
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse> validationExceptionHandler(UsernameNotFoundException exception){
        ApiResponse response = new ApiResponse(false,"Username Or Email Not Found");
        return ResponseEntity.status(200).body(response);
    }
}