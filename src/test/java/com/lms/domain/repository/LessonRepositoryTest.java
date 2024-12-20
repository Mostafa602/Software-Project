package com.lms.domain.repository;

import com.lms.domain.model.course.Lesson;
import com.lms.domain.model.course.Course;
import com.lms.domain.model.user.Instructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class LessonRepositoryTest {

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    private Course course;

    @BeforeEach
    public void setUp() {
        Instructor instructor = new Instructor("Ali", "Mahmoud", "ali.mahmoud@example.com", "securePassword");
        instructorRepository.save(instructor);

        course = new Course("Course 101", "Introduction to Testing", instructor);
        courseRepository.save(course);

        Lesson lesson1 = new Lesson(null, "Lesson 1", "Description 1", course, 123456L);
        Lesson lesson2 = new Lesson(null, "Lesson 2", "Description 2", course, 654321L);

        lessonRepository.save(lesson1);
        lessonRepository.save(lesson2);
    }

    @Test
    public void testFindLessonByOtp() {
        Lesson lesson = lessonRepository.findLessonByotp(123456L);
        assertThat(lesson).isNotNull();
        assertThat(lesson.getName()).isEqualTo("Lesson 1");

        Lesson lessonNotFound = lessonRepository.findLessonByotp(111111L);
        assertThat(lessonNotFound).isNull();
    }
}