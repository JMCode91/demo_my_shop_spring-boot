package com.example.demo.controller;

import com.example.demo.domain.Product;
import com.example.demo.service.ImageService;
import com.example.demo.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para la gestión interna del Catálogo.
 * Contiene endpoints protegidos para que el Administrador pueda crear, editar y eliminar productos.
 */
@Controller
public class ProductController {

    private final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private ImageService imageService;

    @GetMapping("/admin/products/new")
    public String formularioNuevoProducto(Model model) {
        model.addAttribute("product", new Product());
        return "new-product";
    }

    /**
     * Procesa la creación o edición de un producto, gestionando de forma inteligente
     * si la imagen principal es una URL externa, un archivo subido, o si debe asignar una por defecto.
     * También procesa la cadena de URLs para la galería de imágenes del producto.
     */
    @PostMapping("/admin/products/save")
    public String saveProduct(
            @ModelAttribute("product") Product product,
            @RequestParam(value = "imageSource", required = false, defaultValue = "file") String imageSource,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "galleryUrls", required = false) String galleryUrls) {

        // 1. GESTIÓN DE LA IMAGEN PRINCIPAL
        if ("url".equals(imageSource) && imageUrl != null && !imageUrl.isEmpty()) {
            product.setImage(imageUrl);
        } else if ("file".equals(imageSource) && imageFile != null && !imageFile.isEmpty()) {
            try {
                String uploadedUrl = imageService.uploadImage(imageFile); 
                product.setImage(uploadedUrl);
            } catch (Exception e) {
                logger.error("Error al subir la imagen: " + e.getMessage());
            }
        } else if (product.getId() == 0 || product.getImage() == null) {
            product.setImage("https://via.placeholder.com/500?text=Sin+Imagen");
        }

        // 2. GESTIÓN DE LA GALERÍA MÚLTIPLE
        if (galleryUrls != null && !galleryUrls.trim().isEmpty()) {
            List<String> parsedGallery = Arrays.stream(galleryUrls.split(","))
                    .map(String::trim)
                    .filter(url -> !url.isEmpty())
                    .collect(Collectors.toList());
            
            product.setGallery(parsedGallery);
        } else {
            product.setGallery(new ArrayList<>());
        }

        // 3. GUARDAMOS EN BASE DE DATOS
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