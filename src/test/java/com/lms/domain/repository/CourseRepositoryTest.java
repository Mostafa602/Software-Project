package com.lms.domain.repository;

import com.lms.domain.model.course.Course;
import com.lms.domain.model.user.Instructor;
import com.lms.domain.model.user.Student;
import com.lms.domain.projection.CourseProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private InstructorRepository instructorRepository;

    @Autowired
    private StudentRepository studentRepository;

    private Course course;
    private Instructor instructor;
    private Student student;

    @BeforeEach
    public void setUp() {
        instructor = new Instructor("Ali", "Mahmoud", "ali.mahmoud@example.com", "securePassword");
        instructorRepository.save(instructor);

        student = new Student("Mona", "Amin", "mona.amin@example.com", "securePassword");
        studentRepository.save(student);

        course = new Course("Course 101", "Introduction to Testing", instructor);
        course.enrollStudent(student);
        courseRepository.save(course);
    }


    @Test
    public void testFindProjectedById() {
        CourseProjection projection = courseRepository.findProjectedById(course.getId());
        assertThat(projection).isNotNull();
        assertThat(projection.getName()).isEqualTo("Course 101");
    }

    @Test
    public void testFindEnrolledStudentsById() {
        Set<Student> students = courseRepository.findEnrolledStudentsById(course.getId());
        assertThat(students).isNotNull();
        assertThat(students).hasSize(1);
        assertThat(students.iterator().next().getEmail()).isEqualTo("mona.amin@example.com");
    }
}