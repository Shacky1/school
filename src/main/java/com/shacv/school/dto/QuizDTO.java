package com.shacv.school.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuizDTO {
    private String title;
    private Long courseId;
    private int duration; // Duration in minutes
    private List<String> questions;
    private List<String> options;
    private List<String> correctAnswers;
}
