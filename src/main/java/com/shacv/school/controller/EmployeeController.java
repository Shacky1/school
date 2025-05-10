package com.shacv.school.controller;

import com.shacv.school.dto.EmployeeDto;
import com.shacv.school.service.CourseMaterialService;
import com.shacv.school.service.DepartmentService;
import com.shacv.school.service.EmployeeService;
import com.shacv.school.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private CourseMaterialService courseMaterialService;
    @Autowired
    private DepartmentService departmentService;
    @GetMapping("/new")
    public String registrationPage(Model model) {
        model.addAttribute("employeeDto", new EmployeeDto());
        model.addAttribute("roles", roleService.getAllRoles());
        model.addAttribute("departments", departmentService.getAllDepartments()); // Add departments to the model
        return "teacher_registration";
    }


    @PostMapping("/save_emp")
    public String saveStudent(@ModelAttribute EmployeeDto employeeDto, Model model) {

        if (employeeDto.getUser() == null) {
            model.addAttribute("error", "User information is missing");
            return "teacher_registration";
        }

        // Password confirmation
        if (!employeeDto.getUser().getPassword().equals(employeeDto.getRepeatPassword())) {
            model.addAttribute("error", "Passwords do not match");
            return "teacher_registration";
        }

        try {
            String registrationNumber = employeeService.saveTeacher(employeeDto);
            model.addAttribute("success", "Employee registration successful. Your registration number is " + registrationNumber);
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: User already registered");

        }

        return "teacher_registration";
    }
}
