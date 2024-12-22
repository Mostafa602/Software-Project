package com.lms.controller;

import com.lms.domain.dto.BasicResponseDto;
import com.lms.domain.dto.course.AssignmentDto;
import com.lms.domain.execptionhandler.MissingFieldsException;
import com.lms.domain.execptionhandler.UnauthorizedAccessException;
import com.lms.domain.model.user.Roles;
import com.lms.domain.service.AssignmentService;
import com.lms.domain.service.CourseService;
import com.lms.domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class AssignmentControllerTest {

    @InjectMocks
    private AssignmentController assignmentController;

    @Mock
    private AssignmentService assignmentService;

    @Mock
    private UserService userService;

    @Mock
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }



    @Test
    void testCreateAssignment_MissingFields() {
        Long courseId = 1L;
        AssignmentDto assignmentDto = new AssignmentDto(new Date(), null, "Title");

        MissingFieldsException exception = assertThrows(MissingFieldsException.class,
                () -> assignmentController.createAssignment(courseId, assignmentDto));

        assertEquals("provide all required fields", exception.getMessage());
        verify(assignmentService, never()).createAssignment(anyLong(), any());
    }

    @Test
    void testCreateAssignment_UnauthorizedAccess() {
        Long courseId = 1L;
        AssignmentDto assignmentDto = new AssignmentDto(new Date(), "Description", "Title");

        when(userService.getCurrentUserRole()).thenReturn(Roles.ROLE_INSTRUCTOR);
        when(courseService.isInstructing(anyLong(), eq(courseId))).thenReturn(false);

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
                () -> assignmentController.createAssignment(courseId, assignmentDto));

        assertNotNull(exception);
        verify(assignmentService, never()).createAssignment(anyLong(), any());
    }



    @Test
    void testUpdateAssignment_UnauthorizedAccess() {
        Long courseId = 1L;
        Long assignmentId = 1L;
        AssignmentDto updatedAssignment = new AssignmentDto(new Date(), "Updated Description", "Updated Title");

        when(userService.getCurrentUserRole()).thenReturn(Roles.ROLE_INSTRUCTOR);
        when(courseService.isInstructing(anyLong(), eq(courseId))).thenReturn(false);

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
                () -> assignmentController.updateAssignment(courseId, assignmentId, updatedAssignment));

        assertNotNull(exception);
        verify(assignmentService, never()).updateAssignment(anyLong(), anyLong(), any());
    }

    @Test
    void testGetAssignment_Success() {
        Long courseId = 1L;
        Long assignmentId = 1L;
        AssignmentDto assignmentDto = new AssignmentDto(new Date(), "Description", "Title");

        when(userService.getCurrentUserRole()).thenReturn(Roles.ROLE_INSTRUCTOR);
        when(courseService.isInstructing(anyLong(), eq(courseId))).thenReturn(true);
        when(assignmentService.getAssignment(eq(assignmentId))).thenReturn(assignmentDto);

        AssignmentDto response = assignmentController.getAssignment(courseId, assignmentId);

        assertNotNull(response);
        assertEquals("Description", response.getDescription());
        assertEquals("Title", response.getTitle());
    }

    @Test
    void testGetAssignment_UnauthorizedAccess() {
        Long courseId = 1L;
        Long assignmentId = 1L;

        when(userService.getCurrentUserRole()).thenReturn(Roles.ROLE_STUDENT);
        when(courseService.isEnrolled(anyLong(), eq(courseId))).thenReturn(false);

        UnauthorizedAccessException exception = assertThrows(UnauthorizedAccessException.class,
                () -> assignmentController.getAssignment(courseId, assignmentId));

        assertNotNull(exception);
        verify(assignmentService, never()).getAssignment(anyLong());
    }
}
