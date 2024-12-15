package com.lms.domain.service;

import com.lms.config.security.JwtService;
import com.lms.domain.dto.BasicResponseDto;
import com.lms.domain.dto.auth.RegisterDto;
import com.lms.domain.model.user.Instructor;
import com.lms.domain.model.user.Roles;
import com.lms.domain.model.user.Student;
import com.lms.domain.model.user.User;
import com.lms.domain.repository.StudentRepository;
import com.lms.domain.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    UserService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            if (userDetails instanceof User user) {
                return user.getId();
            }
        }
        return null;
    }

    public Roles getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            if (userDetails instanceof User user) {
                return user.getRole();
            }
        }
        return null;
    }

    public String login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("Invalid email or password");
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UsernameNotFoundException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getId(), user.getRole());
        return "Bearer " + token;
    }

    public void register(RegisterDto registerDto) {

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }
        switch (registerDto.getRole()) {
            case ROLE_STUDENT -> {
                Student user = new Student();
                user.setEmail(registerDto.getEmail());
                user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
                user.setFirstName(registerDto.getFirstName());
                user.setLastName(registerDto.getLastName());
                user.setRole(Roles.ROLE_STUDENT);
                userRepository.save(user);

            }
            case ROLE_INSTRUCTOR ->{
                Instructor user = new Instructor();
                user.setEmail(registerDto.getEmail());
                user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
                user.setFirstName(registerDto.getFirstName());
                user.setLastName(registerDto.getLastName());
                user.setRole(Roles.ROLE_INSTRUCTOR);
                userRepository.save(user);

            }
            default -> {
                throw new IllegalArgumentException("Invalid role");

            }
        }
    }
}
