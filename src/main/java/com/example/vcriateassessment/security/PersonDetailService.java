package com.example.vcriateassessment.security;

import com.example.vcriateassessment.model.AuthCredential;
import com.example.vcriateassessment.repository.AuthCredRepository;
import com.example.vcriateassessment.security.interf.MyUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

@Service
public class PersonDetailService implements UserDetailsService {

    @Autowired
    private AuthCredRepository authCredRepository;

    @Override
    public MyUserDetails loadUserByUsername(String email) {

        Supplier<AuthCredential> authCredentialSupplier = () -> {
            AuthCredential authCredential;
            try {
                authCredential = this.authCredRepository.findByEmail(email);

                if (authCredential == null) {
                    throw new UsernameNotFoundException("Username Not Found");
                }
                return authCredential;
            } catch (Exception e) {
                throw new UsernameNotFoundException(e.getMessage());
            }
        };

        return new MyUserDetails() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                AuthCredential authCredential = authCredentialSupplier.get();
                return Arrays.asList(new SimpleGrantedAuthority(authCredential.getRole()));
            }

            @Override
            public String getPassword() {
                AuthCredential authCredential = authCredentialSupplier.get();
                return authCredential.getPassword();
            }

            @Override
            public String getUsername() {
                AuthCredential authCredential = authCredentialSupplier.get();
                return authCredential.getEmail();
            }

            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }

            @Override
            public String getEmail() {
                AuthCredential authCredential = authCredentialSupplier.get();
                return authCredential.getEmail();
            }

            @Override
            public String getRole() {
                AuthCredential authCredential = authCredentialSupplier.get();
                return authCredential.getRole();
            }
        };
    }
}


