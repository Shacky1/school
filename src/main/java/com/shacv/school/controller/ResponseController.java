package com.shacv.school.controller;

import com.shacv.school.entity.UserResponse;
import com.shacv.school.service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/responses")
public class ResponseController {
    @Autowired
    private ResponseService responseService;

    @PostMapping
    public String submitResponse(@ModelAttribute UserResponse response) {
        responseService.saveResponse(response);
        return "redirect:/quizzes";
    }
}
