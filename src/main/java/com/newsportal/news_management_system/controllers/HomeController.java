package com.newsportal.news_management_system.controllers;

import com.newsportal.news_management_system.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class HomeController {

    @GetMapping("/")
    public String welcome() {
        return "Welcome to the News Portal API!";
    }

    @PostMapping("/t1")
    public String testDefault1(Test test) {
        return "Test successful: " + test.name + ", " + test.email;
    }

    @PostMapping("/t2")
    public String testDefault2(@RequestBody Test test) {
        return "Test successful: " + test.name + ", " + test.email;
    }
    @GetMapping("/{id}")
    public String getUserById(Long id) {
        return id.toString();
    }
}
