package com.shacv.school.repository;

import com.shacv.school.entity.Course;
import com.shacv.school.entity.CourseMaterial;
import com.shacv.school.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseMaterialRepository extends JpaRepository<CourseMaterial, Long> {
    List<CourseMaterial> findByInstructor(Employee instructor);

    // New method to find course materials for a list of courses
    List<CourseMaterial> findByCourseIn(List<Course> courses);

    List<CourseMaterial> findByCourse(Course course);
}
