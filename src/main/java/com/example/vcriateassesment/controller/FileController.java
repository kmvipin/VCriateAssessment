package com.example.vcriateassesment.controller;

import com.example.vcriateassesment.model.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
public class FileController {

    @Value("${vcriate.file.location}")
    private String path;

    private final Path fileLocation = Paths.get(path);

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Path targetLocation = this.fileLocation.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            ApiResponse apiResponse = new ApiResponse(true,"File Upload Successfully");
            return ResponseEntity.status(200).body(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse(true,e.getMessage());
            return ResponseEntity.status(500).body(apiResponse);
        }
    }


}
