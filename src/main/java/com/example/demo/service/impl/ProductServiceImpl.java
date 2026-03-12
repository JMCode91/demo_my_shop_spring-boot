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



    @Override
    public Product modifyProduct(long id, Product newProduct) {
        // 1. Buscamos el producto original
        Product product = this.findById(id);

        // 2. Si no existe, lanzamos la alarma (la atrapará el controlador)
        if (product == null) {
            throw new com.example.demo.exception.ProductNotFoundException(id);
        }

        // 3. Si existe, le inyectamos el ID original al nuevo producto para que Spring
        // sepa que tiene que sobrescribir y no crear uno nuevo
        newProduct.setId(product.getId());

        // 4. Lo guardamos (sobrescribimos)
        return productRepository.save(newProduct);
    }



    @Override
    public void deleteProduct(long id) {
        // 1. Comprobamos si el producto existe
        Product product = this.findById(id);

        // 2. Si no existe, lanzamos la excepción 404
        if (product == null) {
            throw new com.example.demo.exception.ProductNotFoundException(id);
        }

        // 3. Si existe, lo borramos de la base de datos
        productRepository.deleteById(id);
    }

}
