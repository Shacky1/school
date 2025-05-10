package com.shacv.school.controller;

import com.shacv.school.entity.Course;
import com.shacv.school.entity.Employee;
import com.shacv.school.entity.Program;
import com.shacv.school.entity.Student;
import com.shacv.school.service.CourseService;
import com.shacv.school.service.CustomUserDetailsService;
import com.shacv.school.service.EmployeeService;
import com.shacv.school.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/hod")
public class HODController {

    @Autowired
    private EmployeeService hodService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // Display the HOD dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model, Principal principal) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(principal.getName());
        String hodName = hodService.getHODName();  // Assuming there's a method to fetch the HOD's name

        // Fetching data for recently added courses
        List<Course> recentCourses = courseService.getAllCourses();  // Example: Fetch the 5 most recent courses

        model.addAttribute("user", userDetails);
        model.addAttribute("recentCourses", recentCourses);
        return "hod-dashboard";  // Render Thymeleaf template "hod-dashboard.html"
    }

    // Method to manage instructors
    @GetMapping("/instructors")
    public String manageInstructors(Model model, Principal principal) {
        // Get the currently logged-in HOD's details using Principal
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(principal.getName());

        // Assuming there's a method to get the department of the HOD
        String hodDepartment = hodService.getDepartmentByHOD(principal.getName());

        // Fetch all employees who are instructors (have "teacher" role) and belong to the HOD's department
        List<Employee> instructors = hodService.getInstructorsByRoleAndDepartment("teacher", hodDepartment);

        // For each instructor, fetch their assigned courses
        instructors.forEach(instructor -> {
            List<Course> assignedCourses = courseService.getCoursesByInstructor(instructor);
            instructor.setCourses(assignedCourses);
        });

        model.addAttribute("instructors", instructors);
        return "instructors-list";  // Render a page with the list of instructors and their courses
    }

    // Method to manage courses within the HOD's department
    @GetMapping("/courses")
    public String manageCourses(Model model, Principal principal) {
        // Get the currently logged-in HOD's details using Principal
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(principal.getName());

        // Fetch the department of the HOD
        String hodDepartment = hodService.getDepartmentByHOD(principal.getName());

        // Fetch all courses that belong to the HOD's department
        List<Course> departmentCourses = courseService.getCoursesByDepartment(hodDepartment);

        // Add the filtered courses to the model
        model.addAttribute("courses", departmentCourses);

        return "courses-list";  // Render a page with a list of courses
    }

    // Method to manage programs within the HOD's department
    @GetMapping("/programs")
    public String managePrograms(Model model, Principal principal) {
        // Get the currently logged-in HOD's details using Principal
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(principal.getName());

        // Fetch the department of the HOD
        String hodDepartment = hodService.getDepartmentByHOD(principal.getName());

        // Fetch all programs that belong to the HOD's department, along with students
        List<Program> programs = programService.getProgramByDepartmentWithStudents(hodDepartment);

        // For each program, fetch the students associated with it (Eager fetching or manual loading)
        programs.forEach(program -> {
            List<Student> students = program.getStudents();  // Fetch the students associated with each program
            program.setStudents(students);  // This is redundant unless you modify the Program entity fetch type
        });

        // Add programs (with students) to the model
        model.addAttribute("programs", programs);

        return "programs-list";  // Render a page with a list of programs and their students
    }


    // Method to display the add new program form
    @GetMapping("/programs/add")
    public String showAddProgramForm(Model model) {
        Program program = new Program();
        model.addAttribute("program", program);
        return "add-program";  // Render the form to add a new program
    }

    // Method to handle the submission of the new program
    @PostMapping("/programs/add")
    public String addNewProgram(@ModelAttribute Program program, RedirectAttributes redirectAttributes,Principal principal) {
        try {
            // Get the logged-in user (HOD) and find their department
            Employee loggedInHod =hodService.findEmployeeByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Logged-in HOD not found"));

            // Set the course's department to the logged-in HOD's department
            program.setDepartment(loggedInHod.getDepartment());

            programService.saveProgram(program);
            redirectAttributes.addFlashAttribute("success", "Program added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding the program!");
        }
        return "redirect:/hod/programs";  // Redirect to the programs list page
    }

    // Method to generate reports
    @GetMapping("/reports")
    public String generateReports(Model model) {
        // Generate department performance reports
        String reportData = hodService.generateDepartmentReport();
        model.addAttribute("reportData", reportData);
        return "reports";  // Render a reports page
    }
}
