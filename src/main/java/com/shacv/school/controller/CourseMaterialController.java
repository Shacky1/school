package com.shacv.school.controller;

import com.shacv.school.entity.Course;
import com.shacv.school.entity.CourseMaterial;
import com.shacv.school.entity.Employee;
import com.shacv.school.service.CourseMaterialService;
import com.shacv.school.service.CourseService;
import com.shacv.school.service.CustomUserDetailsService;
import com.shacv.school.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/course-materials")
public class CourseMaterialController {

    @Autowired
    private CourseMaterialService courseMaterialService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // Display form to create a new course material
    @GetMapping("/new")
    public String showCreateForm(Model model, Principal principal) {
        // Get the currently logged-in teacher's details using Principal
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(principal.getName());

        // Find the logged-in user in the employee service
        Employee loggedInInstructor = employeeService.findEmployeeByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Logged-in instructor not found"));

        // Create a new CourseMaterial object
        CourseMaterial courseMaterial = new CourseMaterial();
        // Set the logged-in instructor as the author of the course material
        courseMaterial.setInstructor(loggedInInstructor);

        // Fetch the courses associated with the logged-in instructor
        List<Course> instructorCourses = courseService.getCoursesByInstructor(loggedInInstructor);

        // Add attributes to the model
        model.addAttribute("courseMaterial", courseMaterial);
        model.addAttribute("courses", instructorCourses); // Show only courses associated with the logged-in instructor
        model.addAttribute("instructors", List.of(loggedInInstructor)); // Only show the logged-in instructor

        return "course-material-form";
    }


    // Handle form submission for creating a new course material with file upload
    @PostMapping("/save")
    public String createCourseMaterial(@ModelAttribute CourseMaterial courseMaterial,
                                       @RequestParam("file") MultipartFile file,
                                       Principal principal, Model model) {
        try {
            Employee loggedInInstructor = employeeService.findEmployeeByUsername(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Logged-in instructor not found"));

            courseMaterial.setInstructor(loggedInInstructor);

            // Attempt to save the course material
            courseMaterialService.createCourseMaterial(courseMaterial, file);
            model.addAttribute("success", "Course material created successfully");
        } catch (Exception e) {
            model.addAttribute("error", "Error creating course material: " + e.getMessage());
            // Optionally, you can log the error for debugging
        }

        return "redirect:/course-materials/list";
    }
    // Download a course material file
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) throws Exception {
        CourseMaterial material = courseMaterialService.getCourseMaterialById(id)
                .orElseThrow(() -> new Exception("Course material not found"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + material.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(material.getFileType()))
                .body(material.getContent());
    }

    // View details of a single course material
    @GetMapping("/{id}")
    public String getCourseMaterialById(@PathVariable Long id, Model model) throws Exception {
        CourseMaterial material = courseMaterialService.getCourseMaterialById(id)
                .orElseThrow(() -> new Exception("Course material not found"));
        model.addAttribute("courseMaterial", material);
        return "course-material-detail";
    }

    // Display form to edit an existing course material
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) throws Exception {
        CourseMaterial material = courseMaterialService.getCourseMaterialById(id)
                .orElseThrow(() -> new Exception("Course material not found"));
        model.addAttribute("courseMaterial", material);
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("instructors", employeeService.getAllInstructors());
        return "course-material-form";
    }

    // Handle form submission for updating an existing course material with optional file update
    @PostMapping("/update/{id}")
    public String updateCourseMaterial(@PathVariable Long id,
                                       @ModelAttribute CourseMaterial courseMaterial,
                                       @RequestParam(value = "file", required = false) MultipartFile file,
                                       Model model) {
        try {
            courseMaterialService.updateCourseMaterial(id, courseMaterial, file);
            model.addAttribute("success", "Course material updated successfully");
        } catch (Exception e) {
            model.addAttribute("error", "Error updating course material: " + e.getMessage());
        }
        return "redirect:/course-materials/list";
    }

    // Delete a course material
    @GetMapping("/delete/{id}")
    public String deleteCourseMaterial(@PathVariable Long id, Model model) {
        try {
            courseMaterialService.deleteCourseMaterial(id);
            model.addAttribute("success", "Course material deleted successfully");
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting course material: " + e.getMessage());
        }
        return "redirect:/course-materials/list";
    }

    // List all course materials
    @GetMapping("/list")
    public String listCourseMaterials(Model model) {
        List<CourseMaterial> materials = courseMaterialService.getAllCourseMaterials();
        model.addAttribute("materials", materials);
        return "course-material-list";
    }
}