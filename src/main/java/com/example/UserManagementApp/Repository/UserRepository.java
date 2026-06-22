package com.example.UserManagementApp.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.UserManagementApp.Entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

}
