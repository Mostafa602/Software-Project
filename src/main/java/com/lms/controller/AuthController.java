package com.lms.controller;


import com.lms.domain.dto.BasicResponseDto;
import com.lms.domain.dto.auth.LoginDto;
import com.lms.domain.dto.auth.RegisterDto;
import com.lms.domain.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;


@RestController
public class AuthController {
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        String token = userService.login(loginDto.getEmail(), loginDto.getPassword());
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        return ResponseEntity.status(HttpStatus.OK).body(
            tokenMap
        );

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        userService.register(registerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new BasicResponseDto(
                        "success",
                        "User registered successfully"
                )
        );

    }

    @GetMapping("/test")
    public ResponseEntity<BasicResponseDto> test(){
        return ResponseEntity.status(HttpStatus.OK).body(
                new BasicResponseDto(
                        "success",
                        "test"
                )
        );
    }
}