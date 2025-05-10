package com.shacv.school.controller;

import com.shacv.school.entity.Announcement;
import com.shacv.school.service.AnnouncementService;
import com.shacv.school.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private EmployeeService managerService;
    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        String managerName = managerService.getManagerName();
        model.addAttribute("managerName", managerName);
        model.addAttribute("announcements", announcementService.getAllAnnouncements());
        model.addAttribute("announcement", new Announcement());
        return "manager-dashboard";
    }

    @GetMapping("/employees")
    public String viewEmployees(Model model) {
        model.addAttribute("employees", managerService.getAllEmployees());
        return "employees-list"; // Create employees-list.html for listing employees
    }

    @GetMapping("/reports")
    public String viewReports(Model model) {
        model.addAttribute("reports", managerService.getReports());
        return "reports-list"; // Create reports-list.html for displaying reports
    }
}
