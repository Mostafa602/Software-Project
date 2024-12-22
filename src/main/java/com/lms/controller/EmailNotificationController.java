package com.lms.controller;

import com.lms.domain.dto.BasicResponseDto;
import com.lms.domain.dto.notifications.EmailDto;
import com.lms.domain.execptionhandler.EmailSendingException;
import com.lms.domain.execptionhandler.MissingFieldsException;
import com.lms.domain.execptionhandler.UnauthorizedAccessException;
import com.lms.domain.model.email.EmailNotification;
import com.lms.domain.model.user.Roles;
import com.lms.domain.repository.EmailNotificationRepository;
import com.lms.domain.service.EmailService;
import com.lms.domain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emails")
public class EmailNotificationController {

    private final UserService userService;
    private final EmailService emailService;

    @Autowired
    public EmailNotificationController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

//    @GetMapping("/{studentId}")
//    public ResponseEntity<List<EmailDto>> getEmailNotifications(@PathVariable Long studentId) {
//        if (userService == null) {
//            throw new RuntimeException("UserService is null!");
//        }
//        if ((userService.getCurrentUserRole() != Roles.ROLE_STUDENT ) || (!studentId.equals(userService.getCurrentUserId())) ) {
//            throw new UnauthorizedAccessException();
//        }
//        List<EmailDto> emailNotifications = emailService.findEmailNotificationByStudentId(studentId);
//        return ResponseEntity.status(HttpStatus.OK).body(emailNotifications);
//    }

    @PostMapping
    public ResponseEntity<BasicResponseDto>  createEmailNotification (@RequestBody EmailDto emailDto){
        if (emailDto == null || emailDto.getTo() == null || emailDto.getContent() == null || emailDto.getSubject() == null
                || emailDto.getTo().isEmpty() || emailDto.getContent().isEmpty() || emailDto.getSubject().isEmpty()) {
            throw new MissingFieldsException("All fields (to, subject, content) must be provided.");
        }
        if(userService.getCurrentUserRole() == Roles.ROLE_STUDENT ) {
            throw new UnauthorizedAccessException();
        }
        try {
            String instructorName =  userService.getUserById(userService.getCurrentUserId()).getFirstName();
            emailService.sendEmail( emailDto.getTo(), emailDto.getSubject(),emailDto.getContent(), instructorName);
            return ResponseEntity.status(HttpStatus.CREATED).body(new BasicResponseDto(
                    "success",
                    "Email has been sent successfully!"
            ));
        } catch (Exception ex) {
            throw new EmailSendingException("Failed to send email: " + ex.getMessage(), ex);
        }
    }

}
