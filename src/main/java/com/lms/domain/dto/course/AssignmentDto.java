package com.lms.domain.dto.course;

import jakarta.persistence.Column;

import java.util.Date;

public class AssignmentDto {

    private String title;
    private String description;
    private Date dueDate;

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public AssignmentDto( Date dueDate, String description, String title) {
        this.dueDate = dueDate;
        this.description = description;
        this.title = title;
    }



}
