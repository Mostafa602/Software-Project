package com.lms.domain.service;

import com.lms.config.security.JwtService;
import com.lms.domain.dto.BasicResponseDto;
import com.lms.domain.dto.auth.RegisterDto;
import com.lms.domain.dto.user.UserDto;
import com.lms.domain.execptionhandler.ConflictException;
import com.lms.domain.model.user.*;
import com.lms.domain.repository.StudentRepository;
import com.lms.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.lms.domain.execptionhandler.MissingFieldsException;
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
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new MissingFieldsException("Invalid email or password")
        );
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new MissingFieldsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getId(), user.getRole());
        return "Bearer " + token;
    }

    public void register(RegisterDto registerDto) {

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new ConflictException("Email is already registered");
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
            case ROLE_ADMIN -> {
                Admin user = new Admin();
                user.setEmail(registerDto.getEmail());
                user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
                user.setFirstName(registerDto.getFirstName());
                user.setLastName(registerDto.getLastName());
                user.setRole(Roles.ROLE_ADMIN);
                userRepository.save(user);
            }
            default -> {
                throw new MissingFieldsException("Invalid role");
            }
        }
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found with id = " + id)
        );
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id) ) {
            throw new EntityNotFoundException("User not found with id = " + id);
        }
        userRepository.deleteById(id);
    }
}
