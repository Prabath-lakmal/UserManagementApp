package com.example.UserManagementApp.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@Table(name = "users")

public class User {

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
}
