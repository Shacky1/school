package com.shacv.school.controller;


import com.shacv.school.dto.MarksDto;
import com.shacv.school.dto.StudentMarksDto;
import com.shacv.school.entity.*;
import com.shacv.school.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private EmployeeService teacherService;

    @Autowired
    private CourseMaterialService courseMaterialService;

    @Autowired
    private CourseService courseService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private UserService userService;
    @Autowired
    private StudentService studentService;
    @Autowired
    private MarksService marksService;

    @Autowired
    private QuizResultService quizResultService;

    @Autowired
    private QuizService quizService;



    // Teacher dashboard page
    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        // Fetch the logged-in user's details using the principal's username
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(principal.getName());
        User user = userService.findUserByUserName(principal.getName());

        // Fetch the corresponding Employee entity using the User's email/username
        Employee teacher = teacherService.findEmployeeByUserId(user.getUserName());

        // Fetch all course materials created by this teacher
        List<CourseMaterial> courseMaterials = courseMaterialService.getAllCourseMaterialsByTeacher(teacher);

        // Add necessary attributes to the model to display in the dashboard
        model.addAttribute("user", userDetails);
        model.addAttribute("teacher", teacher);  // Employee details
         model.addAttribute("courseMaterials", courseMaterials);

        return "teacher-dashboard";  // This points to the Thymeleaf template 'teacher-dashboard.html'
    }

    // Show teacher profile
    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal) {
        User user = userService.findUserByUserName(principal.getName());
        Employee teacher = teacherService.findEmployeeByUserId(user.getUserName());

        // Add the teacher profile and course materials to the model
        model.addAttribute("teacher", teacher);

        List<CourseMaterial> courseMaterials = courseMaterialService.getAllCourseMaterialsByTeacher(teacher);
        model.addAttribute("courseMaterials", courseMaterials);

        return "teacher-profile";  // Points to the Thymeleaf view 'teacher-profile.html'
    }


    // Manage teacher's courses
    @GetMapping("/courses")
    public String showCourses(Model model, Principal principal) {
        User user = userService.findUserByUserName(principal.getName());
        Employee teacher = teacherService.findEmployeeByUserId(user.getUserName());

        List<Course> teacherCourses = courseService.getCoursesByTeacher(teacher);
        List<CourseMaterial> courseMaterials = courseMaterialService.getAllCourseMaterialsByTeacherCourses(teacherCourses);

        model.addAttribute("teacherCourses", teacherCourses);
        model.addAttribute("courseMaterials", courseMaterials);

        return "teacher-courses";  // Points to a Thymeleaf view 'teacher-courses.html'
    }
    @GetMapping("/courses/students/{courseId}")
    public String viewStudentsInCourse(@PathVariable Long courseId, Model model, Principal principal) throws Exception {
        // Fetch the logged-in user's details
        User user = userService.findUserByUserName(principal.getName());
        Employee teacher = teacherService.findEmployeeByUserId(user.getUserName());

        // Find the course by its ID and ensure the course belongs to the logged-in teacher
        Course course = courseService.findById(courseId);
        if (course == null || !course.getEmployee().getId().equals(teacher.getId())) {
            model.addAttribute("error", "You are not authorized to view this course.");
            return "teacher-courses";
        }

        // Get the students enrolled in this course
        List<Student> students = course.getStudents();

        // Fetch their scores and grades for this course
        List<StudentMarksDto> studentMarksDtoList = new ArrayList<>();
        for (Student student : students) {
            Marks marks = marksService.findByStudentAndCourse(student, course);
            StudentMarksDto dto = new StudentMarksDto(student, marks != null ? marks.getScore() : null, marks != null ? marks.getGrade() : null);
            studentMarksDtoList.add(dto);
        }

        // Add the course and students with their marks to the model
        model.addAttribute("course", course);
        model.addAttribute("studentMarks", studentMarksDtoList);

        return "view-course-students";
    }


    // Show form to edit course material
    @GetMapping("/course-materials/edit/{id}")
    public String editCourseMaterial(@PathVariable Long id, Model model) throws Exception {
        CourseMaterial courseMaterial = courseMaterialService.getCourseMaterialById(id)
                .orElseThrow(() -> new Exception("Course material not found"));
        model.addAttribute("courseMaterial", courseMaterial);
        return "course-material-form";  // Points to 'course-material-form.html' for editing
    }

    // Update course material
    @PostMapping("/course-materials/update/{id}")
    public String updateCourseMaterial(@PathVariable Long id, @ModelAttribute CourseMaterial courseMaterial, Model model) {
        try {
            //courseMaterialService.updateCourseMaterial(id, courseMaterial);
            model.addAttribute("success", "Course material updated successfully");
        } catch (Exception e) {
            model.addAttribute("error", "Error updating course material: " + e.getMessage());
        }
        return "redirect:/teacher/courses";
    }

