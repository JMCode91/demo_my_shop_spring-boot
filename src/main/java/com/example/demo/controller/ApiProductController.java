package com.example.demo.controller;

import com.example.demo.domain.Product;
import com.example.demo.exception.ProductNotFoundException;
import com.example.demo.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Productos", description = "Catálogo y gestión de productos de MyShop")
public class ApiProductController {

    // Herramienta para escribir errores en la consola (logger)
    private static final Logger logger = LoggerFactory.getLogger(ApiProductController.class);

    @Autowired
    private ProductService productService;

    // =========================================================================
    // 1. OBTENER TODOS LOS PRODUCTOS (Con o sin filtro)
    // =========================================================================
    @Operation(summary = "Obtener lista de productos", description = "Devuelve el catálogo completo. Permite filtrar por categoría usando el parámetro 'category'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista devuelta con éxito",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Product.class))))
    })
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(value = "category", defaultValue = "") String category) {

        logger.info("Petición GET recibida. Filtro de categoría: '" + category + "'");

        List<Product> products;

        if (category.equals("")) {
            products = productService.findAll();
        } else {
            products = productService.findByCategory(category);
        }

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    // =========================================================================
    // 2. OBTENER UN PRODUCTO POR ID
    // =========================================================================
    @Operation(summary = "Obtener un producto por su ID")
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Product> getProduct(@PathVariable("id") long id) {

        logger.info("Petición GET recibida. Buscando producto con ID: " + id);

        Product product = productService.findById(id);

        if (product == null) {
            throw new ProductNotFoundException(id);
        }

        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    // =========================================================================
    // 3. CREAR UN NUEVO PRODUCTO (POST)
    // =========================================================================
    @Operation(summary = "Añadir un nuevo producto")
    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {

        logger.info("Petición POST recibida para crear el producto: " + product.getName());

        productService.save(product);

        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    // =========================================================================
    // 4. ACTUALIZAR UN PRODUCTO (PUT)
    // =========================================================================
    @Operation(summary = "Modificar un producto existente")
    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<Product> modifyProduct(
            @PathVariable("id") long id,
            @RequestBody Product newProduct) {

        logger.info("Petición PUT recibida para modificar el producto con ID: " + id);

        Product product = productService.modifyProduct(id, newProduct);

        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    // =========================================================================
    // 5. BORRAR UN PRODUCTO (DELETE)
    // =========================================================================
    @Operation(summary = "Eliminar un producto por su ID")
    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Response> deleteProduct(@PathVariable("id") long id) {

        logger.info("Petición DELETE recibida para borrar el producto con ID: " + id);

        productService.deleteProduct(id);

        return new ResponseEntity<>(Response.noErrorResponse(), HttpStatus.OK);
    }

    // =========================================================================
    // EL CAZADOR DE ERRORES (ExceptionHandler)
    // =========================================================================
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response> handleException(ProductNotFoundException pnfe) {
        Response response = Response.errorResponse(Response.NOT_FOUND, pnfe.getMessage());
        logger.error(pnfe.getMessage(), pnfe);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}