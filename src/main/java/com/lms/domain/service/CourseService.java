package com.lms.domain.service;

import com.lms.domain.dto.course.ChoiceDto;
import com.lms.domain.dto.course.CourseCreationDto;
import com.lms.domain.dto.course.CourseDto;
import com.lms.domain.dto.course.QuestionDto;
import com.lms.domain.dto.user.StudentDto;
import com.lms.domain.model.course.Choice;
import com.lms.domain.model.course.Course;
import com.lms.domain.model.course.Question;
import com.lms.domain.model.course.QuestionBank;
import com.lms.domain.model.user.Instructor;
import com.lms.domain.model.user.Student;
import com.lms.domain.projection.CourseProjection;
import com.lms.domain.repository.CourseRepository;
import com.lms.domain.repository.InstructorRepository;
import com.lms.domain.repository.QuestionRepository;
import com.lms.domain.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final QuestionRepository questionRepository;

    public CourseService(CourseRepository courseRepository, StudentRepository studentRepository,
                         InstructorRepository instructorRepository,
                         QuestionRepository questionRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public void enrollStudent(Long courseId, Long studentId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        Optional<Student> studentOptional = studentRepository.findById(studentId);

        if (courseOptional.isEmpty()) {
            throw new IllegalArgumentException("Course not found.");
        }
        if (studentOptional.isEmpty()) {
            throw new IllegalArgumentException("Student not found.");
        }

        Course course = courseOptional.get();
        Student student = studentOptional.get();

        course.enrollStudent(student);
        courseRepository.save(course);
    }

    @Transactional
    public void unenrollStudent(Long courseId, Long studentId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        Optional<Student> studentOptional = studentRepository.findById(studentId);

        if (courseOptional.isEmpty()) {
            throw new IllegalArgumentException("Course not found.");
        }
        if (studentOptional.isEmpty()) {
            throw new IllegalArgumentException("Student not found.");
        }

        Course course = courseOptional.get();
        Student student = studentOptional.get();

        course.unenrollStudent(student);
        courseRepository.save(course);
    }


    public List<CourseDto> getAllCourses(){
        List<CourseProjection> projections = courseRepository.findAllProjectedBy();

        return projections.stream()
                .map(projection -> new CourseDto(
                        projection.getId(),
                        projection.getName(),
                        projection.getDescription(),
                        projection.getInstructorsFullNames()
                ))
                .collect(Collectors.toList());
    }

    public CourseDto getCourseById(Long id){
        CourseProjection courseProjection = courseRepository.findProjectedById(id);
        return new CourseDto(
                courseProjection.getId(),
                courseProjection.getName(),
                courseProjection.getDescription(),
                courseProjection.getInstructorsFullNames()

        );
    }

    public void saveCourse(CourseCreationDto courseCreationDto) {
        Course course = new Course();
        course.setName(courseCreationDto.getName());
        course.setDescription(courseCreationDto.getDescription());
        for(Long instructorId : courseCreationDto.getInstructors()){
            Optional<Instructor> instructorOptional = instructorRepository.findById(instructorId);
            if (instructorOptional.isEmpty()) {
                throw new IllegalArgumentException("Instructor not found - id = "+instructorId);
            }
            course.addInstructor(instructorOptional.get());

        }
        courseRepository.save(course);
    }

    public List<StudentDto> getAllStudents(Long id){
        Optional<Course> course = courseRepository.findById(id);
        if(course.isEmpty()){
            throw new IllegalArgumentException("Course not found");
        }
        List<StudentDto> students = new ArrayList<>();
        for(Student s : course.get().getEnrolledStudents()){
            students.add( new StudentDto(s.getId(), s.getFirstName(),
                    s.getLastName(), s.getEmail(), s.getGpa()
            ));
        }
        return students;
    }

    public Boolean isInstructing(Long instructorId, Long courseId){
        Instructor instructor = instructorRepository.findById(instructorId).get();
        Optional<Course> course = courseRepository.findById(courseId);
        if(course.isEmpty()){
            throw new IllegalArgumentException("Course not found");
        }
        return course.get().getInstructors().contains(instructor);

    }

    @Transactional
    public void addQuestion(Long courseId, QuestionDto questionDto) {

        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            throw new IllegalArgumentException("Course not found");
        }
        Course course = courseOptional.get();
        QuestionBank questionBank = course.getQuestionBank();

        Question question ;
        try{
            question = new Question(
                    questionDto.getContent(),
                    questionBank
            );
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Provide a question");
        }

        Set<Choice> choices = new HashSet<>();
        try {
            for (ChoiceDto choiceDto : questionDto.getChoices()) {
                choices.add(
                        new Choice(choiceDto.getContent(), choiceDto.isTrue(), question)
                );
            }
            question.setChoices(choices);
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Provide Choices");
        }


        questionRepository.save(question);


    }

    public Set<QuestionDto> getQuestions(Long courseId){
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            throw new IllegalArgumentException("Course not found");
        }
        Course course = courseOptional.get();
        QuestionBank questionBank = course.getQuestionBank();

        Set<QuestionDto> questionDtos = new HashSet<>();
        for(Question question : questionBank.getQuestions()){
            Set<ChoiceDto> choices = new HashSet<>();
            for(Choice choice : question.getChoices()){
                choices.add(
                        new ChoiceDto(
                              choice.getContent(),
                              choice.isTrue()
                        )
                );
            }

            questionDtos.add(
                    new QuestionDto(
                            question.getContent(),
                            choices
                    )
            );
        }

        return questionDtos;

    }
}
