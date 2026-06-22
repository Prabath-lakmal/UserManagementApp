package com.example.UserManagementApp.DTO;

import lombok.Data;

@Data
public class LoginResponseDTO {

    private String jwtToken;
    private UserDTO userDTO;
}
