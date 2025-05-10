package com.shacv.school.service;

import com.shacv.school.entity.Department;
import com.shacv.school.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
    @Autowired
    private DepartmentRepository departmentRepository;

    public Department findDepartment(String name){
        return departmentRepository.findByName(name);

    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
}
