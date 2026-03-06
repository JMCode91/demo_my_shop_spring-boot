package com.example.demo.controller;

import com.example.demo.domain.Product;
import com.example.demo.repository.ProductRepository;
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

    // Traemos el servicio y el repositorio que necesitamos para los productos
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/admin/products/new")
    public String formularioNuevoProducto(Model model) {
        model.addAttribute("product", new Product());
        return "new-product";
    }

    @PostMapping("/admin/products/save")
    public String guardarProducto(Product product, @RequestParam("imageFile") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            try {
                // 1. Ahora guardamos en una carpeta externa llamada "uploads"
                Path directorioImagenes = Paths.get("uploads");

                // 2. Si la carpeta no existe, le decimos a Java que la cree automáticamente
                if (!java.nio.file.Files.exists(directorioImagenes)) {
                    java.nio.file.Files.createDirectories(directorioImagenes);
                }

                // 3. Guardamos el archivo allí
                String nombreArchivo = imageFile.getOriginalFilename();
                Path rutaCompleta = directorioImagenes.resolve(nombreArchivo);
                java.nio.file.Files.write(rutaCompleta, imageFile.getBytes());

                // 4. Guardamos el nombre en el producto
                product.setImage(nombreArchivo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        productService.save(product);
        return "redirect:/admin/panel";
    }

    @PostMapping("/admin/products/delete/{id}")
    public String borrarProducto(@PathVariable("id") Long id) {
        productRepository.deleteById(id);
        return "redirect:/admin/panel";
    }

    @GetMapping("/admin/products/edit/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        Product productoAEditar = productRepository.findById(id).orElse(null);
        model.addAttribute("product", productoAEditar);
        return "new-product";
    }
}