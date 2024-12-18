package com.lms.controller;


import com.lms.domain.dto.course.AssignmentDto;
import com.lms.domain.model.course.Assignment;
import com.lms.domain.service.AssignmentService;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/courses/{courseId}/assignments")
public class AssignmentController {

    AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping()
    public String createAssignment(@PathVariable Long courseId, @RequestBody AssignmentDto assignment) {
        assignmentService.createAssignment(courseId,assignment);
        return "Assignment created successfully";
    }

    @PutMapping("/{aId}")
    public String updateAssignment(@PathVariable Long courseId,
                                                   @PathVariable Long aId,
                                                   @RequestBody AssignmentDto updatedAssignment) {

        assignmentService.updateAssignment(courseId, aId, updatedAssignment);
        return ("Assignment updated successfully!");
    }

    @GetMapping("/{aId}")
    public AssignmentDto getAssignment(@PathVariable Long courseId, @PathVariable Long aId) {
        Assignment assignment = assignmentService.getAssignment(aId);
        String title = assignment.getTitle();
        String description = assignment.getDescription();
        Date date = assignment.getDueDate();
        AssignmentDto assignmentDto = new AssignmentDto(date,description,title);
        return assignmentDto;
    }






}
