package com.shacv.school.service;

import com.shacv.school.entity.Quiz;
import com.shacv.school.entity.Question;
import com.shacv.school.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QuizService {
    @Autowired
    private QuizRepository quizRepository;

    public List<Quiz> getAllQuizzes() {
        return quizRepository.findAll();
    }
    public List<Quiz> getQuizzesByCourseId(Long courseId) {
        return quizRepository.findByCourseId(courseId);
    }

    public Quiz getQuizById(Long id) {
        return quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
    }

    public void saveQuiz(Quiz quiz) {
        quizRepository.save(quiz);
    }

    public int calculateScore(Long quizId, Map<Long, String> responses) {
        Quiz quiz = getQuizById(quizId);
        int score = 0;

        for (Question question : quiz.getQuestions()) {
            String correctAnswer = question.getCorrectAnswer();
            String userAnswer = responses.get(question.getId());

            if (correctAnswer != null && correctAnswer.equals(userAnswer)) {
                score++;
            }
        }
        return score;
    }
}
