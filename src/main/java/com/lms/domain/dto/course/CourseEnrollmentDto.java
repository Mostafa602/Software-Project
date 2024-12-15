package com.lms.domain.dto.course;

public class CourseEnrollmentDto {
    Long CourseId;
    Long StudentId;

    public CourseEnrollmentDto(Long courseId, Long studentId) {
        CourseId = courseId;
        StudentId = studentId;
    }

    public Long getCourseId() {
        return CourseId;
    }

    public void setCourseId(Long courseId) {
        CourseId = courseId;
    }

    public Long getStudentId() {
        return StudentId;
    }

    public void setStudentId(Long studentId) {
        StudentId = studentId;
    }
}
