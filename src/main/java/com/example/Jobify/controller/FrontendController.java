package com.example.Jobify.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {
    @GetMapping({"/", "/login", "/register"})
    public String forwardToReactApp() {
        return "forward:/index.html";
    }

    @GetMapping("/{path:[^\\.]*}")
    public String forwardToReactAppWithPath() {
        return "forward:/index.html";
    }
}
