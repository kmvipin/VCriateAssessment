package com.example.vcriateassessment.controller;

import com.example.vcriateassessment.model.ApiResponse;
import com.example.vcriateassessment.security.interf.MyUserDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/file")
public class FileController {

    @Value("${vcriate.file.location}")
    private String path;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadFile(@RequestParam("file") MultipartFile file, Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken == false) {
            throw new RuntimeException("User Not Exist");
        }
        String email = ((MyUserDetails)((UsernamePasswordAuthenticationToken) principal)
                .getPrincipal()).getEmail();

        Path fileLocation = Paths.get(this.path+File.separator+email);
        File dir = new File(path+File.separator+email);
        if(!dir.exists()){
            dir.mkdir();
        }
        try {
            Path targetLocation = fileLocation.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            ApiResponse apiResponse = new ApiResponse(true,"File Upload Successfully");
            return ResponseEntity.status(200).body(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = new ApiResponse(false,e.getMessage());
            return ResponseEntity.status(500).body(apiResponse);
        }
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken == false) {
            throw new RuntimeException("User Not Exist");
        }
        String email = ((MyUserDetails)((UsernamePasswordAuthenticationToken) principal)
                .getPrincipal()).getEmail();

        Path fileLocation = Paths.get(this.path+File.separator+email);
        try {
            Path filePath = fileLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            throw new RuntimeException("File download failed: " + e.getMessage());
        }
    }

    @GetMapping("/info")
    public ResponseEntity<List<String>> filesInfo(Principal principal){
        if (principal instanceof UsernamePasswordAuthenticationToken == false) {
            throw new RuntimeException("User Not Exist");
        }
        String email = ((MyUserDetails)((UsernamePasswordAuthenticationToken) principal)
                .getPrincipal()).getEmail();
        String filesPath = path+File.separator+email;
        Path filesLocation = Paths.get(filesPath);
        List<String> filesName = new ArrayList<>();
        try(Stream<Path> files = Files.list(filesLocation)){
            files.forEach(filePath -> {
                filesName.add(filePath.getFileName().toString());
            });
        }
        catch (Exception e){
            throw new RuntimeException("Path Does Not Exist");
        }
        return ResponseEntity.status(200).body(filesName);
    }
}
