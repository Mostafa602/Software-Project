package com.lms.domain.repository;

import com.lms.domain.model.user.Student;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StudentRepository extends JpaRepository<Student, Long> {
}
