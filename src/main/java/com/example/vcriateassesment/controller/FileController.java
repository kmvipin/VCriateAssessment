package com.example.vcriateassesment.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
public class FileController {

    @Value("${vcriate.file.location}")
    private String path;


}
