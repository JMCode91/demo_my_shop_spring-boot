package com.example.demo.controller;

import com.example.demo.domain.Product;
import com.example.demo.service.ImageService; // <-- 1. Importamos nuestra nueva interfaz
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    // <-- 2. Inyectamos nuestro servicio de imágenes
    @Autowired
    private ImageService imageService;

    @GetMapping("/admin/products/new")
    public String formularioNuevoProducto(Model model) {
        model.addAttribute("product", new Product());
        return "new-product";
    }

    @PostMapping("/admin/products/save")
    public String guardarProducto(Product product, @RequestParam("imageFile") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            try {
                // <-- 3. AQUÍ OCURRE LA MAGIA.
                // Mandamos el archivo a Cloudinary y recogemos el enlace (URL)
                String imageUrl = imageService.uploadImage(imageFile);

                // Guardamos ese enlace de internet en nuestro producto
                product.setImage(imageUrl);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        productService.save(product);
        return "redirect:/admin/panel";
    }

    @PostMapping("/admin/products/delete/{id}")
    public String borrarProducto(@PathVariable("id") Long id) {
        productService.deleteById(id);
        return "redirect:/admin/panel";
    }

    @GetMapping("/admin/products/edit/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        Product productoAEditar = productService.findById(id);
        model.addAttribute("product", productoAEditar);
        return "new-product";
    }
}