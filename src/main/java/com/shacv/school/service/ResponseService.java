package com.shacv.school.service;

import com.shacv.school.entity.UserResponse;
import com.shacv.school.repository.UserResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResponseService {
    @Autowired
    private UserResponseRepository responseRepository;

    public void saveResponse(UserResponse response) {
        responseRepository.save(response);
    }

    public int calculateScore(Long quizId, Long userId) {
        List<UserResponse> responses = responseRepository.findByQuizIdAndUserId(quizId, userId);
        return (int) responses.stream().filter(response -> {
            // Add logic to compare selectedAnswer with the correctAnswer
            return true;
        }).count();
    }
}