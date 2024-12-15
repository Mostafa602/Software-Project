package com.lms.domain.dto.course;

import java.util.List;

public class CourseDto {
    Long id;
    String name;
    String description;
    List<String> instructors;

    public CourseDto(Long id, String name, String description, List<String> instructors) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.instructors = instructors;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getInstructors() {
        return instructors;
    }

    public void setInstructors(List<String> instructors) {
        this.instructors = instructors;
    }
}
