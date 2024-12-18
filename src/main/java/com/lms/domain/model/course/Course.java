package com.lms.domain.model.course;

import com.lms.domain.execptionhandler.ConflictException;
import com.lms.domain.model.user.Instructor;
import com.lms.domain.model.user.Student;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private int enrolledNum;

    @ManyToMany
    @JoinTable(
            name = "Enrollement",
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private Set<Student> enrolledStudents;

    @ManyToMany
    @JoinTable(
            name = "Teaching",
            inverseJoinColumns = @JoinColumn(name = "instructor_id")
    )
    private Set<Instructor> instructors;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "question_bank_id")
    private QuestionBank questionBank;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private Set<CourseMaterial> courseMaterials;

    public List<Assignment> getAssignmentList() {
        return assignmentList;
    }

    public void setAssignmentList(List<Assignment> assignmentList) {
        this.assignmentList = assignmentList;
    }

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Assignment> assignmentList;

    public Course(String name, String description, Instructor instructor) {
        this.name = name;
        this.description = description;
        this.enrolledNum = 0;
        this.instructors = new HashSet<>();
        this.instructors.add(instructor);
        this.questionBank = new QuestionBank();
        this.courseMaterials = new HashSet<>();
        this.assignmentList = new ArrayList<>();
    }

    public Course() {
        this.instructors = new HashSet<>();
        this.courseMaterials = new HashSet<>();
        this.questionBank = new QuestionBank();
        this.assignmentList = new ArrayList<>();
    }

    public Set<String> getInstructorsFullNames() {
        Set<String> fullNames = new HashSet<>();
        for(Instructor instructor : instructors) {
            fullNames.add(instructor.getFullName());
        }
        return fullNames;
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

    public int getEnrolledNum() {
        return enrolledNum;
    }


    public Set<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void setEnrolledStudents(Set<Student> enrolledStudents) {
        this.enrolledStudents = enrolledStudents;
    }

    public Set<Instructor> getInstructors() {
        return instructors;
    }

    public void setInstructors(Set<Instructor> instructor) {
        this.instructors = instructor;
    }
    public void addInstructor(Instructor instructor) {
        this.instructors.add(instructor);
    }
    public void enrollStudent(Student student) {
        if (enrolledStudents == null) {
            enrolledStudents = new HashSet<>();
        }
        if (!enrolledStudents.contains(student)) {
            enrolledStudents.add(student);
            this.enrolledNum++;
        }
        else {
            throw new ConflictException("Student is already enrolled in this course.");
        }
    }
    public void unenrollStudent(Student student) {
        if (enrolledStudents == null || !enrolledStudents.contains(student)) {
            throw new ConflictException("Student is not enrolled in this course.");
        }
        else {
            enrolledStudents.remove(student);
            this.enrolledNum--;
        }
    }

    public QuestionBank getQuestionBank() {
        return questionBank;
    }

    public void setQuestionBank(QuestionBank questionBank) {
        this.questionBank = questionBank;
    }


}
