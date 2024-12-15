package com.lms.controller;

import com.lms.domain.dto.BasicResponseDto;
import com.lms.domain.dto.course.*;
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

    @GetMapping("/")
    public ResponseEntity <List<CourseDto>> getCourses() {
        return ResponseEntity.status(HttpStatus.OK).body(courseService.getAllCourses());
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<?> getCourseById(@PathVariable Long courseId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(courseService.getCourseById(courseId));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new BasicResponseDto(
                            "failure",
                            "invalid course id."
                    )
            );
        }
    }

    @GetMapping("/{courseId}/students")
    public ResponseEntity<?> getEnrolledStudents(@PathVariable Long courseId) {
        try{
            return ResponseEntity.ok(courseService.getAllStudents(courseId)) ;
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new BasicResponseDto(
                            "failure",
                            "invalid course id"
                    )
            );
        }
    }

    @PostMapping("/")
    public ResponseEntity<BasicResponseDto> createCourse(@RequestBody CourseCreationDto courseDto) {
        if(userService.getCurrentUserRole() == Roles.ROLE_STUDENT) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BasicResponseDto(
                    "failure",
                    "Unauthorized"
            ));
        }
        try{
            courseService.saveCourse(courseDto);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new BasicResponseDto(
                            "success",
                            "course created successfully"
                    )
            );
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new BasicResponseDto(
                            "failure",
                            e.getMessage()
                    )
            );
        }
    }


    @PostMapping("/enroll")
    public ResponseEntity <BasicResponseDto> enrollStudent(@RequestBody CourseEnrollmentDto courseEnrollmentDto) {

        if (courseEnrollmentDto.getCourseId() == null || courseEnrollmentDto.getStudentId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponseDto(
                    "failure",
                    "Both courseId and studentId must be provided."
            ));
        }
        if( userService.getCurrentUserRole() == Roles.ROLE_STUDENT &&
                !Objects.equals(courseEnrollmentDto.getStudentId(), userService.getCurrentUserId())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BasicResponseDto(
                    "failure",
                    "Unauthorized"
            ));
        }
        try {

            courseService.enrollStudent(courseEnrollmentDto.getCourseId(), courseEnrollmentDto.getStudentId());
            return ResponseEntity.status(HttpStatus.CREATED).body(new BasicResponseDto(
                    "success",
                    "Student enrolled successfully!"
            ));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponseDto(
                    "failure",
                    e.getMessage()
            ));
        }
    }

    @PostMapping("/unenroll")
    public ResponseEntity <BasicResponseDto> unenrollStudent(@RequestBody CourseEnrollmentDto courseEnrollmentDto) {

        if (courseEnrollmentDto.getCourseId() == null || courseEnrollmentDto.getStudentId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponseDto(
                    "failure",
                    "Both courseId and studentId must be provided."
            ));
        }
        if( userService.getCurrentUserRole() == Roles.ROLE_STUDENT &&
                !Objects.equals(courseEnrollmentDto.getStudentId(), userService.getCurrentUserId())
        ){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BasicResponseDto(
                    "failure",
                    "Unauthorized"
            ));
        }
        try {
            if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
            !courseService.isInstructing(userService.getCurrentUserId() ,courseEnrollmentDto.getCourseId())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new BasicResponseDto(
                        "failure",
                        "Unauthorized"
                ));
            }

            courseService.unenrollStudent(courseEnrollmentDto.getCourseId(), courseEnrollmentDto.getStudentId());
            return ResponseEntity.status(HttpStatus.CREATED).body(new BasicResponseDto(
                    "success",
                    "Student unenrolled successfully!"
            ));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponseDto(
                    "failure",
                    e.getMessage()
            ));
        }
    }

    @PostMapping("/{courseId}/questions")
    public ResponseEntity<?> addQuestionToCourse(@PathVariable Long courseId, @RequestBody QuestionDto questionDto) {
        try{
            courseService.addQuestion(courseId, questionDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(new BasicResponseDto(
                    "success",
                    "Question added successfully!"
            ));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponseDto(
                    "failure",
                    e.getMessage()
            ));
        }

    }

    @GetMapping("/{courseId}/questions")
    public ResponseEntity<?> getQuestionsOfCourse(@PathVariable Long courseId) {
        try{
            return ResponseEntity.status(HttpStatus.OK).body(
                    courseService.getQuestions(courseId)
            );
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new BasicResponseDto(
                            "failure",
                            e.getMessage()
                    )
            );
        }
    }

    @PostMapping("/{courseId}/materials")
    public ResponseEntity<?> addMaterial(@PathVariable Long courseId,
        @RequestParam("file")  MultipartFile file,
         @RequestParam("type") Material type
    ) {
        if(type == null || file == null || file.isEmpty() || courseId == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BasicResponseDto(
                    "failure",
                    "please provide all required fields"
            ));
        }

        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    courseService.addMaterial(courseId, file, type)
            );
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new BasicResponseDto(
               "failure",e.getMessage()
            ));
        }
    }

    @GetMapping("/materials/{materialId}")
    public ResponseEntity<?> getMaterial(@PathVariable Long materialId) {
         try {
             MaterialTransferDto materialTransferDto = courseService.getMaterial(materialId);
             return ResponseEntity.ok()
                     .contentType(MediaType.parseMediaType(materialTransferDto.getContentType()))
                     .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + materialTransferDto.getName() + "\"")
                     .body(materialTransferDto.getResource());

         }
         catch (Exception e) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                     new BasicResponseDto(
                             "failure",
                             e.getMessage()
                     )
             );
         }

    }
}
