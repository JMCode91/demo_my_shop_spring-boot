package com.example.demo.controller;

import com.example.demo.domain.Product;
import com.example.demo.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@Controller
public class WebController {

    private final Logger logger = LoggerFactory.getLogger(WebController.class);

    private final ProductService productService;

    public WebController(@NonNull ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    @PostMapping("/")
    public String index(Model model) {

        //aqui ponemos el logger
        logger.info("👋 El usuario ha entrado en la Home y vamos a buscar productos...");

        Set<Product> products = productService.findAllVisible();
        model.addAttribute("products", products);
        return "index";
    }

    @RequestMapping("/checkout")
    public String checkout(Model model) {
        return "checkout";
    }
}
