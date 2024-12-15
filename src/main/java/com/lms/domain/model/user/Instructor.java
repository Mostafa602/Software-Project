package com.lms.domain.model.user;

import jakarta.persistence.Entity;

@Entity
public class Instructor extends User {

    Integer gees ;

    public Instructor(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password, Roles.ROLE_INSTRUCTOR);
        this.gees = 0;
    }

    public Instructor() {}
}
