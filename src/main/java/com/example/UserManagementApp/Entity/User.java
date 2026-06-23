package com.example.UserManagementApp.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@Table(name = "users")

public class User implements UserDetails {

    private Long id;
    private String username;
    private String email;
    private String password;
    private boolean isActive;
    private LocalDate createdAt;
    private LocalDate updatedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles;

    @PrePersist
    protected void onCreated(){
        this.createdAt = LocalDate.from(LocalDateTime.now());
        this.updatedAt = LocalDate.from(LocalDateTime.now());
    }

    @PreUpdate
    protected void onUpdatedAt(){

        this.updatedAt = LocalDate.from(LocalDateTime.now());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
