package com.lms.domain.dto.quiz;

import java.util.List;

public class QuizCreationDto {
    private String title;
    private Integer numberOfQuestions;

    public QuizCreationDto(String title, int numberOfQuestions) {
        this.title = title;
        this.numberOfQuestions = numberOfQuestions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }
}
