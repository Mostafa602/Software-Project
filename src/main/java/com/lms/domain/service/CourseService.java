package com.lms.domain.service;

import com.lms.domain.dto.course.*;
import com.lms.domain.dto.user.StudentDto;
import com.lms.domain.execptionhandler.InternalServerException;
import com.lms.domain.execptionhandler.MissingFieldsException;
import com.lms.domain.model.course.*;
import com.lms.domain.model.user.Instructor;
import com.lms.domain.model.user.Student;
import com.lms.domain.projection.CourseProjection;
import com.lms.domain.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CourseService {
    
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final QuestionRepository questionRepository;
    private final CourseMaterialRepository courseMaterialRepository;
    private final String uploadPath = "uploads/%d";

    public CourseService(CourseRepository courseRepository,
                         StudentRepository studentRepository,
                         InstructorRepository instructorRepository,
                         QuestionRepository questionRepository,
                         CourseMaterialRepository courseMaterialRepository,
                         LessonRepository lessonRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
        this.questionRepository = questionRepository;
        this.courseMaterialRepository = courseMaterialRepository;
        this.lessonRepository = lessonRepository;
    }

    private String getUploadPath(Long courseId) {
        return String.format(uploadPath, courseId);
    }

    @Transactional
    public void enrollStudent(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + studentId));

        course.enrollStudent(student);
        courseRepository.save(course);
    }

    @Transactional
    public void unenrollStudent(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + studentId));


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
        Course course = courseRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Course not found with ID: " + id)
        );
        return new CourseDto(
                course.getId(),
                course.getName(),
                course.getDescription(),
                course.getInstructorsFullNames()
        );
    }

    public void deleteCourse(Long id){
        if(!courseRepository.existsById(id)){
            throw new EntityNotFoundException("Course not found with ID: " + id);
        }
        courseRepository.deleteById(id);

    }

    public void saveCourse(CourseCreationDto courseCreationDto) {
        Course course = new Course();
        course.setName(courseCreationDto.getName());
        course.setDescription(courseCreationDto.getDescription());
        for(Long instructorId : courseCreationDto.getInstructors()){
            Optional<Instructor> instructorOptional = instructorRepository.findById(instructorId);
            if (instructorOptional.isEmpty()) {
                throw new EntityNotFoundException("Instructor not found with ID: " + instructorId);
            }
            course.addInstructor(instructorOptional.get());

        }
        courseRepository.save(course);
    }


    public void updateCourse(Long id, CourseUpdateDto courseUpdateDto) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + id));

        if(courseUpdateDto.getName()!=null) existingCourse.setName(courseUpdateDto.getName());
        if(courseUpdateDto.getDescription()!=null) existingCourse.setDescription(courseUpdateDto.getDescription());

        courseRepository.save(existingCourse);

    }

    public List<StudentDto> getAllStudents(Long id){
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + id));

        List<StudentDto> students = new ArrayList<>();
        for(Student s : course.getEnrolledStudents()){
            students.add( new StudentDto(s.getId(), s.getFirstName(),
                    s.getLastName(), s.getEmail(), s.getGpa()
            ));
        }
        return students;
    }

    public Boolean isInstructing(Long instructorId, Long courseId){
        Instructor instructor = instructorRepository.findById(instructorId).get();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
        return course.getInstructors().contains(instructor);

    }

    public Boolean isEnrolled(Long studentId, Long courseId){
        Student student = studentRepository.findById(studentId).get();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
        return course.getEnrolledStudents().contains(student);

    }

    @Transactional
    public void addQuestion(Long courseId, QuestionDto questionDto) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));

        QuestionBank questionBank = course.getQuestionBank();
        Question question ;
        try{
            question = new Question(
                    questionDto.getContent(),
                    questionBank
            );
        }
        catch (Exception e) {
            throw new MissingFieldsException("Provide a question");
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
            throw new MissingFieldsException("Provide Choices");
        }


        questionRepository.save(question);


    }

    public QuestionDto getQuestion(Long questionId){
        Question question = questionRepository.findById(questionId).orElseThrow(
                () -> new EntityNotFoundException("Question not found with ID: " + questionId)
        );
        Set<ChoiceDto> choices = new HashSet<>();
        for(Choice choice : question.getChoices()){
            choices.add(new ChoiceDto(
                    choice.getContent(),
                    choice.isTrue()
            ));
        }
        return new QuestionDto(question.getContent(), choices);
    }

    public Set<QuestionDto> getQuestions(Long courseId){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));

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

    public void deleteQuestion(Long questionId){
        if(questionRepository.existsById(questionId)){
           questionRepository.deleteById(questionId);
        }
        else
            throw new EntityNotFoundException("Question not found with ID: " + questionId);
    }

    @Transactional
    public CourseMaterialResponseDto addMaterial(Long courseId, MultipartFile file, Material type){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        Path uploadPath = Paths.get(getUploadPath(course.getId()));

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path targetPath = uploadPath.resolve(fileName);
            file.transferTo(targetPath);
            CourseMaterial material = new CourseMaterial(
                    targetPath.toString(), type, course
            );
            material = courseMaterialRepository.save(material);
            return new CourseMaterialResponseDto(
                    material.getId(), targetPath.toString()
            );
        }
        catch (Exception e){
            throw new IllegalArgumentException(e.getMessage());
        }


    }

    public MaterialTransferDto getMaterial(Long id){
        CourseMaterial courseMaterial = courseMaterialRepository.findById(id).orElseThrow(
                ()-> new EntityNotFoundException("Material not found with ID: " + id)
        );
        String fileUrl = courseMaterial.getUrl();
        File file = new File(fileUrl);
        String contentType ;

        try{
            contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

        }
        catch (Exception e){
            throw new InternalServerException();
        }
        Path path = file.toPath();
        Resource resource;

        try {
            resource = new UrlResource(path.toUri());
        }
        catch (MalformedURLException e) {
            throw new InternalServerException();
        }
        String sysFileName = file.getName();
        String fileName = sysFileName.substring(sysFileName.indexOf("_")+1);
        return new MaterialTransferDto(
                resource, contentType, fileName
        );


    }
    public void addingLesson(Long c_id,LessonDto lesson){
        
        Lesson lesson2 = new Lesson();
        // to set the otp of the specific lesson of this course.
        // for adding lesson to the array of lesson in course class.
        // saving our lesson
        Course course = courseRepository.findById(c_id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + c_id));
        // Generate a random four-digit number
        long randomFourDigits = (long)(Math.random() * 9000) + 1000;
        lesson2.setOtp(randomFourDigits);// to set the otp of the specific lesson of this course.
        lesson2.setCourse(course);
        lesson2.setDescription(lesson.getDescription());
        lesson2.setName(lesson.getName());
        lessonRepository.save(lesson2);// saving our lesson
    }

    public LessonDto getLesson(Long otp,Long c_id){
        Course course = courseRepository.findById(c_id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + c_id));

        Lesson lesson = lessonRepository.findLessonByotp(otp);// Tyring to find the lesson of this specific course by its own otp.
        LessonDto lesson2 = new LessonDto(lesson.getName(),lesson.getDescription());
        return lesson2;
    }


}
