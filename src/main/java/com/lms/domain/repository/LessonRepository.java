package com.lms.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.domain.model.course.Lesson;

public interface LessonRepository extends JpaRepository<Lesson,Long>{
    public Lesson findLessonByotp(Long otp);
}
