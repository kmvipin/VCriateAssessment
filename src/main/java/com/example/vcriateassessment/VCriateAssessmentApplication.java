package com.example.vcriateassessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.vcriateassessment.repository")
@EntityScan("com.example.vcriateassessment.model")
public class VCriateAssessmentApplication {
	public static void main(String[] args) {
		SpringApplication.run(VCriateAssessmentApplication.class, args);
	}

}
