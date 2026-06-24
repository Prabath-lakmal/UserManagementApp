package com.example.UserManagementApp.Service;

import com.example.UserManagementApp.DTO.LoginRequestDTO;
import com.example.UserManagementApp.DTO.LoginResponseDTO;
import com.example.UserManagementApp.DTO.RegisterRequestDTO;
import com.example.UserManagementApp.DTO.UserDTO;
import com.example.UserManagementApp.Entity.User;
import com.example.UserManagementApp.JWT.JwtService;
import com.example.UserManagementApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

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

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO){

        User user = userRepository.findByUsername(loginRequestDTO.getUsername())
                .orElseThrow(()-> new RuntimeException("User Not found"));

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequestDTO.getUsername(),
                loginRequestDTO.getPassword()
        ));

        String jwtToken = jwtService.generateToken(user); //Autowired JwtService

        return LoginResponseDTO.builder()
                .jwtToken(jwtToken)
                .userDTO(convertToUserDTO(user))
                .build();
    }

    public ResponseEntity<String> logout(){
        //create a expired cookie
        ResponseCookie cookie = ResponseCookie.from("JWT", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged out successfully");
    }

    public UserDTO convertToUserDTO(User user){
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());

        return userDTO;
    }
}
