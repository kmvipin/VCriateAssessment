package com.example.vcriateassessment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class AuthCredential {

    @Id
    @GeneratedValue
    private Long id;
    private String email;
    private String password;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String role = "ROLE_USER";


    public AuthCredential(String email, String pass, String role){
        this.email = email;
        this.password = pass;
        this.role = role;
    }
}