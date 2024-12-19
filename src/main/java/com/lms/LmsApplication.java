package com.lms;

import com.lms.domain.model.course.Course;
import com.lms.domain.model.notification.Notification;
import com.lms.domain.model.user.Instructor;
import com.lms.domain.model.user.Student;
import com.lms.domain.repository.CourseRepository;
import com.lms.domain.repository.UserRepository;
import com.lms.domain.service.NotificationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class LmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(LmsApplication.class, args);
    }
    @Bean
    public CommandLineRunner insertDummyData(UserRepository userRepository, CourseRepository courseRepository) {
        return args -> {
            Instructor hema = new Instructor("Ibrahim", "Mkhasy", "hema@gees.com", new BCryptPasswordEncoder().encode("123"));

            if (userRepository.count() == 0) {
                userRepository.save(new Student("Tamer", "ElGayar","tamer@gees.com", new BCryptPasswordEncoder().encode("123")));
                userRepository.save(new Student("Not", "ElGayar","gayar@gees.com", new BCryptPasswordEncoder().encode("123")));
                userRepository.save(hema);
            }
            if(courseRepository.count() == 0) {
                courseRepository.save(new Course("Java Programming", "Java Programming", hema));
                courseRepository.save(new Course("Python", "Python Programming", hema));
            }
        };
    }
}
