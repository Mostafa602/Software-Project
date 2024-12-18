package com.lms.domain.dto.quiz;

public class ChoiceQuizDto {
    String content;
    Long id;
    public ChoiceQuizDto(Long id, String content) {
        this.content = content;
        this.id = id;
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
}
