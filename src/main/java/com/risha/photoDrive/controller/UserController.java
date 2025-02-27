package com.risha.photoDrive.controller;

import com.risha.photoDrive.config.SecurityConfig;
import com.risha.photoDrive.entity.User;
import com.risha.photoDrive.repository.UserRepository;
import com.risha.photoDrive.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestParam String oldPassword, @RequestParam String newPassword,
                                             @RequestParam String confirmNewPassword) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                if (newPassword.equals(confirmNewPassword)) {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userService.updateUser(user);
                    return new ResponseEntity<>("Password updated!",HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Passwords do not match", HttpStatus.BAD_REQUEST);
                }
            } else {
                return new ResponseEntity<>("Wrong old password", HttpStatus.BAD_REQUEST);
            }

        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/change-username")
    public ResponseEntity<?> changeUsername(@RequestParam String newUsername) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            user.setUsername(newUsername);
            userService.updateUser(user);
            return new ResponseEntity<>("Username updated!", HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/change-email")
    public ResponseEntity<?> changeEmail(@RequestParam String newEmail) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            user.setEmail(newEmail);
            userService.updateUser(user);
            return new ResponseEntity<>("Email updated!", HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete-user")
    public ResponseEntity<?> deleteUser() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            userService.deleteUser(user);
            return new ResponseEntity<>("User deleted!", HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
