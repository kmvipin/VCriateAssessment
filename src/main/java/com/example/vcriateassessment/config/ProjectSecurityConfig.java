package com.example.vcriateassessment.config;

import com.example.vcriateassessment.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ProjectSecurityConfig{

    @Autowired
    private LoginService loginService;
    public static final String[] PUBLIC_URLS = {
            "/api/public/**"
    };

    @Value("${portfoliobuilder.allowed.origin}")
    private String allowedOrigin;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.
                csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        DefaultSecurityFilterChain defaultSecurityFilterChain = http.build();

        return defaultSecurityFilterChain;
    }

    public AuthenticationManagerBuilder authenticationManagerBuilder() {
        var managerBuilder = new AuthenticationManagerBuilder(null);
        try {
            managerBuilder.inMemoryAuthentication()
                    .withUser("admin").password("admin123").roles("ROLE_ADMIN");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return managerBuilder;
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authentication -> authenticationManagerBuilder().getOrBuild().authenticate(authentication);
    }

}