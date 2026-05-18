package com.example.demo.controller;

import com.example.demo.domain.Product;
import com.example.demo.domain.User;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Controlador dedicado exclusivamente a las tareas de administración.
 * Todas las rutas de este controlador están protegidas por Spring Security 
 * y requieren que el usuario tenga el rol 'ADMIN'.
 */
@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    /**
     * Muestra el panel de control (Dashboard) del administrador.
     * Carga las listas completas de usuarios y productos para su visualización y gestión.
     * * @param model Modelo para inyectar datos en la vista Thymeleaf.
     * @return El nombre de la plantilla del panel de administración.
     */
    @GetMapping("/admin/panel")
    public String mostrarPanelAdmin(Model model) {
        List<User> listaUsuarios = userService.findAll();
        model.addAttribute("usuarios", listaUsuarios);

        List<Product> listaProductos = productService.findAll();
        model.addAttribute("productos", listaProductos);

        return "admin";
    }
}