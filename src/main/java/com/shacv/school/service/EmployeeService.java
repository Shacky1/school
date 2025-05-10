package com.shacv.school.service;

import com.shacv.school.dto.EmployeeDto;
import com.shacv.school.entity.Department;
import com.shacv.school.entity.Employee;
import com.shacv.school.entity.Role;
import com.shacv.school.entity.User;
import com.shacv.school.repository.EmployeeRepository;
import com.shacv.school.util.SchoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EmployeeRepository employeeRepository;

    public String saveTeacher(EmployeeDto employeeDto) {
        // Generate a unique employee ID for the teacher
        String employeeId = SchoolUtil.teacherIdGenerator();

        // Create the user associated with the employee
        User user = User.builder()
                .userName(employeeId)
                .password(passwordEncoder.encode(employeeDto.getUser().getPassword()))
                .build();

        // Create or find the department
        Department department = departmentService.findDepartment(employeeDto.getDepartment().getName());
        if (department == null) {
            department = Department.builder()
                    .name(employeeDto.getDepartment().getName())
                    .build();
        }

        // Set the role for the employee
        String defaultRoleName = "EMPLOYEE";
        Role role = roleService.findRoleByName(employeeDto.getRole().getName());
        if (role == null) {
            // If the role does not exist, create a new one
            role = Role.builder().name(employeeDto.getRole().getName()).build();
            roleService.saveRole(role);
        }

        // Assign the role to the user
        user.setRoles(Collections.singletonList(role));

        // Create the employee entity
        Employee employee = Employee.builder()
                .firstName(employeeDto.getFirstName())
                .secondName(employeeDto.getSecondName())
                .lastName(employeeDto.getLastName())
                .email(employeeDto.getEmail())
                .phoneNumber(employeeDto.getPhoneNumber())
                .salary(employeeDto.getSalary())
                .user(user)
                .department(department)
                .build();

        // Save the employee in the repository
        employeeRepository.save(employee);
        return employeeId;
    }

    // Method to get instructors (teachers) by role and department
    public List<Employee> getInstructorsByRoleAndDepartment(String roleName, String departmentName) {
        // Fetch all employees
        List<Employee> allEmployees = employeeRepository.findAll();

        // Filter by role and department
        return allEmployees.stream()
                .filter(employee -> employee.getUser().getRoles().stream()
                        .anyMatch(role -> role.getName().equalsIgnoreCase(roleName)) &&
                        employee.getDepartment().getName().equalsIgnoreCase(departmentName))
                .collect(Collectors.toList());
    }

    // Fetch department based on HOD's username (this assumes the HOD is an employee too)
    public String getDepartmentByHOD(String hodUsername) {
        Employee hod = employeeRepository.findByUser_UserName(hodUsername);
        if (hod != null) {
            return hod.getDepartment().getName();
        }
        throw new RuntimeException("HOD not found");
    }

    // Get all instructors (for the original method, keeping this unchanged)
    public List<Employee> getAllInstructors() {
        return employeeRepository.findAll();
    }

    // Get the currently logged-in teacher (Mocked for now)
    public Employee getCurrentTeacher() {
        // Ideally, this should return the logged-in teacher’s details
        return employeeRepository.findById(1L).orElseThrow(() -> new RuntimeException("Teacher not found"));
    }

    public Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id).orElse(null);  // Fetching by id
    }

    public void updateTeacher(Employee teacher) {
        employeeRepository.save(teacher);  // Updating the teacher
    }

    public Employee findEmployeeByUserId(String userName) {
        return employeeRepository.findByUser_UserName(userName);  // Assuming the Employee is linked to User by userName
    }

    // New method to find employee by username
    public Optional<Employee> findEmployeeByUsername(String username) {
        return Optional.ofNullable(employeeRepository.findByUser_UserName(username));
    }

    // Method to generate department performance reports
    public String generateDepartmentReport() {
        // Mock data for the report
        return "Department performance report: Courses: 20, Instructors: 10, Students: 200";
    }

    // Placeholder methods for manager-related functionalities (to be implemented)
    public String getManagerName() {
        return null;
    }

    public List<Employee> getAllEmployees() {

        return employeeRepository.findAll();
    }
    // Fetch instructors by role and department
    public List<Employee> getInstructorsByDepartmentAndRole(String departmentName, String roleName) {
        Department department = departmentService.findDepartment(departmentName);
        if (department == null) {
            throw new RuntimeException("Department not found");
        }
        return employeeRepository.findByDepartmentAndUser_Roles_Name(department, roleName);
    }

    public List<String> getReports() {
        return null;
    }

    public String getHODName() {
        return null;
    }

}
