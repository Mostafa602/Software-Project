package com.lms.domain.service;


import com.lms.domain.dto.course.CourseMaterialResponseDto;
import com.lms.domain.dto.course.MaterialTransferDto;
import com.lms.domain.dto.course.SetGradeDto;
import com.lms.domain.execptionhandler.InternalServerException;
import com.lms.domain.model.course.Assignment;
import com.lms.domain.model.course.AssignmentSubmission;
import com.lms.domain.model.course.CourseMaterial;
import com.lms.domain.model.user.Student;
import com.lms.domain.repository.AssignmentRepository;
import com.lms.domain.repository.AssignmentSubmissionRepository;
import com.lms.domain.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;



@Service
public class AssignmentSubmissionService {

    @Autowired
    AssignmentSubmissionRepository assignmentSubmissionRepository;

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AssignmentRepository assignmentRepository;
    private final String uploadPath = "uploads/%d";
    private String getUploadPath(Long courseId) {
        return String.format(uploadPath, courseId);
    }

    public AssignmentSubmissionService(AssignmentSubmissionRepository assignmentSubmissionRepository) {
        this.assignmentSubmissionRepository = assignmentSubmissionRepository;
    }

    public void SubmitAssignment(Long sId, Long aId, MultipartFile file) {
        Student student = studentRepository.findById(sId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + sId));
        Assignment assignment = assignmentRepository.findById(aId)
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found with ID: " + aId));

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Path uploadPath = Paths.get(getUploadPath(assignment.getId()));

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path targetPath = uploadPath.resolve(fileName);
            file.transferTo(targetPath);
            AssignmentSubmission assignmentSubmission = new AssignmentSubmission(student,
                    targetPath.toString(), assignment
            );
            assignmentSubmission = assignmentSubmissionRepository.save(assignmentSubmission);
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Resource getSubmission(Long subId) {
        AssignmentSubmission assignmentSubmission = assignmentSubmissionRepository.findById(subId).orElseThrow(
                ()-> new EntityNotFoundException("Submission not found with ID: " + subId)
        );
        String fileUrl = assignmentSubmission.getUrl();
        File file = new File(fileUrl);
        String contentType ;

        try{
            contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

        }
        catch (Exception e){
            throw new InternalServerException();
        }
        Path path = file.toPath();
        Resource resource;

        try {
            resource = new UrlResource(path.toUri());
        }
        catch (MalformedURLException e) {
            throw new InternalServerException();
        }
        String sysFileName = file.getName();
        String fileName = sysFileName.substring(sysFileName.indexOf("_")+1);
        return resource;
    }

    public void gradeAssignment(Long subId, SetGradeDto grade) {
        AssignmentSubmission assignmentSubmission = assignmentSubmissionRepository.findById(subId).orElseThrow(
                ()-> new EntityNotFoundException("Submission not found with ID: " + subId)
        );
        assignmentSubmission.setGrade(grade.getGrade());
        assignmentSubmissionRepository.save(assignmentSubmission);
    }

    public SetGradeDto getGrade(Long subId) {
        AssignmentSubmission assignmentSubmission = assignmentSubmissionRepository.findById(subId).orElseThrow(
                ()-> new EntityNotFoundException("Submission not found with ID: " + subId)
        );
        SetGradeDto grade = new SetGradeDto();
        grade.setGrade(assignmentSubmission.getGrade());
        return grade;
    }




}
