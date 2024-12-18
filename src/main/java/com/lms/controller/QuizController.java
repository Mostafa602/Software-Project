package com.lms.controller;

import com.lms.domain.dto.BasicResponseDto;
import com.lms.domain.dto.quiz.QuizSubmissionDto;
import com.lms.domain.execptionhandler.MissingFieldsException;
import com.lms.domain.execptionhandler.UnauthorizedAccessException;
import com.lms.domain.model.user.Roles;
import com.lms.domain.service.CourseService;
import com.lms.domain.service.QuizService;
import com.lms.domain.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lms.domain.dto.quiz.QuizCreationDto;

@RestController
@RequestMapping("/courses/{courseId}/quizzes")
public class QuizController {


    private final QuizService quizService;
    private final UserService userService;
    private final CourseService courseService;


    public QuizController(QuizService quizService, UserService userService, CourseService courseService) {
        this.quizService = quizService;
        this.userService = userService;
        this.courseService = courseService;
    }

    @PostMapping("/")
    public ResponseEntity<?> createQuiz(@PathVariable Long courseId,
                                        @RequestBody QuizCreationDto quizCreationDto) {
        if(quizCreationDto.getTitle()==null || quizCreationDto.getNumberOfQuestions()==null) {
            throw new MissingFieldsException("provide all required fields");
        }
        if(userService.getCurrentUserRole() == Roles.ROLE_INSTRUCTOR &&
                !courseService.isInstructing(userService.getCurrentUserId() ,courseId)) {
            throw new UnauthorizedAccessException();
        }
        quizService.createQuiz(courseId, quizCreationDto);
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
