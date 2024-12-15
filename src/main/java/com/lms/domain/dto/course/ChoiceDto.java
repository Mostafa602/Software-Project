package com.lms.domain.dto.course;

public class ChoiceDto {
    String content;
    boolean isTrue;
    public ChoiceDto(String content, boolean isTrue) {
        this.content = content;
        this.isTrue = isTrue;
    }

    public boolean isTrue() {
        return isTrue;
    }

    public void setTrue(boolean aTrue) {
        isTrue = aTrue;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
