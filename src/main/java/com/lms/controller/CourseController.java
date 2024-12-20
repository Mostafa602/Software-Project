package com.lms.controller;

import com.lms.domain.dto.BasicResponseDto;
import com.lms.domain.dto.course.*;
import com.lms.domain.execptionhandler.MissingFieldsException;
import com.lms.domain.execptionhandler.UnauthorizedAccessException;
import com.lms.domain.model.course.Course;
import com.lms.domain.model.course.Material;
import com.lms.domain.model.user.Instructor;
import com.lms.domain.model.user.Roles;
import com.lms.domain.model.user.Student;
import com.lms.domain.repository.NotificationRepository;
import com.lms.domain.service.CourseService;
import com.lms.domain.service.NotificationService;
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
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/courses")
public class CourseController {
    private final CourseService courseService;
    private final UserService userService;
    private final NotificationService notificationService;


    public CourseController(CourseService courseService, UserService userService, NotificationService notificationService) {
        this.courseService = courseService;
        this.userService = userService;
        this.notificationService = notificationService;
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
        String nameStudent = userService.getUserById( courseEnrollmentDto.getStudentId()).getFirstName();
        Long courseId = courseEnrollmentDto.getCourseId();
        String courseName = courseService.getCourseById(courseId).getName();
        String content = nameStudent +" you have been enrolled into " + courseName + " Course";
        notificationService.addNotification(content,courseEnrollmentDto.getStudentId(),"student");
        Set<Instructor> instructors = courseService.findInstructors(courseId);
        String message = " has been enrolled into your ";
        for( Instructor instructor : instructors ){
            String finalMessage = nameStudent + message + courseName + " course";
            notificationService.addNotification(finalMessage, instructor.getId(), "instructor");
        }

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
        Long courseId = courseEnrollmentDto.getCourseId();
        String courseName = courseService.getCourseById(courseId).getName();
        String studentName = userService.getUserById( courseEnrollmentDto.getStudentId()).getFirstName();
        String content = studentName +" you have been unenrolled from " +
                courseName + " Course";
        notificationService.addNotification(content,courseEnrollmentDto.getStudentId(),"student");
        Set<Instructor> instructors = courseService.findInstructors(courseId);
        String message = " has been unenrolled from your ";
        for( Instructor instructor : instructors ){
            String finalMessage = studentName + message + courseName + " course";
            notificationService.addNotification(finalMessage, instructor.getId(),"instructor");
        }

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

        CourseDto course = courseService.getCourseById(courseId);
        String courseName = course.getName();
        String content = "A new question has been added to the course: " + courseName;
        Set<Student> enrolledStudents = courseService.findEnrolledStudent(courseId);
        for (Student student : enrolledStudents) {
            notificationService.addNotification(content, student.getId(), "student");

        }

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

        CourseDto course = courseService.getCourseById(courseId);
        String courseName = course.getName();
        String content = "An existing question has been deleted from  course: " + courseName;
        Set<Student> enrolledStudents = courseService.findEnrolledStudent(courseId);
        for (Student student : enrolledStudents) {
            notificationService.addNotification(content, student.getId(),"student");
        }


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
        CourseDto course = courseService.getCourseById(courseId);
        String courseName = course.getName();
        String content = "A new material has been added to the course: " + courseName;
        Set<Student> enrolledStudents = courseService.findEnrolledStudent(courseId);
        for (Student student : enrolledStudents) {
            notificationService.addNotification(content, student.getId(),"student");
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
    // lesson
    @PostMapping("/{courseId}/lesson/")
    public ResponseEntity<?> addLesson (@PathVariable("courseId") long c_id,@RequestBody LessonDto lesson) {

        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
                !courseService.isInstructing(userService.getCurrentUserId() ,c_id)) {
            throw new UnauthorizedAccessException();
        }
        courseService.addingLesson(c_id, lesson);
        CourseDto course = courseService.getCourseById((Long) c_id);
        String courseName = course.getName();
        String content = "A new lesson has been added to the course: " + courseName;
        Set<Student> enrolledStudents = courseService.findEnrolledStudent((Long) c_id);
        for (Student student : enrolledStudents) {
            notificationService.addNotification(content, student.getId(),"student");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(new BasicResponseDto(
                "success", "lesson added successfully!"
        ));
    }

    @GetMapping("/{courseId}/lesson/{OTP}")
    public ResponseEntity<?> getLesson (@PathVariable("courseId") Long c_id,@PathVariable("OTP") Long OTP) {

        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
                !courseService.isInstructing(userService.getCurrentUserId() ,c_id)) {
            throw new UnauthorizedAccessException();
        }
        LessonDto lessonDto = courseService.getLesson(OTP, c_id);
        return ResponseEntity.status(HttpStatus.OK).body(lessonDto);
    }
}
