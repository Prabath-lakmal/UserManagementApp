package com.example.UserManagementApp.Service;

import com.example.UserManagementApp.DTO.RegisterRequestDTO;
import com.example.UserManagementApp.DTO.UserDTO;
import com.example.UserManagementApp.Entity.User;
import com.example.UserManagementApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO registerNormalUser(RegisterRequestDTO registerRequestDTO){
        if (userRepository.findByUsername(registerRequestDTO.getUsername()).isPresent()){
            throw new RuntimeException("User is already registered");
        }

        Set<String> set = new HashSet<String>();
        set.add("ROLE_USER");

        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setRoles(set);

        User savedUser = userRepository.save(user);

        return convertToUserDTO(savedUser);
    }

    public UserDTO registerAdminUser(RegisterRequestDTO registerRequestDTO){
        if (userRepository.findByUsername(registerRequestDTO.getUsername()).isPresent()){
            throw new RuntimeException("User is already registered");
        }

        Set<String> set = new HashSet<String>();
        set.add("ROLE_ADMIN");
        set.add("ROLE_USER");

        User user = new User();
        user.setUsername(registerRequestDTO.getUsername());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        user.setRoles(set);

        User savedUser = userRepository.save(user);

        return convertToUserDTO(savedUser);
    }


    public UserDTO convertToUserDTO(User user){
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());

        return userDTO;
    }
}
