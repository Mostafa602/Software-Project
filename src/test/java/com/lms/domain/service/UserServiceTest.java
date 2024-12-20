package com.lms.domain.service;

import com.lms.config.security.JwtService;
import com.lms.domain.dto.auth.RegisterDto;
import com.lms.domain.dto.user.UserDto;
import com.lms.domain.execptionhandler.ConflictException;
import com.lms.domain.execptionhandler.MissingFieldsException;
import com.lms.domain.model.user.*;
import com.lms.domain.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLoginSuccess() {
        String email = "test@example.com";
        String password = "password";
        User user = new User();
        user.setEmail(email);
        user.setPassword("encodedPassword");
        user.setRole(Roles.ROLE_STUDENT);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user.getId(), user.getRole())).thenReturn("token");

        String token = userService.login(email, password);

        assertThat(token).isEqualTo("Bearer token");
    }

    @Test
    public void testLoginInvalidEmailOrPassword() {
        String email = "test@example.com";
        String password = "password";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(email, password))
                .isInstanceOf(MissingFieldsException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    public void testRegisterStudent() {
        RegisterDto registerDto = new RegisterDto("John", "Doe", "student@example.com", "password", Roles.ROLE_STUDENT);

        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerDto.getPassword())).thenReturn("encodedPassword");

        userService.register(registerDto);

        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(userRepository).save(studentCaptor.capture());

        Student savedStudent = studentCaptor.getValue();
        assertThat(savedStudent.getEmail()).isEqualTo("student@example.com");
        assertThat(savedStudent.getRole()).isEqualTo(Roles.ROLE_STUDENT);
    }

    @Test
    public void testRegisterEmailAlreadyExists() {
        RegisterDto registerDto = new RegisterDto("John", "Doe", "student@example.com", "password", Roles.ROLE_STUDENT);

        when(userRepository.existsByEmail(registerDto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userService.register(registerDto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email is already registered");
    }

    @Test
    public void testGetUserById() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto userDto = userService.getUserById(userId);

        assertThat(userDto.getId()).isEqualTo(userId);
        assertThat(userDto.getFirstName()).isEqualTo("John");
        assertThat(userDto.getLastName()).isEqualTo("Doe");
        assertThat(userDto.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    public void testGetUserByIdNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with id = " + userId);
    }

    @Test
    public void testDeleteUserById() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUserById(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    public void testDeleteUserByIdNotFound() {
        Long userId = 1L;

        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUserById(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found with id = " + userId);
    }
}