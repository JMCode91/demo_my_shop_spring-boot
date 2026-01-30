package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.exception.UserRegistrationException;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // Muestra el formulario (GET)
    @GetMapping("/registration")
    public String registerUser(Model model) {
        model.addAttribute("user", new User());
        return "register-user";
    }

    // Procesa el formulario (POST)
    @PostMapping("/new-user")
    public String addUser(@ModelAttribute User user, Model model) {
        logger.info("inicio addUser");
        boolean userAdded = userService.add(user);

        if (!userAdded) {
            throw new UserRegistrationException("Error al registrar el usuario");
        }

        logger.info("Usuario creado: " + user);
        model.addAttribute("user", user);
        logger.info("final addUser");
        return "new-user";
    }
}