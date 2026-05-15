package com.example.demo.repository;

import com.example.demo.domain.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    Set<Product> findByVisible(boolean visible);

    List<Product> findByCategory(String category);
    // Busca productos por categoría Y que además sean visibles
    List<Product> findByCategoryAndVisibleTrue(String category);

    // Buscador inteligente
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    // Busca productos con descuento mayor a X Y que además tengan visible = true
    List<Product> findByDiscountGreaterThanAndVisibleTrue(float discount);

    // Busca por nombre (y que sea visible) O por descripción (y que sea visible)
    List<Product> findByNameContainingIgnoreCaseAndVisibleTrueOrDescriptionContainingIgnoreCaseAndVisibleTrue(String nameQuery, String descQuery);
}

