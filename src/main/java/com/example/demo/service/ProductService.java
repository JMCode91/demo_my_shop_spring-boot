package com.example.demo.service;

import com.example.demo.domain.Product;

import java.util.List;
import java.util.Set;


public interface ProductService {

    Set<Product> findAllVisible();

    void save(Product product);

    List<Product> findAll();
    Product findById(Long id);

    void deleteById(Long id);

    List<Product> findByCategory(String category);

    Product modifyProduct(long id, Product newProduct);

    void deleteProduct(long id);

    List<Product> searchAndFilter(String query, String category, List<String> brands, Float maxPrice);

    List<Product> getOfertasActivas();
}
