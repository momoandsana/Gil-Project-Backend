package com.web.gilproject.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController_JHW {

    @GetMapping("/")
    public String mainP() {

        return "Main Controller";
    }
}


