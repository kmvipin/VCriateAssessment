package com.example.vcriateassessment.controller;

import com.example.vcriateassessment.model.ApiResponse;
import com.example.vcriateassessment.security.interf.MyUserDetails;
import com.example.vcriateassessment.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/file")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadFile(@RequestParam("file") MultipartFile file, Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken == false) {
            throw new RuntimeException("User Not Exist");
        }
        String email = ((MyUserDetails)((UsernamePasswordAuthenticationToken) principal)
                .getPrincipal()).getEmail();
        boolean success = fileService.uploadFile(email,file);
        if (success){
            ApiResponse apiResponse = new ApiResponse(true,"File Upload Successfully");
            return ResponseEntity.status(200).body(apiResponse);
        } else{
            ApiResponse apiResponse = new ApiResponse(false,"Something Went Wrong");
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
        Resource resource = fileService.downloadFile(email,fileName);
        return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
    }

    @GetMapping("/info")
    public ResponseEntity<List<String>> filesInfo(Principal principal){
        if (principal instanceof UsernamePasswordAuthenticationToken == false) {
            throw new RuntimeException("User Not Exist");
        }
        String email = ((MyUserDetails)((UsernamePasswordAuthenticationToken) principal)
                .getPrincipal()).getEmail();
        List<String> filesName = fileService.getFileInfo(email);
        return ResponseEntity.status(200).body(filesName);
    }

    @DeleteMapping("/delete/{filename}")
    public ResponseEntity<ApiResponse> deleteFile(@PathVariable("filename") String filename, Principal principal){
        if (principal instanceof UsernamePasswordAuthenticationToken == false) {
            throw new RuntimeException("User Not Exist");
        }
        String email = ((MyUserDetails)((UsernamePasswordAuthenticationToken) principal)
                .getPrincipal()).getEmail();
        fileService.deleteFile(email,filename);
        ApiResponse apiResponse = new ApiResponse(true,"File Successfully Deleted");
        return ResponseEntity.ok()
                .body(apiResponse);
    }

}
