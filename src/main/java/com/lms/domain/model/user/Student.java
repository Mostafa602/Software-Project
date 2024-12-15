package com.lms.domain.model.user;

import jakarta.persistence.Entity;

@Entity
public class Student extends User {
    private Float gpa;

    public Student(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password, Roles.ROLE_STUDENT);
        this.gpa = null;
    }


    public Student() {}


    public Float getGpa() {
        return gpa;
    }
    public void setGpa(Float gpa) {
        this.gpa = gpa;
    }




}
