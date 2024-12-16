package com.lms.domain.model.user;

import jakarta.persistence.Entity;

@Entity
public class Admin extends User {
    public Admin(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password, Roles.ROLE_ADMIN);
    }
    public Admin() {}
}
