package com.lms.domain.dto.quiz;

public class StudentQuizGrade {
   Long studentId;
   String grade;

    public StudentQuizGrade(Long studentId, String grade) {
        this.studentId = studentId;
        this.grade = grade;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
