package com.example.demo.controller;

import com.example.demo.domain.Product;
import com.example.demo.service.ProductService;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@Controller
public class WebController {

    private final ProductService productService;

    public WebController(@NonNull ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    @PostMapping("/")
    public String index(Model model) {
        Set<Product> products = productService.findAllVisible();
        model.addAttribute("products", products);
        return "index";
    }

    @RequestMapping("/checkout")
    public String checkout(Model model) {
        return "checkout";
    }
}
