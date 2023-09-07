package com.example.investmentportfoliorebalancingtool.controller;

import com.example.investmentportfoliorebalancingtool.domain.User;
import com.example.investmentportfoliorebalancingtool.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class DashboardController {
    private final UserService userService;

    public DashboardController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping({"/dashboard"})
    public String getDashboard(Model model) {
        Optional<User> userOptional = userService.getAuthenticatedUser();
        if(userOptional.isPresent()) {
            User authenticatedUser = userOptional.get();

            model.addAttribute("user", authenticatedUser);
        }
        else {
            throw new RuntimeException("Unable to fetch User Dashboard!");
        }

        return "dashboard";
    }
}


// https://mkyong.com/spring-mvc/spring-mvc-exceptionhandler-example/