package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.exception.UserRegistrationException;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

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
    public String addUser(@ModelAttribute User user, Model model) throws UserRegistrationException {
        logger.info("inicio registro de usuario");
        boolean userAdded = userService.add(user);


        if (!userAdded) {
            logger.error("❌ Fallo en el registro. El servicio devolvió false para el usuario: {}", user);
            throw new UserRegistrationException("Error al registrar el usuario");
        }

        logger.info("Usuario creado: {}", user);
        model.addAttribute("user", user);
        logger.info("final addUser");
        return "new-user";
    }




    @ExceptionHandler(UserRegistrationException.class)
    public ModelAndView handleUserRegistrationException(HttpServletRequest request, UserRegistrationException exception) {

        ModelAndView mav = new ModelAndView();

        // Aquí definimos el mensaje que saldrá en la pantalla
        mav.addObject("message", "No se ha podido registrar el usuario. Por favor contacte con soporte técnico");
        mav.addObject("exception", exception);
        mav.addObject("url", request.getRequestURL());

        // Aquí le decimos que cargue la página "error.html" (que crearemos luego)
        mav.setViewName("error");

        return mav;
    }

    @ExceptionHandler
    public ModelAndView handleException(HttpServletRequest request, Exception exception) {

        ModelAndView mav = new ModelAndView();
        mav.addObject("message", exception.getMessage());
        mav.addObject("exception", exception);
        mav.addObject("url", request.getRequestURL());
        mav.setViewName("error");
        return mav;
    }


}