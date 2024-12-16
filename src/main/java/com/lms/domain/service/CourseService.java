package com.lms.domain.service;

import com.lms.domain.dto.course.*;
import com.lms.domain.dto.user.StudentDto;
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
                         CourseMaterialRepository courseMaterialRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
        this.questionRepository = questionRepository;
        this.courseMaterialRepository = courseMaterialRepository;
    }

    private String getUploadPath(Long courseId) {
        return String.format(uploadPath, courseId);
    }

    @Transactional
    public void enrollStudent(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
        Student student = studentRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + studentId));

        course.enrollStudent(student);
        courseRepository.save(course);
    }

    @Transactional
    public void unenrollStudent(Long courseId, Long studentId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
        Student student = studentRepository.findById(courseId)
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
        CourseProjection courseProjection = courseRepository.findProjectedById(id);
        return new CourseDto(
                courseProjection.getId(),
                courseProjection.getName(),
                courseProjection.getDescription(),
                courseProjection.getInstructorsFullNames()

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
        Optional<Course> course = courseRepository.findById(id);
        if(course.isEmpty()){

            throw new EntityNotFoundException("Course not found with ID: " + id);
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
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));

        return course.getInstructors().contains(instructor);

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

    @Transactional
    public CourseMaterialResponseDto addMaterial(Long courseId, MultipartFile file, Material type){
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            throw new IllegalArgumentException("Course not found");
        }
        Course course = courseOptional.get();
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
        Optional<CourseMaterial> materialOptional = courseMaterialRepository.findById(id);
        if (materialOptional.isEmpty()) {
            throw new IllegalArgumentException("Course Material not found");
        }
        String fileUrl = materialOptional.get().getUrl();
        File file = new File(fileUrl);
        String contentType ;
        try{
            contentType = Files.probeContentType(file.toPath());
            if (contentType == null) {
                contentType = "application/octet-stream";
            }
        }
        catch (Exception e){
            throw new IllegalArgumentException("an error occurred");
        }
        Path path = file.toPath();
        Resource resource;
        try {
            resource = new UrlResource(path.toUri());
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("an error occurred");
        }
        String sysFileName = file.getName();
        String fileName = sysFileName.substring(sysFileName.indexOf("_")+1);
        return new MaterialTransferDto(
                resource, contentType, fileName
        );


    }


}
