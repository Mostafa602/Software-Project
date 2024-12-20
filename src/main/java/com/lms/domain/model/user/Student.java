package com.lms.domain.model.user;

import com.lms.domain.model.course.AssignmentSubmission;
import com.lms.domain.model.course.QuizSubmission;
import com.lms.domain.model.email.EmailNotification;
import com.lms.domain.model.notification.Notification;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Student extends User {
    private Float gpa;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<QuizSubmission> quizzes;


    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<AssignmentSubmission> assignments;


    public Student(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password, Roles.ROLE_STUDENT);
        this.gpa = null;
        this.assignments = new ArrayList<>();
    }


    public Student() {}


    public Float getGpa() {
        return gpa;
    }
    public void setGpa(Float gpa) {
        this.gpa = gpa;
    }


    public List<QuizSubmission> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<QuizSubmission> quizzes) {
        this.quizzes = quizzes;
    }

    public List<AssignmentSubmission> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<AssignmentSubmission> assignments) {
        this.assignments = assignments;
    }


}
