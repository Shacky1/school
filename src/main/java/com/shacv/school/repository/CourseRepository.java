package com.shacv.school.repository;

import com.shacv.school.entity.Course;
import com.shacv.school.entity.Employee;
import com.shacv.school.entity.Program;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course,Long>{
    List<Course> findByEmployee(Employee instructor);
    List<Course> findByDepartmentName(String departmentName);
    List<Course> findByProgramName(String programName);
    List<Course> findByProgramAndSemester(Program program, Integer semester);
    List<Course> findByProgramAndYearAndSemester(Program program, Integer year, Integer semester);

}


