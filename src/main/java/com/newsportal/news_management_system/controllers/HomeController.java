package com.newsportal.news_management_system.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HomeController {

    @GetMapping("/")
    public String welcome() {
        return "Welcome to the News Portal API!";
    }
}
