package com.lms.domain.service;

import com.lms.domain.dto.notifications.EmailDto;
import com.lms.domain.execptionhandler.MissingFieldsException;
import com.lms.domain.model.email.EmailNotification;
import com.lms.domain.model.user.User;
import com.lms.domain.repository.EmailNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class EmailService {

//    @Autowired
//    private JavaMailSender mailSender;

    private final EmailNotificationRepository emailNotificationRepository;
    private final UserService userService;

    @Autowired
    public EmailService(EmailNotificationRepository emailNotificationRepository, UserService userService) {
        this.emailNotificationRepository = emailNotificationRepository;
        this.userService = userService;
    }


    public void sendEmail(String to, String subject, String content, String instructorName) {
        EmailNotification emailNotification = new EmailNotification();
        User user = userService.getUserByEmail(to).orElseThrow(
                () -> new MissingFieldsException("Invalid email or password")
        );
        String fullContent = content + "\n\nBest regards,\n" + instructorName;
        Long studentId = user.getId();
        emailNotification.setStudentId(studentId);
        emailNotification.setSubject(subject);
        System.out.println(subject);
        emailNotification.setBody(fullContent);
        emailNotificationRepository.save(emailNotification);

    }

    public List<EmailDto> findEmailNotificationByStudentId (Long studentId) {
       List<EmailNotification> emailNotifications = emailNotificationRepository.findByStudentId(studentId);
       String studentName =  userService.getUserById(studentId).getFirstName();
       List<EmailDto> emailDtos = new ArrayList<>();
       for ( EmailNotification emailNotification : emailNotifications  ){
           EmailDto emailDto = new EmailDto( studentName,emailNotification.getSubject(), emailNotification.getBody() );
           emailDtos.add(emailDto);
       }
       return emailDtos;
    }

}
