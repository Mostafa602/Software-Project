package com.lms.domain.model.course;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private int numberOfQuestions;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private Set<QuizSubmission> studentsSubmissions;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Quiz(String title, int numberOfQuestions, Course course) {
        this.title = title;
        this.numberOfQuestions = numberOfQuestions;
        this.course = course;
        this.studentsSubmissions = new HashSet<>();
    }
    public Quiz() {
        this.studentsSubmissions = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<QuizSubmission> getStudentsSubmissions() {
        return studentsSubmissions;
    }

    public void setStudentsSubmissions(Set<QuizSubmission> studentsSubmissions) {
        this.studentsSubmissions = studentsSubmissions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
