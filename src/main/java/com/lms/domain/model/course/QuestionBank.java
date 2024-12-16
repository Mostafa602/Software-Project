package com.lms.domain.model.course;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class QuestionBank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "questionBank", cascade = CascadeType.ALL)
    private Course course;

    @OneToMany(mappedBy = "questionBank", cascade = CascadeType.ALL)
    private Set<Question> questions;

    public QuestionBank(Long id, Course course, Set<Question> questions) {
        this.id = id;
        this.course = course;
        this.questions = questions;
    }

    public QuestionBank() {
        this.questions = new HashSet<Question>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Set<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
    }
    public void removeQuestion(Question question) {
        this.questions.remove(question);
    }
}
