package com.lms.domain.dto.course;

public class LessonDto {
    private String name;
    private String description;


    public LessonDto(String name, String Description) {
        this.name = name;
        this.description = Description;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String Lname) {
        this.name = Lname;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String Description) {
        this.description = Description;
    }
}
