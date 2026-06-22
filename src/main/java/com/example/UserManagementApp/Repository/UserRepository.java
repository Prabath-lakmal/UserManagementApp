package com.example.UserManagementApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.UserManagementApp.Entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
