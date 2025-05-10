package com.shacv.school.repository;

import com.shacv.school.entity.Course;
import com.shacv.school.entity.Marks;
import com.shacv.school.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarksRepository extends JpaRepository<Marks, Long> {

        Marks findByStudentAndCourse(Student student, Course course);


}
