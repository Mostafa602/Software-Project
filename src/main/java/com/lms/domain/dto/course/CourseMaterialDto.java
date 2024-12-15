package com.lms.domain.dto.course;

import com.lms.domain.model.course.Material;

public class CourseMaterialDto {
    private String url;
    private String name;
    private String description;
    private Material type;

    public CourseMaterialDto( String name, String description, String url, Material type) {
        this.url = url;
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Material getType() {
        return type;
    }

    public void setType(Material type) {
        this.type = type;
    }
}
