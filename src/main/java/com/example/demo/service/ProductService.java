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
}
