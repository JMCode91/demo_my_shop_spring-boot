package com.example.demo.service;

import com.example.demo.domain.Product;

import java.util.List;
import java.util.Set;

/**
 * Interfaz que define las operaciones de negocio para el Catálogo de Productos.
 */
public interface ProductService {

    Set<Product> findAllVisible();

    void save(Product product);

    List<Product> findAll();
    
    Product findById(Long id);

    void deleteById(Long id);

    List<Product> findByCategory(String category);

    Product modifyProduct(long id, Product newProduct);

    void deleteProduct(long id);

    /**
     * Motor de búsqueda y filtrado de productos.
     * @param query Texto a buscar.
     * @param category Categoría seleccionada.
     * @param brands Lista de marcas para filtrar.
     * @param maxPrice Límite de precio superior.
     * @return Lista de productos que cumplen todos los criterios.
     */
    List<Product> searchAndFilter(String query, String category, List<String> brands, Float maxPrice);

    List<Product> getOfertasActivas();
}