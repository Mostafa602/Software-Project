package com.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.domain.dto.BasicResponseDto;
import com.lms.domain.dto.course.CourseCreationDto;
import com.lms.domain.dto.course.CourseDto;
import com.lms.domain.dto.course.CourseUpdateDto;
import com.lms.domain.model.user.Roles;
import com.lms.domain.service.CourseService;
import com.lms.domain.service.NotificationService;
import com.lms.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CourseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CourseService courseService;

    @Mock
    private UserService userService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CourseController courseController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(courseController).build();
    }




    @Test
    public void testDeleteCourseSuccess() throws Exception {
        Long courseId = 1L;

        when(userService.getCurrentUserRole()).thenReturn(Roles.ROLE_ADMIN);

        mockMvc.perform(delete("/courses/{courseId}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("course deleted successfully"));

        verify(courseService).deleteCourse(courseId);
    }

}