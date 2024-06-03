package com.example.vcriateassessment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
public class AuthCredential {

    @Id
    private String id;
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