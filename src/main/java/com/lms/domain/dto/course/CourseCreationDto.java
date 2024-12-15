package com.lms.domain.dto.course;

import java.util.List;

public class CourseCreationDto {
    private String courseName;
    private String courseDescription;
    private List<Long> instructors;

    public CourseCreationDto(String name, String description, List<Long> instructors) {
        this.courseName = name;
        this.courseDescription = description;
        this.instructors = instructors;
    }

    public List<Long> getInstructors() {
        return instructors;
    }

    public void setInstructors(List<Long> instructors) {
        this.instructors = instructors;
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
