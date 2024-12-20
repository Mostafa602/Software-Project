package com.lms.domain.model.email;
import com.lms.domain.model.user.Student;
import com.lms.domain.model.user.User;
import jakarta.persistence.*;
import org.springframework.context.annotation.Primary;
@Entity
public class EmailNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String subject;


    @Column
    private  String body;

    @Column
    private Long studentId;


    public EmailNotification (){ }

    public EmailNotification(String subject, String body, Long studentId ) {
        this.subject = subject;
        this.body = body;
        this.studentId = studentId;
    }


    public String getBody(){
        return this.body;
    }

    public void setBody(String body){
        this.body = body;
    }

    public Long getId(){
        return this.id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getSubject( ){
        return this.subject;
    }

    public void setSubject(String content){
        this.subject = content;
    }

    public Long getStudentId(){
        return this.studentId;
    }

    public void setStudentId ( Long studentId){
        this.studentId = studentId;
    }
}