package com.lms.domain.dto.course;

import java.util.List;

public class CourseUpdateDto {
    private String courseName;
    private String courseDescription;

    public CourseUpdateDto(String name, String description) {
        this.courseName = name;
        this.courseDescription = description;
    }


    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public String getDescription() {
        return courseDescription;
    }

    public void setDescription(String description) {
        this.courseDescription = description;
    }

    public String getName() {
        return courseName;
    }

    public void setName(String name) {
        this.courseName = name;
    }

}

