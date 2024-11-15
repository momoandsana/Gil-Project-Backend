package com.web.gilproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MyController_JHW {

    @GetMapping("my")
    @ResponseBody
    public String myP() {

        return "my route";
    }

}
