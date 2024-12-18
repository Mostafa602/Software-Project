package com.lms.domain.dto.quiz;

import java.util.ArrayList;
import java.util.List;

public class QuizGradesDto {
    List<StudentQuizGrade> studentQuizGrades;
    public QuizGradesDto() {
        studentQuizGrades = new ArrayList<>();
    }
    public List<StudentQuizGrade> getStudentQuizGrades() {
        return studentQuizGrades;
    }
    public void setStudentQuizGrades(List<StudentQuizGrade> studentQuizGrades) {
        this.studentQuizGrades = studentQuizGrades;
    }
    public void addStudentQuizGrade(StudentQuizGrade studentQuizGrade) {
        studentQuizGrades.add(studentQuizGrade);
    }
}
