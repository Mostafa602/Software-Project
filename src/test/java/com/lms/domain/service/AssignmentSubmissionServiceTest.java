package com.lms.domain.service;

import com.lms.domain.dto.course.MaterialTransferDto;
import com.lms.domain.dto.course.SetGradeDto;
import com.lms.domain.execptionhandler.InternalServerException;
import com.lms.domain.execptionhandler.UnauthorizedAccessException;
import com.lms.domain.model.course.Assignment;
import com.lms.domain.model.course.AssignmentSubmission;
import com.lms.domain.model.user.Roles;
import com.lms.domain.model.user.Student;
import com.lms.domain.repository.AssignmentRepository;
import com.lms.domain.repository.AssignmentSubmissionRepository;
import com.lms.domain.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class AssignmentSubmissionServiceTest {

    @Mock
    private AssignmentSubmissionRepository assignmentSubmissionRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private AssignmentRepository assignmentRepository;

    @Mock
    private CourseService courseService;

    @Mock
    private UserService userService;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private AssignmentSubmissionService assignmentSubmissionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSubmitAssignment() throws Exception {
        Long studentId = 1L;
        Long assignmentId = 1L;
        Student student = new Student();
        student.setId(studentId);
        Assignment assignment = new Assignment();
        assignment.setId(assignmentId);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.of(assignment));
        when(multipartFile.getOriginalFilename()).thenReturn("test.txt");

        assignmentSubmissionService.SubmitAssignment(studentId, assignmentId, multipartFile);

        ArgumentCaptor<AssignmentSubmission> submissionCaptor = ArgumentCaptor.forClass(AssignmentSubmission.class);
        verify(assignmentSubmissionRepository).save(submissionCaptor.capture());

        AssignmentSubmission savedSubmission = submissionCaptor.getValue();
        assertThat(savedSubmission.getStudent()).isEqualTo(student);
        assertThat(savedSubmission.getAssignment()).isEqualTo(assignment);
        assertThat(savedSubmission.getUrl()).contains("test.txt");
    }

    @Test
    public void testSubmitAssignmentStudentNotFound() {
        Long studentId = 1L;
        Long assignmentId = 1L;

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentSubmissionService.SubmitAssignment(studentId, assignmentId, multipartFile))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Student not found with ID: " + studentId);
    }

    @Test
    public void testSubmitAssignmentAssignmentNotFound() {
        Long studentId = 1L;
        Long assignmentId = 1L;
        Student student = new Student();
        student.setId(studentId);

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(assignmentRepository.findById(assignmentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentSubmissionService.SubmitAssignment(studentId, assignmentId, multipartFile))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Assignment not found with ID: " + assignmentId);
    }



    @Test
    public void testGetSubmissionNotFound() {
        Long submissionId = 1L;

        when(assignmentSubmissionRepository.findById(submissionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentSubmissionService.getSubmission(submissionId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Submission not found with ID: " + submissionId);
    }

    @Test
    public void testGradeAssignment() {
        Long submissionId = 1L;
        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setId(submissionId);

        SetGradeDto gradeDto = new SetGradeDto();
        gradeDto.setGrade(90);

        when(assignmentSubmissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));

        assignmentSubmissionService.gradeAssignment(submissionId, gradeDto);

        verify(assignmentSubmissionRepository).save(submission);
        assertThat(submission.getGrade()).isEqualTo(90);
    }

    @Test
    public void testGradeAssignmentNotFound() {
        Long submissionId = 1L;
        SetGradeDto gradeDto = new SetGradeDto();
        gradeDto.setGrade(90);

        when(assignmentSubmissionRepository.findById(submissionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> assignmentSubmissionService.gradeAssignment(submissionId, gradeDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Submission not found with ID: " + submissionId);
    }

    @Test
    public void testGetGrade() {
        Long submissionId = 1L;
        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setId(submissionId);
        submission.setGrade(85);
        Student student = new Student();
        student.setId(1L);
        submission.setStudent(student);

        when(assignmentSubmissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));
        when(userService.getCurrentUserRole()).thenReturn(Roles.ROLE_STUDENT);
        when(userService.getCurrentUserId()).thenReturn(1L);

        SetGradeDto gradeDto = assignmentSubmissionService.getGrade(submissionId);

        assertThat(gradeDto.getGrade()).isEqualTo(85);
    }

    @Test
    public void testGetGradeUnauthorized() {
        Long submissionId = 1L;
        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setId(submissionId);
        submission.setGrade(85);
        Student student = new Student();
        student.setId(1L);
        submission.setStudent(student);

        when(assignmentSubmissionRepository.findById(submissionId)).thenReturn(Optional.of(submission));
        when(userService.getCurrentUserRole()).thenReturn(Roles.ROLE_STUDENT);
        when(userService.getCurrentUserId()).thenReturn(2L);

        assertThatThrownBy(() -> assignmentSubmissionService.getGrade(submissionId))
                .isInstanceOf(UnauthorizedAccessException.class);
    }
}