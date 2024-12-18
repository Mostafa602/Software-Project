package com.lms.domain.model.course;

import com.lms.domain.model.user.Student;
import jakarta.persistence.*;

@Entity
@Table(name = "assignment_submission")
public class AssignmentSubmission {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student student;


    @ManyToOne
    private Assignment assignment;


    private String url;

    private float grade = -1; // -1 means it is not corrected yet

    public AssignmentSubmission(){

    }

    public AssignmentSubmission(Student student, String url, Assignment assignment) {
        this.student = student;
        this.url = url;
        this.assignment = assignment;
    }

    public float getGrade() {
        return grade;
    }

    public void setGrade(float grade) {
        this.grade = grade;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }







}
