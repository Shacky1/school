package com.shacv.school.service;

import com.shacv.school.dto.StudentDto;
import com.shacv.school.entity.*;
import com.shacv.school.repository.StudentRepository;
import com.shacv.school.util.SchoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProgramService programService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private QuizResultService quizResultService; // Add QuizResultService dependency

    @Autowired
    private QuizService quizService;

    public String registerStudent(StudentDto studentDto) {
        // Generate a unique registration number
        String registrationNumber = SchoolUtil.registrationNumberGenerator();

        // Create a new User instance from the DTO
        User user = User.builder()
                .userName(registrationNumber)
                .password(passwordEncoder.encode(studentDto.getUser().getPassword()))
                .build();

        // Set default role as "STUDENT"
        String defaultRoleName = "STUDENT";
        Role role = roleService.findRoleByName(defaultRoleName);
        if (role == null) {
            role = Role.builder().name(defaultRoleName).build();
            roleService.saveRole(role);
        }
        user.setRoles(Collections.singletonList(role));

        // Fetch the program using the programId
        Program program = programService.findProgramById(studentDto.getProgramId());
        if (program == null) {
            throw new IllegalArgumentException("Invalid program ID");
        }

        // Set enrollment date to the current date (or modify as needed)
        LocalDate enrollmentDate = LocalDate.now(); // Current date

        // Create Student, including the program and enrollment date
        Student student = Student.builder()
                .firstName(studentDto.getFirstName())
                .middleName(studentDto.getMiddleName())
                .lastName(studentDto.getLastName())
                .email(studentDto.getEmail())
                .phoneNumber(studentDto.getPhoneNumber())
                .enrollmentDate(enrollmentDate) // Set the enrollment date
                .user(user)
                .program(program)
                .build();

        studentRepository.save(student);

        return registrationNumber;
    }

    public void saveQuizResult(Long studentId, Long quizId, int score) {
        // Fetch the student and quiz
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new IllegalArgumentException("Student not found"));
        Quiz quiz = quizService.getQuizById(quizId); // Assuming you have a quiz service to fetch quizzes

        // Create and save the QuizResult
        QuizResult quizResult = new QuizResult();
        quizResult.setStudent(student);
        quizResult.setQuiz(quiz);
        quizResult.setScore(score);
        quizResult.setSubmittedAt(LocalDateTime.now());
        quizResultService.saveQuizResult(quizResult);
    }

    public Student findStudentByUsername(String username) {
        return studentRepository.findByUser_UserName(username);
    }

    public List<User> findStudentsByCourseId(Long courseId) {
        return studentRepository.findByCourses_Id(courseId);
    }

    public void updateStudent(Student student) {
        studentRepository.save(student);
    }

    public Student findStudentById(Long studentId) {
        return studentRepository.findById(studentId).orElse(null);
    }

}
