package com.lms.domain.service;


import com.lms.domain.dto.course.AssignmentDto;
import com.lms.domain.model.course.Assignment;
import com.lms.domain.model.course.Course;
import com.lms.domain.repository.AssignmentRepository;
import com.lms.domain.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssignmentService {
    @Autowired
    AssignmentRepository assignmentRepository;

    @Autowired
    CourseRepository courseRepository;

    public AssignmentService(AssignmentRepository assignmentRepository, CourseRepository courseRepository) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
    }

    public String createAssignment(Long courseId,AssignmentDto assignment) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
        Assignment newAssignment = new Assignment(course,assignment.getDueDate(),assignment.getDescription(),
                assignment.getTitle());
        assignmentRepository.save(newAssignment);
        return "Assignment is created successfully";
    }

    public void updateAssignment(Long courseId, Long assignmentId, AssignmentDto updatedAssignment) {
        Assignment existingAssignment = assignmentRepository.findById(assignmentId).get();

        if (updatedAssignment.getTitle() != null) {
            existingAssignment.setTitle(updatedAssignment.getTitle());
        }
        if (updatedAssignment.getDescription() != null) {
            existingAssignment.setDescription(updatedAssignment.getDescription());
        }
        if (updatedAssignment.getDueDate() != null) {
            existingAssignment.setDueDate(updatedAssignment.getDueDate());
        }
        assignmentRepository.save(existingAssignment);
    }

    public Assignment getAssignment(Long assignmentId) {
        return assignmentRepository.getReferenceById(assignmentId);
    }


}
