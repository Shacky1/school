package com.shacv.school.controller;

import com.shacv.school.dto.QuizDTO;
import com.shacv.school.entity.*;
import com.shacv.school.service.QuizService;
import com.shacv.school.service.CourseService;
import com.shacv.school.service.StudentService;
import com.shacv.school.service.QuizResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/quizzes")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private QuizResultService quizResultService;

    @GetMapping("/course/{courseId}")
    public String listQuizzesByCourse(@PathVariable Long courseId, @RequestParam Long studentId, Model model) {
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return "error/404";
        }
        model.addAttribute("course", course);
        model.addAttribute("quizzes", quizService.getQuizzesByCourseId(courseId));
        model.addAttribute("studentId", studentId);  // Pass studentId
        return "quiz/list";
    }

    @GetMapping("/{id}")
    public String takeQuiz(@PathVariable Long id, @RequestParam Long studentId, Model model) {
        Quiz quiz = quizService.getQuizById(id);
        Student student = studentService.findStudentById(studentId);

        if (quiz == null || student == null) {
            return "error/404";
        }

        model.addAttribute("quiz", quiz);
        model.addAttribute("studentId", studentId);
        return "quiz/take";
    }

    @PostMapping("/submit")
    public String submitQuiz(@RequestParam Long quizId,
                             @RequestParam Map<String, String> responses,
                             @RequestParam Long studentId,
                             Model model) {
        Quiz quiz = quizService.getQuizById(quizId);
        Student student = studentService.findStudentById(studentId);

        if (quiz == null || student == null) {
            return "error/404";
        }

        Map<Long, String> parsedResponses = responses.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("question-"))
                .collect(Collectors.toMap(
                        entry -> Long.parseLong(entry.getKey().split("-")[1]),
                        Map.Entry::getValue
                ));

        int score = quizService.calculateScore(quizId, parsedResponses);

        QuizResult quizResult = QuizResult.builder()
                .student(student)
                .quiz(quiz)
                .score(score)
                .submittedAt(LocalDateTime.now())
                .build();
        quizResultService.saveQuizResult(quizResult);

        return "redirect:/quizzes/result/" + quizId + "?studentId=" + studentId;
    }

    @GetMapping("/result/{quizId}")
    public String showQuizResult(@PathVariable Long quizId, @RequestParam Long studentId, Model model) {
        Quiz quiz = quizService.getQuizById(quizId);
        Student student = studentService.findStudentById(studentId);

        if (quiz == null || student == null) {
            return "error/404";
        }

        List<QuizResult> results = quizResultService.getQuizResultsByStudentId(studentId);
        Optional<QuizResult> latestResult = results.stream()
                .filter(result -> result.getQuiz().getId().equals(quizId))
                .max(Comparator.comparing(QuizResult::getSubmittedAt));

        if (latestResult.isEmpty()) {
            return "error/404";
        }

        model.addAttribute("quiz", quiz);
        model.addAttribute("score", latestResult.get().getScore());
        return "quiz/result";
    }
    // Show form to create a new quiz
    @GetMapping("/new/{courseId}")
    public String newQuizForm(@PathVariable Long courseId, Model model) {
        Course course = courseService.getCourseById(courseId);
        if (course == null) {
            return "error/404";
        }
        model.addAttribute("course", course);
        model.addAttribute("quiz", new Quiz());
        return "quiz/new";
    }
    // Create a new quiz
    @PostMapping("/new")
    public String createQuiz(@ModelAttribute QuizDTO quizDTO) {
        Course course = courseService.getCourseById(quizDTO.getCourseId());
        if (course == null) {
            return "error/404";
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(quizDTO.getTitle());
        quiz.setDuration(quizDTO.getDuration());
        quiz.setCourse(course);

        List<Question> questionList = new ArrayList<>();
        for (int i = 0; i < quizDTO.getQuestions().size(); i++) {
            if (i < quizDTO.getOptions().size() && i < quizDTO.getCorrectAnswers().size()) {
                Question question = new Question();
                question.setContent(quizDTO.getQuestions().get(i));
                question.setCorrectAnswer(quizDTO.getCorrectAnswers().get(i));

                if (quizDTO.getOptions().get(i) != null && !quizDTO.getOptions().get(i).isEmpty()) {
                    List<String> optionList = Arrays.asList(quizDTO.getOptions().get(i).split(","));
                    question.setOptions(optionList);
                }

                question.setQuiz(quiz);
                questionList.add(question);
            }
        }

        quiz.setQuestions(questionList);
        quizService.saveQuiz(quiz);
        return "redirect:/quizzes/course/" + quizDTO.getCourseId();
    }
    @GetMapping("/results/{quizId}")
    public String viewQuizResults(@PathVariable Long quizId, Model model) {
        Quiz quiz = quizService.getQuizById(quizId);

        if (quiz == null) {
            return "error/404";
        }

        List<QuizResult> results = quizResultService.getQuizResultsByQuizId(quizId);

        model.addAttribute("quiz", quiz);
        model.addAttribute("results", results);
        return "quiz/results";  // This will be the Thymeleaf template displaying results
    }

}
