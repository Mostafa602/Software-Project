package com.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.domain.dto.BasicResponseDto;
import com.lms.domain.dto.user.UserDto;
import com.lms.domain.execptionhandler.UnauthorizedAccessException;
import com.lms.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testGetUserByIdSuccess() throws Exception {
        Long userId = 1L;
        UserDto userDto = new UserDto(userId, "Ahmed", "Hassan", "ahmed.hassan@example.com");

        when(userService.getCurrentUserId()).thenReturn(userId);
        when(userService.getUserById(userId)).thenReturn(userDto);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.firstName").value("Ahmed"))
                .andExpect(jsonPath("$.lastName").value("Hassan"))
                .andExpect(jsonPath("$.email").value("ahmed.hassan@example.com"));
    }


    @Test
    public void testDeleteUserByIdSuccess() throws Exception {
        Long userId = 1L;

        when(userService.getCurrentUserId()).thenReturn(userId);

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("user deleted successfully"));

        verify(userService).deleteUserById(userId);
    }


}