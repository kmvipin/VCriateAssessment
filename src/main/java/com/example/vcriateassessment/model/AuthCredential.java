package com.example.vcriateassessment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthCredential {
    @JsonIgnore
    private String email;
    private String password;
    private String role;


    public AuthCredential(String email, String pass, String role){
        this.email = email;
        this.password = pass;
        this.role = role;
    }
}