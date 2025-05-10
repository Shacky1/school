package com.shacv.school.controller;

import com.shacv.school.entity.Announcement;
import com.shacv.school.service.AnnouncementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping("/manager/announcements")
    public String showAnnouncements(Model model) {
        model.addAttribute("announcements", announcementService.getAllAnnouncements());
        model.addAttribute("announcement", new Announcement());
        return "announcements";
    }

    @PostMapping("/manager/announcements")
    public String createAnnouncement(@ModelAttribute Announcement announcement) {
        announcementService.saveAnnouncement(announcement);
        return "redirect:/manager/announcements";
    }
}