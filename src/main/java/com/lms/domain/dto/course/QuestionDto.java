package com.lms.domain.dto.course;

import com.lms.domain.model.course.Choice;

import java.util.Set;

public class QuestionDto {
    private String content;

    private Set<ChoiceDto> choices;

    public QuestionDto(String content, Set<ChoiceDto> choices) {
        this.content = content;
        this.choices = choices;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<ChoiceDto> getChoices() {
        return choices;
    }

    public void setChoices(Set<ChoiceDto> choices) {
        this.choices = choices;
    }

    public void addChoice(ChoiceDto choice) {
        this.choices.add(choice);
    }
}
