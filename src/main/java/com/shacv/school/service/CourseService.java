package com.shacv.school.service;

import com.shacv.school.entity.*;
import com.shacv.school.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ProgramService programService;

    @Lazy
    @Autowired
    private StudentService studentService;

    // Save or update a course
    public void saveCourse(Course courseDto) throws Exception {
        // Find or create the department
        Department department = departmentService.findDepartment(courseDto.getDepartment().getName());
        if (department == null) {
            department = Department.builder()
                    .name(courseDto.getDepartment().getName())
                    .build();
        }

        Program program = programService.findProgram(courseDto.getProgram().getName());
        if (program == null) {
            program = Program.builder()
                    .name(courseDto.getProgram().getName())
                    .department(department)
                    .build();
        }

        // Find the employee by username
        Employee employee = employeeService.findEmployeeByUserId(courseDto.getEmployee().getUser().getUserName());
        if (employee == null) {
            throw new Exception("Employee not found");
        }

        // Ensure that the employee belongs to the same department as the course
        Long employeeDepartmentId = employee.getDepartment().getId();
        Long courseDepartmentId = department.getId();

        if (!employeeDepartmentId.equals(courseDepartmentId)) {
            throw new Exception("The employee does not belong to the selected department");
        }

        // Create or update the course with the validated employee and department
        Course course = Course.builder()
                .name(courseDto.getName())
                .semester(courseDto.getSemester())
                .credit(courseDto.getCredit())
                .description(courseDto.getDescription())
                .program(program)
                .year(courseDto.getYear())
                .department(department)
                .employee(employee)
                .build();

        courseRepository.save(course);
    }

    // Get all courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // Get courses assigned to a specific instructor
    public List<Course> getCoursesByInstructor(Employee instructor) {
        return courseRepository.findByEmployee(instructor);
    }

    // Get courses assigned to a teacher
    public List<Course> getCoursesByTeacher(Employee teacher) {
        return courseRepository.findByEmployee(teacher);
    }

    // Fetch courses by department
    public List<Course> getCoursesByDepartment(String departmentName) {
        return courseRepository.findByDepartmentName(departmentName);
    }

    // Fetch courses by program
    public List<Course> getCoursesByProgram(String programName) {
        return courseRepository.findByProgramName(programName);
    }

    // Modify findById method to return Course directly
    public Course findById(Long id) throws Exception {
        Optional<Course> courseOptional = courseRepository.findById(id);
        if (courseOptional.isEmpty()) {
            throw new Exception("Course not found");
        }
        return courseOptional.get();
    }

    // Delete course
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }

    // Fetch courses by program and year of study
    public List<Course> getCoursesByProgramAndYear(Program program, Integer yearOfStudy) {
        return courseRepository.findByProgramAndSemester(program, yearOfStudy);
    }

    // Fetch courses by program, year, and semester
    public List<Course> getCoursesByProgramYearAndSemester(Program program, Integer year, Integer semester) {
        return courseRepository.findByProgramAndYearAndSemester(program, year, semester);
    }

    // Fetch students for a course
    public List<User> getStudentsForCourse(Long courseId) {
        return studentService.findStudentsByCourseId(courseId);
    }

    // Fetch course by its ID
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId).orElse(null);
    }
}
