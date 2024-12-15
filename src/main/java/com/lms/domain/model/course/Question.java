package com.lms.domain.model.course;
import jakarta.persistence.*;


import java.util.Set;

@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "question_bank_id", nullable = false)
    private QuestionBank questionBank;

    @OneToMany( cascade = CascadeType.ALL)
    private Set<Choice> choices;

    public Question(String content, QuestionBank questionBank) {
        this.content = content;
        this.questionBank = questionBank;
    }

    public Question() {}

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

    public QuestionBank getQuestionBank() {
        return questionBank;
    }

    public void setQuestionBank(QuestionBank questionBank) {
        this.questionBank = questionBank;
    }

    public Set<Choice> getChoices() {
        return choices;
    }

    public void setChoices(Set<Choice> choice) {
        this.choices = choice;
    }

    public void addChoice(Choice choice) {
        this.choices.add(choice);
    }
}
