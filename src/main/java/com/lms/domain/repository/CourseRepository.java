package com.lms.domain.repository;

import com.lms.domain.model.course.Course;
import com.lms.domain.model.user.Student;
import com.lms.domain.projection.CourseProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<CourseProjection> findAllProjectedBy();
    CourseProjection findProjectedById(Long id);

}
