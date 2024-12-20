package com.lms.config.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers(HttpMethod.POST, "/register").permitAll();
                    registry.requestMatchers(HttpMethod.POST, "/login").permitAll();
                    registry.requestMatchers(HttpMethod.DELETE, "courses/{courseId}").hasAnyRole("INSTRUCTOR", "ADMIN"); // delete course
                    registry.requestMatchers(HttpMethod.PUT, "courses/{courseId}").hasAnyRole("INSTRUCTOR", "ADMIN"); // update course
                    registry.requestMatchers(HttpMethod.GET, "courses/{courseId}/students").hasAnyRole("INSTRUCTOR", "ADMIN"); // get course students
                    registry.requestMatchers(HttpMethod.POST, "courses/").hasAnyRole("INSTRUCTOR", "ADMIN"); // add course
                    registry.requestMatchers(HttpMethod.POST, "courses/enroll").hasAnyRole("STUDENT", "ADMIN"); // enroll course
                    registry.requestMatchers(HttpMethod.POST, "courses/unenroll").hasAnyRole("STUDENT", "ADMIN", "INSTRUCTOR"); // unenroll course
                    registry.requestMatchers(HttpMethod.POST, "courses/{courseId}/questions").hasAnyRole("INSTRUCTOR", "ADMIN"); // add question to course question bank
                    registry.requestMatchers(HttpMethod.GET, "courses/{courseId}/questions").hasAnyRole("INSTRUCTOR", "ADMIN"); // get course question bank
                    registry.requestMatchers(HttpMethod.DELETE, "courses/{courseId}/questions/{questionId}").hasAnyRole("INSTRUCTOR", "ADMIN"); // delete question from a course
                    registry.requestMatchers(HttpMethod.GET, "courses/{courseId}/questions/{questionId}").hasAnyRole("INSTRUCTOR", "ADMIN"); // get a question from course qb
                    registry.requestMatchers(HttpMethod.POST, "courses/{courseId}/materials").hasAnyRole("INSTRUCTOR", "ADMIN"); // add course material
                    registry.requestMatchers(HttpMethod.POST, "courses/{courseId}/assignments").hasAnyRole("INSTRUCTOR", "ADMIN"); // Create an assignment
                    registry.requestMatchers(HttpMethod.PUT, "courses/{courseId}/assignments/{aId}").hasAnyRole("INSTRUCTOR", "ADMIN"); // Update an assignment
                    registry.requestMatchers(HttpMethod.POST, "courses/{courseId}/assignments/{aId}/submit").hasRole("STUDENT"); // Submit assignment
                    registry.requestMatchers(HttpMethod.POST, "courses/{courseId}/assignments/{aId}/grade/submission/{subId}").hasAnyRole("INSTRUCTOR", "ADMIN"); // Grade assignment
                    registry.requestMatchers(HttpMethod.GET, "users/{userid}/notification").hasAnyRole("INSTRUCTOR", "STUDENT"); // See the notification
                    registry.requestMatchers(HttpMethod.GET, "courses/{courseId}/lesson/{OTP}").hasAnyRole("INSTRUCTOR", "STUDENT", "ADMIN"); //  Get a lesson by its otp
                    registry.requestMatchers(HttpMethod.POST, "courses/{courseId}/lesson/").hasAnyRole("INSTRUCTOR", "ADMIN"); // Create a lesson for a specific course
                    registry.requestMatchers(HttpMethod.GET, "emails/{studentId}").hasAnyRole( "STUDENT"); //  Get an email
                    registry.requestMatchers(HttpMethod.POST, "emails").hasAnyRole("INSTRUCTOR", "ADMIN"); //  Create an email


                    registry.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


}
