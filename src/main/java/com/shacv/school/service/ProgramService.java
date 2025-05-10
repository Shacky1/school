package com.shacv.school.service;

import com.shacv.school.entity.Course;
import com.shacv.school.entity.Department;
import com.shacv.school.entity.Program;
import com.shacv.school.repository.ProgramRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProgramService {

    @Autowired
    private ProgramRepository programRepository;

    // Find a program by its name (existing method)
    public Program findProgram(String name) {
        return programRepository.findByName(name);
    }

    // Find a program by its ID (new method)
    public Program findProgramById(Long id) {
        Optional<Program> program = programRepository.findById(id);
        return program.orElse(null); // Return null if not found or handle as needed
    }

    // Save a program (existing method)
    public void saveProgram(Program program) {

        programRepository.save(program);
    }

    // Get a list of all programs (existing method)
    public List<Program> getAllPrograms() {
        return programRepository.findAll();
    }
    // Fetch courses by department
    public List<Program> getProgramByDepartment(String departmentName) {
        return programRepository.findByDepartmentName(departmentName);
    }
    public List<Program> getProgramByDepartmentWithStudents(String departmentName) {
        return programRepository.findProgramsByDepartmentWithStudents(departmentName);
    }
    public List<Program> getProgramsByDepartment(Department department) {
        return programRepository.findByDepartment(department);
    }

}
