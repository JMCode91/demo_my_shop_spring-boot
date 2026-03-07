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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    // ¡NUESTRO ÚNICO TRABAJADOR AQUÍ!
    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String registerUser(Model model) {
        model.addAttribute("user", new User());
        return "register-user";
    }

    @PostMapping("/new-user")
    public String addUser(@ModelAttribute User user, Model model) throws UserRegistrationException {
        logger.info("inicio registro de usuario");
        user.setActive(true);
        boolean userAdded = userService.add(user);

        if (!userAdded) {
            logger.error("❌ Fallo en el registro");
            throw new UserRegistrationException("Error al registrar el usuario");
        }

        logger.info("Usuario creado: {}", user);
        return "redirect:/login";
    }

    @ExceptionHandler(UserRegistrationException.class)
    public ModelAndView handleUserRegistrationException(HttpServletRequest request, UserRegistrationException exception) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("message", "No se ha podido registrar el usuario. Por favor contacte con soporte técnico");
        mav.addObject("exception", exception);
        mav.addObject("url", request.getRequestURL());
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

    @PostMapping("/admin/users/delete/{id}")
    public String borrarUsuario(@PathVariable("id") Long id) {
        // El camarero solo pide que borren
        userService.deleteById(id);
        return "redirect:/admin/panel";
    }

    @GetMapping("/admin/users/edit/{id}")
    public String mostrarFormularioEditarUsuario(@PathVariable("id") Long id, Model model) {
        // El camarero solo pide el usuario
        User usuarioAEditar = userService.findById(id);
        model.addAttribute("user", usuarioAEditar);
        return "register-user";
    }

    @PostMapping("/admin/users/save")
    public String guardarUsuarioEditado(@ModelAttribute User user) {
        // ¡Mira qué limpio! El camarero le pasa el usuario modificado al cocinero y se olvida.
        userService.update(user);
        return "redirect:/admin/panel";
    }
}