//    // Delete course material
//    @GetMapping("/course-materials/delete/{id}")
//    public String deleteCourseMaterial(@PathVariable Long id, Model model) {
//        try {
//            courseMaterialService.deleteCourseMaterial(id);
//            model.addAttribute("success", "Course material deleted successfully");
//        } catch (Exception e) {
//            model.addAttribute("error", "Error deleting course material: " + e.getMessage());
//        }
//        return "redirect:/teacher/courses";
//    }
    // View students in a specific course and assign marks
    @GetMapping("/courses/students/{courseId}/assign-marks")
    public String assignMarksForm(@PathVariable Long courseId, Model model, Principal principal) throws Exception {
        // Fetch the logged-in teacher
        User user = userService.findUserByUserName(principal.getName());
        Employee teacher = teacherService.findEmployeeByUserId(user.getUserName());

        // Find the course by ID and ensure it belongs to the logged-in teacher
        Course course = courseService.findById(courseId);
        if (course == null || !course.getEmployee().getId().equals(teacher.getId())) {
            model.addAttribute("error", "You are not authorized to view this course.");
            return "teacher-courses";
        }

        // Get the students enrolled in the course
        List<Student> students = course.getStudents();

        // Add course and students to the model
        model.addAttribute("course", course);
        model.addAttribute("students", students);
        model.addAttribute("marksDto", new MarksDto()); // To capture form data

        return "assign_marks"; // Points to Thymeleaf template for assigning marks
    }

    // Handle the submission of marks
    @PostMapping("/courses/students/{courseId}/assign-marks")
    public String assignMarks(@PathVariable Long courseId, @ModelAttribute MarksDto marksDto, Model model, Principal principal) throws Exception {
        // Fetch the logged-in teacher
        User user = userService.findUserByUserName(principal.getName());
        Employee teacher = teacherService.findEmployeeByUserId(user.getUserName());

        // Find the course and ensure it belongs to the teacher
        Course course = courseService.findById(courseId);
        if (course == null || !course.getEmployee().getId().equals(teacher.getId())) {
            model.addAttribute("error", "You are not authorized to assign marks for this course.");
            return "teacher-courses";
        }

        // Fetch the student and assign the marks
        Student student = studentService.findStudentById(marksDto.getStudentId());
        if (student == null) {
            model.addAttribute("error", "Student not found.");
            return "assign_marks";
        }

        // Create a new Marks entry and save it
        Marks marks = new Marks();
        marks.setStudent(student);
        marks.setCourse(course);
        marks.setScore(marksDto.getScore());
        marks.setGrade(marksDto.getGrade());

        marksService.saveMarks(marks);

        model.addAttribute("success", "Marks assigned successfully.");
        return "redirect:/teacher/courses/students/" + courseId; // Redirect after successful submission
    }
    @GetMapping("/course/{courseId}")
    public String listQuizzesByCourse(@PathVariable Long courseId,Model model) {
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return "error/404";
        }
        model.addAttribute("course", course);
        model.addAttribute("quizzes", quizService.getQuizzesByCourseId(courseId));
        return "quiz/listForteacher";
    }



}
