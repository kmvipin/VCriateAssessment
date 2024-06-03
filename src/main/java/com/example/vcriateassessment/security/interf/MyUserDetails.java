package com.example.vcriateassessment.security.interf;

import org.springframework.security.core.userdetails.UserDetails;

public interface MyUserDetails extends UserDetails {
    String getEmail();

    String getRole();
}
