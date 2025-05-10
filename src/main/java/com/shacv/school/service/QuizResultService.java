package com.shacv.school.service;

import com.shacv.school.entity.QuizResult;
import com.shacv.school.repository.QuizResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizResultService {

    @Autowired
    private QuizResultRepository quizResultRepository;

    public void saveQuizResult(QuizResult quizResult) {
        quizResultRepository.save(quizResult);
    }

    public List<QuizResult> getQuizResultsByStudentId(Long studentId) {
        return quizResultRepository.findByStudent_Id(studentId);
    }

    public List<QuizResult> getQuizResultsByQuizId(Long quizId) {
        return quizResultRepository.findByQuizId(quizId);
    }


}
