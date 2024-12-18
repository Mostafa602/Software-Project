package com.lms.domain.dto.quiz;

import com.lms.domain.dto.course.ChoiceDto;

import java.util.ArrayList;
import java.util.List;

public class QuestionQuizDto {
    Long id ;
    String content;
    List<ChoiceQuizDto> choices;

    public QuestionQuizDto(Long id, String content, List<ChoiceQuizDto> choices) {
        this.id = id;
        this.content = content;
        this.choices = choices;
    }


    public QuestionQuizDto() {
        this.choices = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ChoiceQuizDto> getChoices() {
        return choices;
    }

    public void setChoices(List<ChoiceQuizDto> choices) {
        this.choices = choices;
    }

    public void addChoice(ChoiceQuizDto choice) {
        this.choices.add(choice);
    }
}
