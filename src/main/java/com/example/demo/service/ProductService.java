package com.example.demo.service;

import com.example.demo.domain.Product;

import java.util.Set;


public interface ProductService {

    Set<Product> findAllVisible();
}
