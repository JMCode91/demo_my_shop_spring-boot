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

/**
 * Controlador encargado de la Autenticación y Autorización pública.
 * Gestiona el inicio de sesión y el registro de nuevos clientes en la tienda.
 */
@Controller
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    /**
     * Muestra el formulario de inicio de sesión.
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * Muestra el formulario de registro para un nuevo usuario.
     */
    @GetMapping("/registration")
    public String registerUser(Model model) {
        model.addAttribute("user", new User());
        return "register-user";
    }

    /**
     * Procesa los datos del formulario de registro y crea el usuario en la BD.
     */
    @PostMapping("/new-user")
    public String addUser(@ModelAttribute User user, Model model) throws UserRegistrationException {
        logger.info("Inicio registro de usuario");
        user.setActive(true);
        boolean userAdded = userService.add(user);

        if (!userAdded) {
            logger.error("❌ Fallo en el registro");
            throw new UserRegistrationException("Error al registrar el usuario");
        }

        logger.info("Usuario creado: {}", user);
        return "redirect:/login";
    }

    /**
     * Captura las excepciones específicas de registro y muestra una pantalla de error amigable.
     */
    @ExceptionHandler(UserRegistrationException.class)
    public ModelAndView handleUserRegistrationException(HttpServletRequest request, UserRegistrationException exception) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("message", "No se ha podido registrar el usuario. Por favor contacte con soporte técnico");
        mav.addObject("exception", exception);
        mav.addObject("url", request.getRequestURL());
        mav.setViewName("error");
        return mav;
    }
}