package com.lms.domain.repository;

import com.lms.domain.model.course.Assignment;
import com.lms.domain.model.course.AssignmentSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long>{

}
