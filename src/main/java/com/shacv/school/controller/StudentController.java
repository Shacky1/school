package com.shacv.school.controller;

import com.shacv.school.dto.StudentDto;
import com.shacv.school.dto.StudentMarksDto;
import com.shacv.school.entity.*;
import com.shacv.school.service.*;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.shacv.school.entity.CourseMaterial;
import com.shacv.school.service.CourseMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;


import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private CourseMaterialService courseMaterialService;
    @Autowired
    private MarksService marksService;

    @GetMapping("/stud_reg")
    public String registrationPage(Model model) {
        model.addAttribute("studentDto", new StudentDto());
        // Fetch all available programs for the dropdown
        List<Program> programs = programService.getAllPrograms();
        model.addAttribute("programs", programs);
        return "student_registration";
    }

    @PostMapping("/save_stud")
    public String saveStudent(@ModelAttribute StudentDto studentDto, Model model) {
        if (studentDto.getUser() == null || studentDto.getUser().getPassword() == null) {
            model.addAttribute("error", "User information or password is missing.");
            return "student_registration";
        }
        if (!studentDto.getUser().getPassword().equals(studentDto.getRepeatPassword())) {
            model.addAttribute("error", "Passwords do not match.");
            return "student_registration";
        }
        try {
            String registrationNumber = studentService.registerStudent(studentDto);
            model.addAttribute("success", "Student registration successful. Your registration number is " + registrationNumber);
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed. User already registered or another issue occurred!" + e.getMessage());
        }
        return "student_registration";
    }


    @GetMapping("/courses")
    public String studentCourses(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentService.findStudentByUsername(username);

        if (student == null) {
            model.addAttribute("error", "Student not found.");
            return "student_courses";
        }

        Program studentProgram = student.getProgram();
        Integer yearOfStudy = student.getYearOfStudy();

        // Fetch courses for both semesters in the specific year of study
        List<Course> firstSemesterCourses = courseService.getCoursesByProgramYearAndSemester(studentProgram, yearOfStudy, 1);
        List<Course> secondSemesterCourses = courseService.getCoursesByProgramYearAndSemester(studentProgram, yearOfStudy, 2);

        // Combine both semesters' courses into one list
        List<Course> allCourses = new ArrayList<>();
        allCourses.addAll(firstSemesterCourses);
        allCourses.addAll(secondSemesterCourses);

        // Fetch course materials for the courses
        List<CourseMaterial> materials = new ArrayList<>();
        for (Course course : allCourses) {
            List<CourseMaterial> courseMaterials = courseMaterialService.getMaterialsByCourse(course);
            materials.addAll(courseMaterials);
        }

        // Add attributes to the model
        model.addAttribute("student", student);
        model.addAttribute("courses", allCourses);
        model.addAttribute("materials", materials);
        model.addAttribute("yearOfStudy", yearOfStudy);

        return "student_courses";
    }
    @PostMapping("/add-course/{courseId}")
    public String addCourse(@PathVariable Long courseId, Model model, RedirectAttributes redirectAttributes) {
        // Get the authenticated student's username
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Find the student using the username
        Student student = studentService.findStudentByUsername(username);
        if (student == null) {
            redirectAttributes.addFlashAttribute("error", "Student not found.");
            return "redirect:/student/home"; // Redirect to student home
        }

        // Fetch the course by ID
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            redirectAttributes.addFlashAttribute("error", "Course not found.");
            return "redirect:/student/courses"; // Redirect to courses view
        }

        // Check if the student is already enrolled in the course
        if (student.getCourses().contains(course)) {
            redirectAttributes.addFlashAttribute("error", "You are already enrolled in this course.");
            return "redirect:/student/courses"; // Redirect to the courses page
        }

        // Add the course to the student's course list
        student.getCourses().add(course);
        studentService.updateStudent(student); // Update the student in the database

        redirectAttributes.addFlashAttribute("success", "Course registered successfully!");
        return "redirect:/student/courses"; // Redirect to the courses page
    }
    @GetMapping("/registered-courses")
    public String viewRegisteredCourses(Model model) {
        // Get the authenticated student's username
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Find the student using the username
        Student student = studentService.findStudentByUsername(username);

        if (student == null) {
            model.addAttribute("error", "Student not found.");
            return "student_home"; // Redirect to the home page if student not found
        }

        // Fetch the courses the student is enrolled in
        List<Course> registeredCourses = student.getCourses();

        // Add the courses and student details to the model
        model.addAttribute("student", student);
        model.addAttribute("registeredCourses", registeredCourses);

        return "student_registered_courses"; // Return to the registered courses view
    }
    @GetMapping("/results")
    public String viewResults(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Student student = studentService.findStudentByUsername(username);

        if (student == null) {
            model.addAttribute("error", "Student not found.");
            return "student_home";
        }

        List<StudentMarksDto> results = new ArrayList<>();
        List<Course> registeredCourses = student.getCourses();

        for (Course course : registeredCourses) {
            Marks marks = marksService.findByStudentAndCourse(student, course);
            if (marks != null) {
                results.add(new StudentMarksDto(course, marks.getScore(), marks.getGrade()));
            }
        }

        model.addAttribute("results", results);
        return "student_results";
    }


    @GetMapping("/dashboard")
    public String studentDashboard(Model model) {
        // Get the authenticated student's username
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // Find the student using the username
        Student student = studentService.findStudentByUsername(username);
        if (student == null) {
            model.addAttribute("error", "Student not found.");
            return "student_home";
        }
        Integer yearOfStudy = student.getYearOfStudy();
        model.addAttribute("student", student);
        model.addAttribute("yearOfStudy", yearOfStudy);
        return "student-dashboard";
    }

    @GetMapping("/materials")
    public String courseMaterials(Model model) {
        // Get the authenticated student's username
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // Find the student using the username
        Student student = studentService.findStudentByUsername(username);
        if (student == null) {
            model.addAttribute("error", "Student not found.");
            return "student_home";
        }
        // Get the student's program and year of study
        Program studentProgram = student.getProgram();
        Integer yearOfStudy = student.getYearOfStudy();
        // Fetch courses related to the student's program and year of study
        List<Course> courses = courseService.getCoursesByProgramYearAndSemester(studentProgram, yearOfStudy, 1);    
        // Fetch course materials for these courses
        List<CourseMaterial> courseMaterials = courseMaterialService.getMaterialsByCourses(courses);
        // Add the materials and other details to the model
        model.addAttribute("courseMaterials", courseMaterials);
        return "student_materials"; // This should match the name of your HTML template
    }
    // Method to stream media (audio/video)
    @GetMapping("/media/{id}")
    public ResponseEntity<byte[]> streamMedia(@PathVariable Long id) {
        // Fetch the CourseMaterial by ID
        Optional<CourseMaterial> materialOpt = courseMaterialService.getCourseMaterialById(id);
        if (materialOpt.isPresent()) {
            CourseMaterial material = materialOpt.get();

            // Fetch the media content from the database
            byte[] content = material.getContent(); // Content stored in the byte[] field

            // Check if the content exists
            if (content != null && content.length > 0) {
                // Return the media with the appropriate content type and inline viewing
                return ResponseEntity.ok()
                        .contentType(determineMediaType(material.getFileType())) // Dynamically set content type
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "inline; filename=\"" + material.getFileName() + "\"") // Inline content
                        .body(content); // Send the media content
            }
        }

        // Return 404 if the media content doesn't exist
        return ResponseEntity.notFound().build();
    }



    // Helper method to determine media type
    private MediaType determineMediaType(String fileType) {
        if ("video".equals(fileType)) {
            return MediaType.parseMediaType("video/mp4");
        } else if ("audio".equals(fileType)) {
            return MediaType.parseMediaType("audio/mpeg");
        } else {
            return MediaType.APPLICATION_OCTET_STREAM; // For other file types
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) throws Exception {
        CourseMaterial material = courseMaterialService.getCourseMaterialById(id)
                .orElseThrow(() -> new Exception("Course material not found"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + material.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(material.getFileType()))
                .body(material.getContent());
    }

    @GetMapping("/profile")
    public String viewProfile(Model model) {
        // Get the authenticated student's username
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Find the student using the username
        Student student = studentService.findStudentByUsername(username);

        if (student == null) {
            model.addAttribute("error", "Student not found.");
            return "student_home";
        }

        model.addAttribute("student", student);
        return "student_profile"; // This should match the name of your profile HTML template
    }

}
