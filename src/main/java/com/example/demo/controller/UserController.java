package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.exception.UserRegistrationException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

        // Forzamos que el usuario esté activo al registrarse por primera vez
        user.setActive(true);

        boolean userAdded = userService.add(user);

        if (!userAdded) {
            logger.error("❌ Fallo en el registro");
            throw new UserRegistrationException("Error al registrar el usuario");
        }

        logger.info("Usuario creado: {}", user);

        // EN LUGAR DE: return "new-user";
        // HACEMOS ESTO:


        return "redirect:/login";


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


    //  Añadimos la herramienta para acceder a la base de datos de usuarios
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/admin/users/delete/{id}")
    public String borrarUsuario(@PathVariable("id") Long id) {

        userRepository.deleteById(id);

        return "redirect:/admin/panel";
    }


    @GetMapping("/admin/users/edit/{id}")
    public String mostrarFormularioEditarUsuario(@PathVariable("id") Long id, Model model) {

        // Buscamos al usuario en la base de datos
        User usuarioAEditar = userRepository.findById(id).orElse(null);

        // Lo metemos en el modelo para enviarlo al HTML
        model.addAttribute("user", usuarioAEditar);

        // Reutilizamos la vista de registro
        return "register-user";
    }


    @PostMapping("/admin/users/save")
    public String guardarUsuarioEditado(@ModelAttribute User user) {

        // 1. Buscamos al usuario original en la base de datos para ver sus datos antiguos
        User usuarioExistente = userRepository.findById(user.getId()).orElse(null);

        if (usuarioExistente != null) {
            // 2. Si el admin ha dejado la contraseña en blanco, le volvemos a poner la que ya tenía
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(usuarioExistente.getPassword());
            } else {
                // Si el admin ha escrito algo nuevo, la encriptamos (necesitas el passwordEncoder aquí)
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            // 3. Nos aseguramos de no perder la fecha de creación original
            user.setCreationDate(usuarioExistente.getCreationDate());
        }

        // 4. Ahora sí, guardamos de forma segura
        userRepository.save(user);

        return "redirect:/admin/panel";
    }



}