package com.example.demo.controller;

import com.example.demo.domain.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador principal encargado de gestionar la página de inicio (Home)
 * y todas las páginas de información estática (Aviso Legal, Contacto, etc.).
 */
@Controller
public class HomeController {

    @Autowired
    private ProductService productService;

    /**
     * Mapea la ruta raíz ("/") a la página principal de la tienda.
     * Carga el catálogo visible y selecciona los 8 productos con mayor descuento 
     * para mostrarlos en el escaparate inicial.
     * * @param model Modelo para inyectar datos en la vista Thymeleaf.
     * @return El nombre de la plantilla de la página de inicio.
     */
    @GetMapping("/")
    public String index(Model model) {
        List<Product> todosVisible = new ArrayList<>(productService.findAllVisible());
        
        List<Product> destacados = todosVisible.stream()
                .sorted((p1, p2) -> Float.compare(p2.getDiscount(), p1.getDiscount())) 
                .limit(8)
                .collect(Collectors.toList());

        model.addAttribute("products", destacados);
        return "index";
    }

    // ==========================================
    // PÁGINAS LEGALES Y DE INFORMACIÓN
    // ==========================================
    
    /**
     * Muestra la página de Aviso Legal.
     */
    @GetMapping("/aviso-legal")
    public String showAvisoLegal(Model model) {
        model.addAttribute("pageTitle", "Aviso Legal");
        return "aviso-legal";
    }

    /**
     * Muestra la Política de Privacidad.
     */
    @GetMapping("/privacidad")
    public String showPrivacidad(Model model) {
        model.addAttribute("pageTitle", "Política de Privacidad");
        return "privacidad";
    }

    /**
     * Muestra la página de Contacto de la empresa.
     */
    @GetMapping("/contacto")
    public String showContacto(Model model) {
        model.addAttribute("pageTitle", "Contacto");
        return "contacto";
    }

    /**
     * Muestra el Centro de Ayuda y Preguntas Frecuentes.
     */
    @GetMapping("/centro-ayuda")
    public String showCentroAyuda(Model model) {
        model.addAttribute("pageTitle", "Centro de Ayuda");
        return "centro-ayuda";
    }

    /**
     * Muestra las políticas de Devoluciones de la tienda.
     */
    @GetMapping("/devoluciones")
    public String showDevoluciones(Model model) {
        model.addAttribute("pageTitle", "Devoluciones");
        return "devoluciones";
    }

    /**
     * Muestra la información sobre Envíos y tiempos de entrega.
     */
    @GetMapping("/envios")
    public String showEnvios(Model model) {
        model.addAttribute("pageTitle", "Envíos y Entregas");
        return "envios";
    }

    /**
     * Muestra las Condiciones Generales de Compra.
     */
    @GetMapping("/condiciones-compra")
    public String showCondicionesCompra(Model model) {
        model.addAttribute("pageTitle", "Condiciones de Compra");
        return "condiciones-compra";
    }

    /**
     * Muestra la Política de Cookies.
     */
    @GetMapping("/cookies")
    public String showCookies(Model model) {
        model.addAttribute("pageTitle", "Política de Cookies");
        return "cookies";
    }
}