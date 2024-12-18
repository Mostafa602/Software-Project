package com.lms.domain.dto.quiz;
import java.util.ArrayList;
import java.util.List;

public class QuizSubmissionDto {
    List<QuestionSubmission> questionSubmissions;
    public QuizSubmissionDto() {
        this.questionSubmissions = new ArrayList<>();
    }

    public QuizSubmissionDto(List<QuestionSubmission> questionSubmissions) {
        this.questionSubmissions = questionSubmissions;
    }

    public List<QuestionSubmission> getQuestionSubmissions() {
        return questionSubmissions;
    }

    public void setQuestionSubmissions(List<QuestionSubmission> questionSubmissions) {
        this.questionSubmissions = questionSubmissions;
    }
}
