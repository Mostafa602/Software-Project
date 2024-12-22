package com.lms.domain.model.user;

import jakarta.persistence.Entity;

@Entity
public class Instructor extends User {


    public Instructor(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password, Roles.ROLE_INSTRUCTOR);
    }

    public Instructor() {}
}
