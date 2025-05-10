package com.shacv.school.repository;

import com.shacv.school.entity.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    // Correct method to fetch QuizResults for a given Student's ID
    List<QuizResult> findByStudent_Id(Long studentId);

    // Corrected query to fetch quiz results for a specific course
    List<QuizResult> findByQuiz_Course_Id(Long courseId);

    List<QuizResult> findByQuizId(Long quizId);

}
