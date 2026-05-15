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
        return productRepository.findByVisible(true);
    }

    @Override
    public void save(Product product) {
        if (product != null) {
            productRepository.save(product);
        }
    }

    @Override
    public List<Product> findAll() {
        return (List<Product>) productRepository.findAll();
    }

    @Override
    public Product findById(Long id) {
        if (id == null) return null;
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        if (id != null) {
            productRepository.deleteById(id);
        }
    }

    @Override
    public List<Product> findByCategory(String category) {
        return productRepository.findByCategoryAndVisibleTrue(category);
    }

    @Override
    public Product modifyProduct(long id, Product newProduct) {
        Product product = this.findById(id);

        if (product == null) {
            throw new com.example.demo.exception.ProductNotFoundException(id);
        }

        newProduct.setId(product.getId());
        this.save(newProduct);
        return newProduct; 
    }

    @Override
    public void deleteProduct(long id) {
        Product product = this.findById(id);

        if (product == null) {
            throw new com.example.demo.exception.ProductNotFoundException(id);
        }

        this.deleteById(id);
    }

    @Override
    public List<Product> searchAndFilter(String query, String category, List<String> brands, Float maxPrice) {
        List<Product> products;

        // 1. Búsqueda inicial (por texto o por categoría) SIEMPRE respetando que sean visibles
        if (query != null && !query.isEmpty()) {
            // Usamos el método nativo de Spring Data que no da fallos de Entidad
            products = productRepository.findByNameContainingIgnoreCaseAndVisibleTrueOrDescriptionContainingIgnoreCaseAndVisibleTrue(query, query);
        } else if (category != null && !category.isEmpty()) {
            products = productRepository.findByCategoryAndVisibleTrue(category);
        } else {
            products = new java.util.ArrayList<>(this.findAllVisible());
        }

        // 2. Colador de Marcas (Si el usuario ha seleccionado alguna)
        if (brands != null && !brands.isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getBrand() != null && brands.stream().anyMatch(b -> p.getBrand().equalsIgnoreCase(b)))
                    .collect(java.util.stream.Collectors.toList());
        }

        // 3. Colador de Precio Máximo
        if (maxPrice != null) {
            products = products.stream()
                    .filter(p -> p.getFinalPrice() <= maxPrice)
                    .collect(java.util.stream.Collectors.toList());
        }

        return products;
    }

    @Override
    public List<Product> getOfertasActivas() {
        // Filtramos por descuento > 0 y solo los que el admin haya marcado como visibles
        return productRepository.findByDiscountGreaterThanAndVisibleTrue(0f);
    }
}