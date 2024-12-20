package com.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.domain.dto.BasicResponseDto;
import com.lms.domain.dto.course.AssignmentDto;
import com.lms.domain.model.course.Course;
import com.lms.domain.model.user.Roles;
import com.lms.domain.model.user.Student;
import com.lms.domain.service.AssignmentService;
import com.lms.domain.service.CourseService;
import com.lms.domain.service.NotificationService;
import com.lms.domain.service.UserService;
import com.lms.domain.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssignmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AssignmentService assignmentService;

    @Mock
    private UserService userService;

    @Mock
    private CourseService courseService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private AssignmentController assignmentController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(assignmentController).build();
    }


    @Test
    public void testGetAssignmentSuccess() throws Exception {
        Long courseId = 1L;
        Long assignmentId = 1L;
        AssignmentDto assignmentDto = new AssignmentDto(new Date(), "Description", "Title");

        when(userService.getCurrentUserRole()).thenReturn(Roles.ROLE_INSTRUCTOR);
        when(courseService.isInstructing(anyLong(), anyLong())).thenReturn(true);
        when(assignmentService.getAssignment(assignmentId)).thenReturn(assignmentDto);

        mockMvc.perform(get("/courses/{courseId}/assignments/{aId}", courseId, assignmentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.title").value("Title"));
    }
}