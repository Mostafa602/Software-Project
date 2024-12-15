package com.lms.domain.repository;

import com.lms.domain.model.user.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InstructorRepository extends JpaRepository<Instructor, Long> {
}
