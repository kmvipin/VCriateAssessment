package com.example.vcriateassessment.service;

import com.example.vcriateassessment.model.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FileService {

    @Value("${vcriate.file.location}")
    private String path;

    public boolean uploadFile(String email, MultipartFile file){
        Path fileLocation = Paths.get(this.path+ File.separator+email);
        File dir = new File(this.path+File.separator+email);
        if(!dir.exists()){
            dir.mkdir();
        }
        try {
            Path targetLocation = fileLocation.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e){
            return false;
        }
        return true;
    }

    public Resource downloadFile(String email, String filename){
        Path fileLocation = Paths.get(this.path+File.separator+email);
        Path filePath = fileLocation.resolve(filename).normalize();
        Resource resource;
        try {
            resource = new UrlResource(filePath.toUri());
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        return resource;
    }

    public List<String> getFileInfo(String email){
        String filesPath = this.path+File.separator+email;
        Path filesLocation = Paths.get(filesPath);
        List<String> filesName = new ArrayList<>();
        try(Stream<Path> files = Files.list(filesLocation)){
            files.forEach(filePath -> {
                filesName.add(filePath.getFileName().toString());
            });
        }
        catch (Exception e){
            return new ArrayList<>();
        }
        return filesName;
    }

    public boolean deleteFile(String email, String filename){
        Path fileLocation = Paths.get(this.path+File.separator+email);
        try {
            Path filePath = fileLocation.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
        } catch (Exception e) {
            throw new RuntimeException("Something Went Wrong " + e.getMessage());
        }
        return true;
    }
}
