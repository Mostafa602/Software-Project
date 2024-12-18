package com.lms.domain.dto.quiz;

import com.lms.domain.dto.course.QuestionDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuizInstanceDto {
    Long submissionId;
    Set<QuestionQuizDto> questions;

    public QuizInstanceDto(Set<QuestionQuizDto> questions) {
        this.questions = questions;
    }

    public QuizInstanceDto() {
        this.questions = new HashSet<>();
    }

    public Set<QuestionQuizDto> getQuestions() {
        return questions;
    }

    public void setQuestions(Set<QuestionQuizDto> questions) {
        this.questions = questions;
    }

    public Long getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(Long submissionId) {
        this.submissionId = submissionId;
    }

    public void addQuestion(QuestionQuizDto question) {
        this.questions.add(question);
    }
}
