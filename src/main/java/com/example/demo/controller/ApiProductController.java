package com.example.demo.controller;

import com.example.demo.domain.Product;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ApiProductController {

    // Herramienta para escribir errores en la consola (logger)
    private static final Logger logger = LoggerFactory.getLogger(ApiProductController.class);

    @Autowired
    private ProductService productService;

    // --- OBTENER PRODUCTOS (Con o sin filtro de categoría) ---
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(value = "category", defaultValue = "") String category) {

        logger.info("Petición GET recibida. Filtro de categoría: '" + category + "'");

        List<Product> products;

        if (category.equals("")) {
            // Si no hay filtro, buscamos todos
            products = productService.findAll();
        } else {
            // Si hay filtro, buscamos por categoría
            products = productService.findByCategory(category);
        }

        return new ResponseEntity<>(products, HttpStatus.OK);
    }


    // --- EL CAZADOR DE ERRORES (Lo que pide el curso) ---
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response> handleException(ProductNotFoundException pnfe) {
        // 1. Metemos el error en nuestro "sobre" oficial
        Response response = Response.errorResponse(Response.NOT_FOUND, pnfe.getMessage());

        // 2. Pintamos el error en rojo en la consola de Spring para enterarnos nosotros
        logger.error(pnfe.getMessage(), pnfe);

        // 3. Devolvemos el sobre al usuario (o la app móvil) con un código 404 (NOT_FOUND)
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    // --- OBTENER UN PRODUCTO POR ID ---
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Product> getProduct(@PathVariable("id") long id) {

        logger.info("Petición GET recibida. Buscando producto con ID: " + id);

        // Buscamos el producto. Si lo encuentra, lo guarda en 'product'.
        // Si no lo encuentra (orElseThrow), hace saltar nuestra alarma personalizada.
        Product product = productService.findById(id);

        if (product == null) {
            throw new ProductNotFoundException(id);
        }

        return new ResponseEntity<>(product, HttpStatus.OK);
    }

}