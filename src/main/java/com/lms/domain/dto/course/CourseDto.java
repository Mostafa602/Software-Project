package com.lms.domain.dto.course;

import java.util.List;
import java.util.Set;

public class CourseDto {
    Long id;
    String name;
    String description;
    Set<String> instructors;

    public CourseDto(Long id, String name, String description, Set<String> instructors) {
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

    public Set<String> getInstructors() {
        return instructors;
    }

    public void setInstructors(Set<String> instructors) {
        this.instructors = instructors;
    }
}
