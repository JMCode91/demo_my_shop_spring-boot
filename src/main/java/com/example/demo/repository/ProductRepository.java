package com.example.demo.repository;

import com.example.demo.domain.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Repositorio central de acceso a datos para el Catálogo de Productos.
 * Utiliza Query Derivation para generar consultas SQL complejas basadas en la nomenclatura de los métodos.
 */
@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    
    Set<Product> findByVisible(boolean visible);

    List<Product> findByCategory(String category);
    
    /**
     * Busca productos por su categoría, filtrando aquellos que están ocultos.
     */
    List<Product> findByCategoryAndVisibleTrue(String category);

    /**
     * Buscador inteligente general. Busca coincidencias ignorando mayúsculas/minúsculas 
     * tanto en el nombre del producto como en su descripción.
     */
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    /**
     * Recupera los productos rebajados (Ofertas) que estén actualmente visibles al público.
     */
    List<Product> findByDiscountGreaterThanAndVisibleTrue(float discount);

    /**
     * Buscador inteligente restringido. Igual que el buscador general, pero garantiza 
     * que no se devuelvan productos que el administrador haya marcado como ocultos.
     */
    List<Product> findByNameContainingIgnoreCaseAndVisibleTrueOrDescriptionContainingIgnoreCaseAndVisibleTrue(String nameQuery, String descQuery);
}