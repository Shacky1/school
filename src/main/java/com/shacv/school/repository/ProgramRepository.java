package com.shacv.school.repository;

import com.shacv.school.entity.Department;
import com.shacv.school.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramRepository extends JpaRepository<Program,Long> {
    Program findByName(String name);

    List<Program> findByDepartmentName(String departmentName);
    @Query("SELECT p FROM Program p JOIN FETCH p.students WHERE p.department.name = :departmentName")
    List<Program> findProgramsByDepartmentWithStudents(@Param("departmentName") String departmentName);

    List<Program> findByDepartment(Department department);
}
