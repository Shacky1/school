package com.shacv.school.controller;

import com.shacv.school.entity.Course;
import com.shacv.school.entity.CourseMaterial;
import com.shacv.school.entity.Employee;
import com.shacv.school.entity.Program;
import com.shacv.school.service.CourseMaterialService;
import com.shacv.school.service.CourseService;
import com.shacv.school.service.EmployeeService;
import com.shacv.school.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private CourseMaterialService courseMaterialService;
    @Autowired
    private ProgramService programService;

    @GetMapping("/coReg")
    public String courseForm(Model model, Principal principal) {
        try {
            // Get the logged-in HOD
            Employee loggedInHod = employeeService.findEmployeeByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Logged-in HOD not found"));

            // Get the programs in the HOD's department
            List<Program> departmentPrograms = programService.getProgramsByDepartment(loggedInHod.getDepartment());

            // Get instructors with role 'TEACHER' in the HOD's department
            List<Employee> departmentInstructors = employeeService.getInstructorsByDepartmentAndRole(loggedInHod.getDepartment().getName(), "TEACHER");

            // Add attributes to the model
            model.addAttribute("courseDto", new Course());
            model.addAttribute("programs", departmentPrograms);
            model.addAttribute("instructors", departmentInstructors);
            return "course-form";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading form: " + e.getMessage());
            return "course-form";
        }
    }



    @PostMapping("/save_co")
    public String saveCourse(@ModelAttribute("courseDto") Course courseDto, Principal principal, Model model) {
        try {
            // Get the logged-in user (HOD) and find their department
            Employee loggedInHod = employeeService.findEmployeeByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Logged-in HOD not found"));

            // Set the course's department to the logged-in HOD's department
            courseDto.setDepartment(loggedInHod.getDepartment());

            // Save the course
            courseService.saveCourse(courseDto);

            model.addAttribute("success", "Course registered successfully");
        } catch (Exception e) {
            model.addAttribute("courseDto", courseDto);
            model.addAttribute("error", "Registration failed: " + e.getMessage());
        }
        return "course-form";
    }

    @GetMapping("/details/{id}")
    public String courseDetails(@PathVariable Long id, Model model) throws Exception {
        Course course = courseService.findById(id);

        List<Employee> employees = employeeService.getAllEmployees();

        model.addAttribute("course", course);
        model.addAttribute("employees", employees); // Populate instructor dropdown
        return "course-details";
    }

    @PostMapping("/update/{id}")
    public String updateCourse(@PathVariable Long id, @RequestParam("name") String name,
                               @RequestParam("employeeId") Long employeeId,
                               @RequestParam("semester") int semester, Model model) {
        try {
            Course course = courseService.findById(id);

            // Find the instructor (employee) by ID
            Employee instructor = employeeService.findEmployeeById(employeeId);

            // Update course details
            course.setName(name);
            course.setEmployee(instructor);
            course.setSemester(semester);

            // Save the updated course
            courseService.saveCourse(course);

            model.addAttribute("success", "Course updated successfully");
        } catch (Exception e) {
            model.addAttribute("error", "Error updating course: " + e.getMessage());
        }
        return "redirect:/course/details/" + id;
    }

    @PostMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id, Model model) {
        try {
            Course course = courseService.findById(id);

            // Handle deletion of associated course materials (but NOT the course itself)
            List<CourseMaterial> courseMaterials = course.getCourseMaterial();
            for (CourseMaterial material : courseMaterials) {
                courseMaterialService.deleteCourseMaterial(material.getId());
            }

            // Delete the course (ensuring department and instructor remain intact)
            courseService.deleteCourse(id);

            model.addAttribute("success", "Course deleted successfully");
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting course: " + e.getMessage());
        }
        return "redirect:/hod/courses";
    }

    @PostMapping("/delete-material/{materialId}")
    public String deleteCourseMaterial(@PathVariable Long materialId, Model model) {
        try {
            // Delete the course material without affecting the course
            courseMaterialService.deleteCourseMaterial(materialId);

            model.addAttribute("success", "Course material deleted successfully");
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting course material: " + e.getMessage());
        }
        return "redirect:/hod/courses"; // Redirect to course list
    }
}
