package com.example.vcriateassessment.controller;

import com.example.vcriateassessment.model.ApiResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api")
public class FileController {

    @Value("${vcriate.file.location}")
    private String path;

    private Path fileLocation;

    @PostConstruct
    private void init(){
        this.fileLocation = Paths.get(this.path);
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Path targetLocation = this.fileLocation.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            ApiResponse apiResponse = new ApiResponse(true,"File Upload Successfully");
            return ResponseEntity.status(200).body(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse(false,e.getMessage());
            return ResponseEntity.status(500).body(apiResponse);
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = this.fileLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("File download failed: " + e.getMessage());
        }
    }
}
