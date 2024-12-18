package com.lms.controller;

import com.lms.domain.dto.BasicResponseDto;
import com.lms.domain.dto.course.*;
import com.lms.domain.execptionhandler.MissingFieldsException;
import com.lms.domain.execptionhandler.UnauthorizedAccessException;
import com.lms.domain.model.course.Material;
import com.lms.domain.model.user.Roles;
import com.lms.domain.service.CourseService;
import com.lms.domain.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;
    private final UserService userService;


    public CourseController(CourseService courseService, UserService userService) {
        this.courseService = courseService;
        this.userService = userService;
    }

    // access : ALL
    @GetMapping("/")
    public ResponseEntity <List<CourseDto>> getCourses() {
        return ResponseEntity.status(HttpStatus.OK).body(courseService.getAllCourses());
    }

    // access : ALL
    @GetMapping("/{courseId}")
    public ResponseEntity<?> getCourseById(@PathVariable Long courseId) {
            return ResponseEntity.status(HttpStatus.OK).body(courseService.getCourseById(courseId));
    }

    // access : ADMIN or INSTRUCTOR (if instructing only)
    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long courseId) {

        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
                !courseService.isInstructing(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }

        courseService.deleteCourse(courseId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new BasicResponseDto(
                        "success",
                        "course deleted successfully"
                )
        );
    }

    // access : ADMIN or INSTRUCTOR (if instructing only)
    @PutMapping("/{courseId}")
    public ResponseEntity<?> updateCourseById(@PathVariable Long courseId, @RequestBody CourseUpdateDto courseUpdateDto) {
        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR && //IMPORTANT
                !courseService.isInstructing(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }

        if(courseUpdateDto.getName()==null && courseUpdateDto.getDescription()!=null)
            throw new MissingFieldsException("provide fields to update");

        courseService.updateCourse(courseId, courseUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new BasicResponseDto(
                        "success",
                        "course updated successfully"
                )
        );
    }

    // access : ADMIN or INSTRUCTOR (if instructing only)
    @GetMapping("/{courseId}/students")
    public ResponseEntity<?> getEnrolledStudents(@PathVariable Long courseId) {
        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
                !courseService.isInstructing(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }
        return ResponseEntity.ok(courseService.getAllStudents(courseId)) ;
    }

    // access : ADMIN or INSTRUCTOR
    @PostMapping("/")
    public ResponseEntity<BasicResponseDto> createCourse(@RequestBody CourseCreationDto courseDto) {

        courseService.saveCourse(courseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new BasicResponseDto(
                        "success",
                        "course created successfully"
                )
        );
    }

    // access : STUDENT (for himself only) or ADMIN (for any student)
    @PostMapping("/enroll")
    public ResponseEntity <BasicResponseDto> enrollStudent(@RequestBody CourseEnrollmentDto courseEnrollmentDto) {
        if (courseEnrollmentDto.getCourseId() == null || courseEnrollmentDto.getStudentId() == null) {
            throw new MissingFieldsException("Both course_id and student_id must be provided.");
        }
        if( userService.getCurrentUserRole() == Roles.ROLE_STUDENT &&
                !Objects.equals(courseEnrollmentDto.getStudentId(), userService.getCurrentUserId())){
            throw new UnauthorizedAccessException();
        }

        courseService.enrollStudent(courseEnrollmentDto.getCourseId(), courseEnrollmentDto.getStudentId());

        return ResponseEntity.status(HttpStatus.CREATED).body(new BasicResponseDto(
                "success",
                "Student enrolled successfully!"
        ));


    }

    // access : STUDENT (for himself only) or ADMIN (for any student) or INSTRUCTOR (for any student if instructing only)
    @PostMapping("/unenroll")
    public ResponseEntity <BasicResponseDto> unenrollStudent(@RequestBody CourseEnrollmentDto courseEnrollmentDto) {

        if (courseEnrollmentDto.getCourseId() == null || courseEnrollmentDto.getStudentId() == null) {
            throw new MissingFieldsException("Both course_id and student_id must be provided.");
        }
        if( userService.getCurrentUserRole() == Roles.ROLE_STUDENT &&
                !Objects.equals(courseEnrollmentDto.getStudentId(), userService.getCurrentUserId())
        ){
            throw new UnauthorizedAccessException();
        }

        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
        !courseService.isInstructing(userService.getCurrentUserId() ,courseEnrollmentDto.getCourseId())) {
            throw new UnauthorizedAccessException();
        }

        courseService.unenrollStudent(courseEnrollmentDto.getCourseId(), courseEnrollmentDto.getStudentId());
        return ResponseEntity.status(HttpStatus.CREATED).body(new BasicResponseDto(
                "success",
                "Student unenrolled successfully!"
        ));

    }

    // access : ADMIN or INSTRUCTOR (if instructing only)
    @PostMapping("/{courseId}/questions")
    public ResponseEntity<?> addQuestionToCourse(@PathVariable Long courseId, @RequestBody QuestionDto questionDto) {

        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
                !courseService.isInstructing(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }

        courseService.addQuestion(courseId, questionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BasicResponseDto(
                "success",
                "Question added successfully!"
        ));

    }

    // access : ADMIN or INSTRUCTOR (if instructing only)
    @GetMapping("/{courseId}/questions")
    public ResponseEntity<?> getQuestionsOfCourse(@PathVariable Long courseId) {
        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
                !courseService.isInstructing(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                courseService.getQuestions(courseId)
        );
    }

    // access : ADMIN or INSTRUCTOR (if instructing only)
    @DeleteMapping("/{courseId}/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long courseId, @PathVariable Long questionId) {
        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
                !courseService.isInstructing(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }

        courseService.deleteQuestion(questionId);
        return ResponseEntity.status(HttpStatus.OK).body(new BasicResponseDto(
                "success", "Question deleted successfully!"
        ));
    }

    // access : ADMIN or INSTRUCTOR (if instructing only)
    @GetMapping("/{courseId}/questions/{questionId}")
    public ResponseEntity<?> getQuestion(@PathVariable Long courseId, @PathVariable Long questionId) {
        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
                !courseService.isInstructing(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }
        return ResponseEntity.status(HttpStatus.OK).body(courseService.getQuestion(questionId));
    }

    // access : ADMIN or INSTRUCTOR (if instructing only)
    @PostMapping("/{courseId}/materials")
    public ResponseEntity<?> addMaterial(@PathVariable Long courseId,
        @RequestParam("file")  MultipartFile file,
         @RequestParam("type") Material type
    ) {
        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
                !courseService.isInstructing(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }

        if(type == null || file == null || file.isEmpty() || courseId == null) {
            throw new MissingFieldsException("all fields must be provided.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(
                courseService.addMaterial(courseId, file, type)
        );

    }

    // access : ALL -> ADMIN or INSTRUCTOR (if instructing only) or STUDENT (if enrolled only)
    @GetMapping("/{courseId}/materials/{materialId}")
    public ResponseEntity<?> getMaterial(@PathVariable Long courseId, @PathVariable Long materialId) {

        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
                !courseService.isInstructing(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }

        if(userService.getCurrentUserRole() == Roles.ROLE_STUDENT &&
                !courseService.isEnrolled(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }

        MaterialTransferDto materialTransferDto = courseService.getMaterial(materialId);
         return ResponseEntity.ok()
                 .contentType(MediaType.parseMediaType(materialTransferDto.getContentType()))
                 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + materialTransferDto.getName() + "\"")
                 .body(materialTransferDto.getResource());

    }
}
