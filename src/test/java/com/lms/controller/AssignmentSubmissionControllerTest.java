package com.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.domain.dto.BasicResponseDto;
import com.lms.domain.dto.course.MaterialTransferDto;
import com.lms.domain.dto.course.SetGradeDto;
import com.lms.domain.model.user.Roles;
import com.lms.domain.service.AssignmentSubmissionService;
import com.lms.domain.service.CourseService;
import com.lms.domain.service.NotificationService;
import com.lms.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssignmentSubmissionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AssignmentSubmissionService assignmentSubmissionService;

    @Mock
    private UserService userService;

    @Mock
    private CourseService courseService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AssignmentSubmissionController assignmentSubmissionController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(assignmentSubmissionController).build();
    }



    @Test
    public void testGetSubmissionSuccess() throws Exception {
        Long submissionId = 1L;
        MaterialTransferDto materialTransferDto = new MaterialTransferDto(new ByteArrayResource("Test content".getBytes()), "text/plain", "test.txt");

        when(assignmentSubmissionService.getSubmission(submissionId)).thenReturn(materialTransferDto);

        mockMvc.perform(get("/courses/{courseId}/assignments/{aId}/submission/{subId}", 1L, 1L, submissionId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.txt\""))
                .andExpect(content().contentType("text/plain"));
    }


    @Test
    public void testGetGradeSuccess() throws Exception {
        Long submissionId = 1L;
        Long courseId = 1L;
        SetGradeDto gradeDto = new SetGradeDto();
        gradeDto.setGrade(85);

        when(userService.getCurrentUserRole()).thenReturn(Roles.ROLE_INSTRUCTOR);
        when(courseService.isInstructing(anyLong(), anyLong())).thenReturn(true);
        when(assignmentSubmissionService.getGrade(submissionId)).thenReturn(gradeDto);

        mockMvc.perform(get("/courses/{courseId}/assignments/{aId}/submission/{subId}/grade", courseId, 1L, submissionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grade").value(85));
    }
}