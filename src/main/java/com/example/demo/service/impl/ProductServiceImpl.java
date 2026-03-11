package com.example.demo.service.impl;

import com.example.demo.domain.Product;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Set<Product> findAllVisible() {
        // AQUÍ UNIMOS TODO:
        // El controller llama a este método sin parámetros.
        // Nosotros llamamos al repositorio pidiendo 'true' (los visibles).
        return productRepository.findByVisible(true);
    }

    @Override
    public void save(Product product) {
        // Aquí es donde el objeto viaja finalmente a tu tabla 'products'
        productRepository.save(product);
    }


    @Override
    public List<Product> findAll() {
        return (List<Product>) productRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }


    @Override
    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
    @Override
    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category);
    }

}
