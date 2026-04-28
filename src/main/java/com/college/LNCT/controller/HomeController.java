package com.college.LNCT.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/api")
    public String home() {
        return "Welcome to FineArts Backend API. Server is running successfully.";
    }
}