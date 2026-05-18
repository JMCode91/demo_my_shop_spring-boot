package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controlador para la gestión interna de Usuarios.
 * Contiene endpoints protegidos para que el Administrador pueda editar o eliminar cuentas.
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/admin/users/delete/{id}")
    public String borrarUsuario(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/admin/panel";
    }

    @GetMapping("/admin/users/edit/{id}")
    public String mostrarFormularioEditarUsuario(@PathVariable("id") Long id, Model model) {
        User usuarioAEditar = userService.findById(id);
        model.addAttribute("user", usuarioAEditar);
        return "register-user";
    }

    @PostMapping("/admin/users/save")
    public String guardarUsuarioEditado(@ModelAttribute User user) {
        userService.update(user);
        return "redirect:/admin/panel";
    }

    /**
     * Manejador global de excepciones genéricas para este controlador.
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(HttpServletRequest request, Exception exception) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("message", exception.getMessage());
        mav.addObject("exception", exception);
        mav.addObject("url", request.getRequestURL());
        mav.setViewName("error");
        return mav;
    }
}