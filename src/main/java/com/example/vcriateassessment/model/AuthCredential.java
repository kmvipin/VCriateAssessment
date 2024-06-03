package com.example.vcriateassessment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class AuthCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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