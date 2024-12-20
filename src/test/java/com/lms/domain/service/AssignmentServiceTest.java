package com.lms.domain.service;

import com.lms.domain.dto.course.AssignmentDto;
import com.lms.domain.model.course.Assignment;
import com.lms.domain.model.course.Course;
import com.lms.domain.repository.AssignmentRepository;
import com.lms.domain.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class AssignmentServiceTest {

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private AssignmentService assignmentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateAssignment() {
        Long courseId = 1L;
        Course course = new Course();
        course.setId(courseId);

        AssignmentDto assignmentDto = new AssignmentDto(new Date(), "Description", "Title");

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        assignmentService.createAssignment(courseId, assignmentDto);

        ArgumentCaptor<Assignment> assignmentCaptor = ArgumentCaptor.forClass(Assignment.class);
        verify(assignmentRepository).save(assignmentCaptor.capture());

        Assignment savedAssignment = assignmentCaptor.getValue();
        assertThat(savedAssignment.getCourse()).isEqualTo(course);
        assertThat(savedAssignment.getDescription()).isEqualTo("Description");
        assertThat(savedAssignment.getTitle()).isEqualTo("Title");
    }

    @Test
    public void testCreateAssignmentCourseNotFound() {
        Long courseId = 1L;
        AssignmentDto assignmentDto = new AssignmentDto(new Date(), "Description", "Title");

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentService.createAssignment(courseId, assignmentDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Course not found with ID: " + courseId);
    }

    @Test
    public void testUpdateAssignment() {
        Long assignmentId = 1L;
        Assignment existingAssignment = new Assignment();
        existingAssignment.setId(assignmentId);

        AssignmentDto updatedAssignmentDto = new AssignmentDto(new Date(), "Updated Description", "Updated Title");

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(existingAssignment));

        assignmentService.updateAssignment(1L, assignmentId, updatedAssignmentDto);

        verify(assignmentRepository).save(existingAssignment);
        assertThat(existingAssignment.getDescription()).isEqualTo("Updated Description");
        assertThat(existingAssignment.getTitle()).isEqualTo("Updated Title");
    }

    @Test
    public void testUpdateAssignmentNotFound() {
        Long assignmentId = 1L;
        AssignmentDto updatedAssignmentDto = new AssignmentDto(new Date(), "Updated Description", "Updated Title");

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentService.updateAssignment(1L, assignmentId, updatedAssignmentDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Assignment not found with ID: " + assignmentId);
    }

    @Test
    public void testGetAssignment() {
        Long assignmentId = 1L;
        Assignment assignment = new Assignment();
        assignment.setId(assignmentId);
        assignment.setTitle("Title");
        assignment.setDescription("Description");
        assignment.setDueDate(new Date());

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));

        AssignmentDto assignmentDto = assignmentService.getAssignment(assignmentId);

        assertThat(assignmentDto.getTitle()).isEqualTo("Title");
        assertThat(assignmentDto.getDescription()).isEqualTo("Description");
        assertThat(assignmentDto.getDueDate()).isEqualTo(assignment.getDueDate());
    }

    @Test
    public void testGetAssignmentNotFound() {
        Long assignmentId = 1L;

        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentService.getAssignment(assignmentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Assignment not found with ID: " + assignmentId);
    }
}