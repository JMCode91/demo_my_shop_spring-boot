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

    // ========================================================
    // 🧠 LÓGICA DE NEGOCIO PRIVADA (El "Cerebro" del Servicio)
    // ========================================================
    private void calculateAndSetFinalPrice(Product product) {
        if (product != null) {
            // 1. Calculamos el precio base con IVA (Para mostrarlo como precio "Original" si hay descuento)
            float precioConIva = product.getPrice() + (product.getPrice() * (product.getTaxes() / 100));
            // Como no tenemos un campo 'precioOriginalConIva' en la clase Product,
            // sobrescribimos temporalmente el 'price' (solo en memoria) para que Thymeleaf lo pinte tachado.
            product.setPrice((float) (Math.round(precioConIva * 100.0) / 100.0));

            // 2. Calculamos el precio final aplicando el descuento sobre el precio con IVA
            float finalPrice = precioConIva - (precioConIva * (product.getDiscount() / 100));
            
            // 3. Redondeamos y guardamos en el atributo @Transient
            product.setFinalPrice((float) (Math.round(finalPrice * 100.0) / 100.0));
        }
    }

    // ========================================================

    @Override
    public Set<Product> findAllVisible() {
        Set<Product> products = productRepository.findByVisible(true);
        // Le inyectamos el precio calculado a cada producto antes de enviarlo al Frontend
        products.forEach(this::calculateAndSetFinalPrice);
        return products;
    }

    @Override
    public void save(Product product) {
        if (product != null) {
            productRepository.save(product);
        }
    }

    @Override
    public List<Product> findAll() {
        List<Product> products = (List<Product>) productRepository.findAll();
        // Le inyectamos el precio calculado a cada producto
        products.forEach(this::calculateAndSetFinalPrice);
        return products;
    }

    @Override
    public Product findById(Long id) {
        if (id == null) return null;
        
        Product product = productRepository.findById(id).orElse(null);
        // Le inyectamos el precio calculado al producto encontrado
        calculateAndSetFinalPrice(product);
        return product;
    }

    @Override
    public void deleteById(Long id) {
        if (id != null) {
            productRepository.deleteById(id);
        }
    }

    @Override
    public List<Product> findByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category);
        products.forEach(this::calculateAndSetFinalPrice);
        return products;
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

        // 1. Búsqueda inicial (por texto o por categoría)
        if (query != null && !query.isEmpty()) {
            products = productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
        } else if (category != null && !category.isEmpty()) {
            products = productRepository.findByCategory(category);
        } else {
            products = new java.util.ArrayList<>(this.findAllVisible());
        }

        // 2. Inyectamos los precios finales ANTES de filtrar, porque el usuario filtra por el precio que ve (con IVA)
        products.forEach(this::calculateAndSetFinalPrice);

        // 3. Colador de Marcas (Si el usuario ha seleccionado alguna)
        if (brands != null && !brands.isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getBrand() != null && brands.stream().anyMatch(b -> p.getBrand().equalsIgnoreCase(b)))
                    .collect(java.util.stream.Collectors.toList());
        }

        // 4. Colador de Precio Máximo
        if (maxPrice != null) {
            products = products.stream()
                    .filter(p -> p.getFinalPrice() <= maxPrice)
                    .collect(java.util.stream.Collectors.toList());
        }

        return products;
    }
}