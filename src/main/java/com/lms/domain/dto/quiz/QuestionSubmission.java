package com.lms.domain.dto.quiz;

public class QuestionSubmission {
    private Long questionId;
    private Long choiceId;


    public QuestionSubmission(Long questionId, Long choiceId) {
        this.questionId = questionId;
        this.choiceId = choiceId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getChoiceId() {
        return choiceId;
    }

    public void setChoiceId(Long choiceId) {
        this.choiceId = choiceId;
    }


}
