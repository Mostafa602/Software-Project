package com.lms.controller;


import com.lms.domain.dto.BasicResponseDto;
import com.lms.domain.dto.course.AssignmentDto;
import com.lms.domain.execptionhandler.MissingFieldsException;
import com.lms.domain.execptionhandler.UnauthorizedAccessException;
import com.lms.domain.model.course.Assignment;
import com.lms.domain.model.user.Roles;
import com.lms.domain.service.AssignmentService;
import com.lms.domain.service.CourseService;
import com.lms.domain.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.EntityResponse;

import java.util.Date;
import java.util.Objects;

@RestController
@RequestMapping("/courses/{courseId}/assignments")
public class AssignmentController {

    AssignmentService assignmentService;
    UserService userService;
    CourseService courseService;

    public AssignmentController(AssignmentService assignmentService, UserService userService, CourseService courseService) {
        this.assignmentService = assignmentService;
        this.userService = userService;
        this.courseService = courseService;
    }

    @PostMapping()
    public ResponseEntity<?> createAssignment(@PathVariable Long courseId, @RequestBody AssignmentDto assignment) {
        if(assignment.getDescription()==null || assignment.getTitle()==null) {
            throw new MissingFieldsException("provide all required fields");
        }
        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
                !courseService.isInstructing(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }

        assignmentService.createAssignment(courseId,assignment);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new BasicResponseDto(
                        "success", "Assignment created successfully"
                )
        );
    }

    @PutMapping("/{aId}")
    public ResponseEntity<?> updateAssignment(@PathVariable Long courseId,
                                                   @PathVariable Long aId,
                                                   @RequestBody AssignmentDto updatedAssignment) {

        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
                !courseService.isInstructing(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }

        assignmentService.updateAssignment(courseId, aId, updatedAssignment);
        return ResponseEntity.status(HttpStatus.OK).body(
                new BasicResponseDto(
                        "success", "Assignment updated successfully"
                )
        );
    }

    @GetMapping("/{aId}")
    public AssignmentDto getAssignment(@PathVariable Long courseId, @PathVariable Long aId) {
        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
                !courseService.isInstructing(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }
        if( userService.getCurrentUserRole() == Roles.ROLE_STUDENT &&
                !courseService.isEnrolled(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }

        return assignmentService.getAssignment(aId);
    }






}
