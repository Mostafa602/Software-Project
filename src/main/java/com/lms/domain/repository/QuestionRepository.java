package com.lms.domain.repository;

import com.lms.domain.model.course.Question;
import com.lms.domain.model.user.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
}
