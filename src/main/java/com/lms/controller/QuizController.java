package com.lms.controller;

import com.lms.domain.dto.BasicResponseDto;
import com.lms.domain.dto.quiz.QuizSubmissionDto;
import com.lms.domain.execptionhandler.MissingFieldsException;
import com.lms.domain.execptionhandler.UnauthorizedAccessException;
import com.lms.domain.model.email.EmailNotification;
import com.lms.domain.model.user.Roles;
import com.lms.domain.model.user.Student;
import com.lms.domain.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lms.domain.dto.quiz.QuizCreationDto;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/courses/{courseId}/quizzes")
public class QuizController {


    private final QuizService quizService;
    private final UserService userService;
    private final CourseService courseService;
    private final NotificationService notificationService;
    private final EmailService emailService;


    public QuizController(QuizService quizService, UserService userService, CourseService courseService,
                          NotificationService notificationService, EmailService emailService) {
        this.quizService = quizService;
        this.userService = userService;
        this.courseService = courseService;
        this.notificationService = notificationService;
        this.emailService = emailService;
    }

    @PostMapping("/")
    public ResponseEntity<?> createQuiz(@PathVariable Long courseId,
                                        @RequestBody QuizCreationDto quizCreationDto) {
        notificationService.addNotification("Quiz has been created " , 1L,"student");
        if(quizCreationDto.getTitle()==null || quizCreationDto.getNumberOfQuestions()==null) {
            throw new MissingFieldsException("provide all required fields");
        }
        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
                !courseService.isInstructing(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }
        quizService.createQuiz(courseId, quizCreationDto);

        Set<Student> enrolledStudent = courseService.findEnrolledStudent(courseId);
        String content = "New quiz has been added to " + courseService.getCourseById(courseId).getName() + " course";
        String name = userService.getUserById(userService.getCurrentUserId()).getFirstName();
        for( Student student : enrolledStudent ){
            notificationService.addNotification(content, student.getId(),"student");
            emailService.sendEmail(student.getEmail() ,"New Quiz",  content, name);
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                new BasicResponseDto("success", "quiz created successfully")
        );
    }

    @GetMapping("/{qId}")
    public ResponseEntity<?> getQuiz(@PathVariable Long courseId, @PathVariable Long qId) {
        if( userService.getCurrentUserRole() == Roles.ROLE_STUDENT &&
                !courseService.isEnrolled(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                quizService.getQuiz(courseId, userService.getCurrentUserId(), qId)
        );
    }

    @PostMapping("/{qId}/{subId}")
    public ResponseEntity<?> submitQuiz(@PathVariable Long courseId, @PathVariable Long qId, @PathVariable Long subId, @RequestBody QuizSubmissionDto quizSubmissionDto) {
        Long userId = userService.getCurrentUserId();
        String content = "You have submit a quiz for " + courseService.getCourseById(courseId).getName() + " course perfectly ";
        notificationService.addNotification(content,userId, "student");
        return ResponseEntity.status(HttpStatus.OK).body(
                quizService.submitQuiz(courseId, qId, subId, userId, quizSubmissionDto)
        );
    }

    @GetMapping("/{qId}/{subId}")
    public ResponseEntity<?> getQuizGrade(@PathVariable Long subId) {
        Long userId = userService.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.OK).body(
                quizService.getGrade(subId, userId)
        );
    }

    @GetMapping("/{qId}/submissions")
    public ResponseEntity<?> getQuizSubmissions(@PathVariable Long qId, @PathVariable Long courseId) {
        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
                !courseService.isInstructing(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                quizService.getAllGrades(qId)
        );
    }
}
