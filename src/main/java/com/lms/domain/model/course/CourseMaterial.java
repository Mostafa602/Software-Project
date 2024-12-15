package com.lms.domain.model.course;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class CourseMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    private String name;
//
//    private String description;

    @Column(columnDefinition = "TEXT")
    private String url;

    @Enumerated(EnumType.STRING)
    private Material type;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Temporal(TemporalType.TIMESTAMP)
    private Date uploadedAt;

    public CourseMaterial(String url, Material type, Course course) {
        this.url = url;
        this.type = type;
        this.uploadedAt = new Date();
        this.course = course;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Date getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Date uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public CourseMaterial() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Material getType() {
        return type;
    }

    public void setType(Material type) {
        this.type = type;
    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
}
