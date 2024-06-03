package com.example.vcriateassessment.repository;

import com.example.vcriateassessment.model.AuthCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthCredentialRepository extends JpaRepository<AuthCredential, Long> {

    boolean existsByEmail(String email);
    AuthCredential findByEmail(String username);
}
