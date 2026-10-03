package com.example.sms.controller;

import com.example.sms.model.LoginRequest;
import com.example.sms.model.User;
import com.example.sms.service.JwtService;
import com.example.sms.service.UserService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // Register
    @PostMapping("/register")
    public String register(@RequestBody User user) {

        userService.registerUser(user);

        return "User registered successfully";
    }

    // Login
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        User user = userService.findByUsername(request.getUsername());

        if (!userService.passwordMatches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException("Invalid username or password");
        }

        return jwtService.generateToken(user.getUsername());
    }
}