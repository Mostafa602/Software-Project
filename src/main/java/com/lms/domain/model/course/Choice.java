package com.lms.domain.model.course;

import jakarta.persistence.*;

@Entity
public class Choice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private boolean isTrue;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    public Choice(String content, boolean isTrue, Question question) {
        this.content = content;
        this.isTrue = isTrue;
        this.question = question;
    }
    public Choice() {}

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

    public boolean isTrue() {
        return isTrue;
    }

    public void setTrue(boolean aTrue) {
        isTrue = aTrue;
    }


}
