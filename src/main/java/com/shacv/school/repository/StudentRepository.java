package com.shacv.school.repository;

import com.shacv.school.entity.QuizResult;
import com.shacv.school.entity.Student;
import com.shacv.school.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Student findByUser_UserName(String username);

    List<User> findByCourses_Id(Long courseId);
}
