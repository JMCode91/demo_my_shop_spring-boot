package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Model model) {
        // Esto le dice a Spring que busque un archivo llamado "login.html" en la carpeta templates
        return "login";
    }
}