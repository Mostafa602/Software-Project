package com.lms.domain.service;

import com.lms.domain.dto.course.AssignmentDto;
import com.lms.domain.dto.course.ChoiceDto;
import com.lms.domain.dto.course.QuestionDto;
import com.lms.domain.dto.quiz.*;
import com.lms.domain.execptionhandler.ConflictException;
import com.lms.domain.execptionhandler.UnauthorizedAccessException;
import com.lms.domain.model.course.*;
import com.lms.domain.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizService {
    private final UserService userService;
    QuizRepository quizRepository;
    CourseRepository courseRepository;
    StudentRepository studentRepository;
    QuizSubmissionRepository quizSubmissionRepository;

    public QuizService(QuizRepository quizRepository,
                       CourseRepository courseRepository,
                       StudentRepository studentRepository,
                       QuizSubmissionRepository quizSubmissionRepository, UserService userService) {
        this.quizRepository = quizRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.quizSubmissionRepository = quizSubmissionRepository;
        this.userService = userService;
    }
    public static int[] generateUniqueNumbers(int n, int min, int max) {
        if (n > (max - min + 1)) {
            throw new IllegalArgumentException("Cannot generate " + n +
                    " unique numbers in range " + min + " to " + max);
        }

        Set<Integer> uniqueNumbers = new HashSet<>();
        Random random = new Random();

        while (uniqueNumbers.size() < n) {
            int randomNum = random.nextInt(max - min + 1) + min;
            uniqueNumbers.add(randomNum);
        }

        return uniqueNumbers.stream().mapToInt(Integer::intValue).toArray();
    }

    public void createQuiz(Long courseId, QuizCreationDto quizCreationDto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));

        if(quizCreationDto.getNumberOfQuestions() > course.getQuestionBank().getQuestions().size()){
            throw new ConflictException("There is no enough questions in the question bank");
        }

        Quiz quiz = new Quiz(
            quizCreationDto.getTitle(),
                quizCreationDto.getNumberOfQuestions(),
                course
        );
        quizRepository.save(quiz);
    }

    public QuizInstanceDto getQuiz(Long courseId, Long studentId, Long quizId) {
        Course course = courseRepository.findById(courseId).orElseThrow(()
                -> new EntityNotFoundException("Course not found with ID: " + courseId));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new EntityNotFoundException("Quiz not found with ID: " + quizId));

        List<Question> questions = new ArrayList<>(course.getQuestionBank().getQuestions());

        QuizInstanceDto quizInstanceDto = new QuizInstanceDto();
        int n = quiz.getNumberOfQuestions();

        int[] indices = generateUniqueNumbers(n, 0, questions.size()-1);

        Set<Question> selectedQuestions = new HashSet<>();

        for (int i=0 ; i<n ; i++) {

            Question question = questions.get(indices[i]);
            selectedQuestions.add(question);
            QuestionQuizDto questionQuizDto = new QuestionQuizDto();
            questionQuizDto.setId(question.getId());
            questionQuizDto.setContent(question.getContent());
            for(Choice choice : question.getChoices()) {
                questionQuizDto.addChoice(
                        new ChoiceQuizDto(choice.getId(), choice.getContent())
                );
            }
            quizInstanceDto.addQuestion(questionQuizDto);

        }

        QuizSubmission quizSubmission = new QuizSubmission();
        quizSubmission.setQuestions(selectedQuestions);
        quizSubmission.setQuiz(quiz);
        quizSubmission.setStudent(
                studentRepository.findById(studentId).get());

        Long id = quizSubmissionRepository.save(quizSubmission).getId();
        quizInstanceDto.setSubmissionId(id);
        return quizInstanceDto;
    }

    public HashMap<String, String> submitQuiz(Long courseId, Long qId, Long subId,
                                              Long userId, QuizSubmissionDto quizSubmissionDto) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new EntityNotFoundException("Course not found with ID: " + courseId)
        );
        Quiz quiz = quizRepository.findById(qId).
                orElseThrow(()-> new EntityNotFoundException("Quiz not found with ID: " + qId));
        QuizSubmission quizSubmission = quizSubmissionRepository.findById(subId).
            orElseThrow(()-> new EntityNotFoundException("Quiz Submission not found with ID: " + subId));

        if(quizSubmission.getGrade() != null){
            throw new ConflictException("Quiz is already submitted");
        }

        if(studentRepository.findById(userId).get() != quizSubmission.getStudent()) {
            throw new UnauthorizedAccessException();
        }

        List<QuestionSubmission> questionSubmissions = quizSubmissionDto.getQuestionSubmissions();

        QuestionBank questionBank = course.getQuestionBank();

        Integer grade = 0 ;

        for(QuestionSubmission questionSubmission : questionSubmissions) {
            Question q = questionBank.findQuestionById(questionSubmission.getQuestionId());
            Choice correctChoice = q.getCorrectChoice();

            System.out.println(correctChoice.getId());
            System.out.println(correctChoice.getContent());
            System.out.println(correctChoice.isTrue());

            System.out.println(questionSubmission.getChoiceId());

            if(Objects.equals(correctChoice.getId(), questionSubmission.getChoiceId())) {
                grade++;
            }
        }

        quizSubmission.setGrade(grade);
        quizSubmissionRepository.save(quizSubmission);

        HashMap<String, String> gradeMap = new HashMap<>();
        gradeMap.put("grade", grade + "/" + quiz.getNumberOfQuestions());
        return gradeMap ;

    }

    public HashMap<String, String> getGrade(Long subId, Long userId) {
        QuizSubmission quizSubmission = quizSubmissionRepository.findById(subId)
                .orElseThrow(()-> new EntityNotFoundException("Quiz submission not found with ID: " + subId));

        if(!Objects.equals(userId, quizSubmission.getStudent().getId())){
            throw new UnauthorizedAccessException();
        }

        HashMap<String, String> gradeMap = new HashMap<>();
        gradeMap.put("grade", quizSubmission.getGrade() + "/" + quizSubmission.getQuiz().getNumberOfQuestions());
        return gradeMap ;
    }

    public QuizGradesDto getAllGrades(Long qId) {
        Quiz quiz = quizRepository.findById(qId).orElseThrow(
                () -> new EntityNotFoundException("Quiz not found with ID: " + qId)
        );
        Set<QuizSubmission> submissions = quiz.getStudentsSubmissions();
        QuizGradesDto quizGradesDto = new QuizGradesDto();
        for(QuizSubmission submission : submissions) {
            StudentQuizGrade studentQuizGrade = new StudentQuizGrade(
                    submission.getId(), submission.getGrade()+ "/" + submission.getQuiz().getNumberOfQuestions()
            );
            quizGradesDto.addStudentQuizGrade(studentQuizGrade);
        }
        return quizGradesDto;
    }

}
