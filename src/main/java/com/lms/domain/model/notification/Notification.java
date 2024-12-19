package com.lms.domain.model.notification;
import com.lms.domain.model.user.User;
import jakarta.persistence.*;
import org.springframework.context.annotation.Primary;
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @Column(nullable = false)
    private Boolean isRead;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Notification (){ }

    public Notification(String content, Boolean isRead , User user) {
        this.content = content;
        this.isRead = isRead;
        this.user = user;
    }

    public Long getId(){
        return this.id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getContent( ){
        return this.content;
    }

    public void setContent(String content){
        this.content = content;
    }

    public Boolean getRead(){
        return this.isRead;
    }

    public void setRead( boolean isRead){
        this.isRead = isRead;
    }

    public User getUser(){
        return this.user;
    }

    public void setUser ( User user){
        this.user = user;
    }
}