package com.lms.domain.service;

import com.lms.domain.dto.course.CourseCreationDto;
import com.lms.domain.dto.course.CourseDto;
import com.lms.domain.dto.course.CourseUpdateDto;
import com.lms.domain.dto.user.StudentDto;
import com.lms.domain.model.course.Course;
import com.lms.domain.model.user.Instructor;
import com.lms.domain.model.user.Student;
import com.lms.domain.projection.CourseProjection;
import com.lms.domain.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class CourseServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private CourseMaterialRepository courseMaterialRepository;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testEnrollStudent() {
        Long courseId = 1L;
        Long studentId = 1L;
        Course course = new Course();
        Student student = new Student();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));

        courseService.enrollStudent(courseId, studentId);

        verify(courseRepository).save(course);
        assertThat(course.getEnrolledStudents()).contains(student);
    }

    @Test
    public void testEnrollStudentCourseNotFound() {
        Long courseId = 1L;
        Long studentId = 1L;

        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.enrollStudent(courseId, studentId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Course not found with ID: " + courseId);
    }

    @Test
    public void testGetAllCourses() {
        CourseProjection projection = mock(CourseProjection.class);
        when(projection.getId()).thenReturn(1L);
        when(projection.getName()).thenReturn("Course 101");
        when(projection.getDescription()).thenReturn("Description");
        when(projection.getInstructorsFullNames()).thenReturn(Set.of("Instructor Name"));

        when(courseRepository.findAllProjectedBy()).thenReturn(List.of(projection));

        List<CourseDto> courses = courseService.getAllCourses();

        assertThat(courses).hasSize(1);
        assertThat(courses.get(0).getName()).isEqualTo("Course 101");
    }



}