package com.example.UserManagementApp.Service;

import com.example.UserManagementApp.DTO.ChangePasswordDTO;
import com.example.UserManagementApp.DTO.UserDTO;
import com.example.UserManagementApp.Entity.User;
import com.example.UserManagementApp.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDTO getUserById(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User Not found!"));

        return convertToUserDTO(user);
    }

    public UserDTO getUserByUsername(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("User Not found!"));

        return convertToUserDTO(user);
    }

    public List<UserDTO> getAllUsers(){
        List<User> listOfUser = userRepository.findAll();

        return listOfUser.stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    public UserDTO changePassword(Long id, ChangePasswordDTO changePasswordDTO){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User Not found!"));

        if (!passwordEncoder.matches(user.getPassword(), changePasswordDTO.getCurrentPassword())){
            throw new RuntimeException("Current Password is incorrect!");
        }

        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getConfirmPassword())){
            throw new RuntimeException("New password and confirm password does not matched!");
        }

        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        User savedUser = userRepository.save(user);

        return convertToUserDTO(savedUser);
    }

    public UserDTO updateUser(Long id, UserDTO userDTO){
        User user = userRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found"));

        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());

        User savedUser = userRepository.save(user);

        return convertToUserDTO(savedUser);
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    public UserDTO convertToUserDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setEmail(user.getEmail());

        return userDTO;
    }

}
