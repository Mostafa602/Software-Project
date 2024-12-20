package com.lms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lms.domain.dto.BasicResponseDto;
import com.lms.domain.dto.quiz.QuizCreationDto;
import com.lms.domain.dto.quiz.QuizSubmissionDto;
import com.lms.domain.model.user.Roles;
import com.lms.domain.service.CourseService;
import com.lms.domain.service.NotificationService;
import com.lms.domain.service.QuizService;
import com.lms.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class QuizControllerTest {

    private MockMvc mockMvc;

    @Mock
    private QuizService quizService;

    @Mock
    private UserService userService;

    @Mock
    private CourseService courseService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private QuizController quizController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(quizController).build();
    }


    @Test
    public void testGetQuizSuccess() throws Exception {
        Long courseId = 1L;
        Long quizId = 1L;

        when(userService.getCurrentUserRole()).thenReturn(Roles.ROLE_STUDENT);
        when(courseService.isEnrolled(anyLong(), anyLong())).thenReturn(true);

        mockMvc.perform(get("/courses/{courseId}/quizzes/{qId}", courseId, quizId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(quizService).getQuiz(courseId, userService.getCurrentUserId(), quizId);
    }



    @Test
    public void testGetQuizGradeSuccess() throws Exception {
        Long submissionId = 1L;

        when(userService.getCurrentUserId()).thenReturn(1L);

        mockMvc.perform(get("/courses/{courseId}/quizzes/{qId}/{subId}", 1L, 1L, submissionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(quizService).getGrade(submissionId, 1L);
    }

    @Test
    public void testGetQuizSubmissionsSuccess() throws Exception {
        Long courseId = 1L;
        Long quizId = 1L;

        when(userService.getCurrentUserRole()).thenReturn(Roles.ROLE_INSTRUCTOR);
        when(courseService.isInstructing(anyLong(), anyLong())).thenReturn(true);

        mockMvc.perform(get("/courses/{courseId}/quizzes/{qId}/submissions", courseId, quizId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(quizService).getAllGrades(quizId);
    }
}