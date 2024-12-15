package com.lms.domain.dto.course;

import org.springframework.core.io.Resource;

public class MaterialTransferDto {
    Resource resource;
    String contentType;
    String name;

    public MaterialTransferDto(Resource resource, String contentType, String name) {
        this.resource = resource;
        this.contentType = contentType;
        this.name = name;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
