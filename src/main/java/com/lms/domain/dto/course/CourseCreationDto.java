package com.lms.domain.dto.course;

import java.util.List;

public class CourseCreationDto extends CourseUpdateDto {
    private List<Long> instructors;

    public CourseCreationDto(String name, String description, List<Long> instructors) {
        super(name, description);
        this.instructors = instructors;
    }

    public List<Long> getInstructors() {
        return instructors;
    }

    public void setInstructors(List<Long> instructors) {
        this.instructors = instructors;
    }

}
