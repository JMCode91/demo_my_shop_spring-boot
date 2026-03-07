package com.example.demo.controller;

import com.example.demo.domain.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class ProductController {

    // ¡SOLO EL SERVICIO! Arquitectura limpia
    @Autowired
    private ProductService productService;

    @GetMapping("/admin/products/new")
    public String formularioNuevoProducto(Model model) {
        model.addAttribute("product", new Product());
        return "new-product";
    }

    @PostMapping("/admin/products/save")
    public String guardarProducto(Product product, @RequestParam("imageFile") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            try {
                // Lógica de guardado de imagen
                Path directorioImagenes = Paths.get("uploads");
                if (!Files.exists(directorioImagenes)) {
                    Files.createDirectories(directorioImagenes);
                }
                String nombreArchivo = imageFile.getOriginalFilename();
                Path rutaCompleta = directorioImagenes.resolve(nombreArchivo);
                Files.write(rutaCompleta, imageFile.getBytes());
                product.setImage(nombreArchivo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Este ya lo tenías bien usando el Service
        productService.save(product);
        return "redirect:/admin/panel";
    }

    @PostMapping("/admin/products/delete/{id}")
    public String borrarProducto(@PathVariable("id") Long id) {
        // Usamos el Service en lugar del Repository
        productService.deleteById(id);
        return "redirect:/admin/panel";
    }

    @GetMapping("/admin/products/edit/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        // Usamos el Service en lugar del Repository
        Product productoAEditar = productService.findById(id);
        model.addAttribute("product", productoAEditar);
        return "new-product";
    }
}