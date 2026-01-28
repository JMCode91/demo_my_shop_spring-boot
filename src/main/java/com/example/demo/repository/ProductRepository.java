package com.example.demo.repository;

import com.example.demo.domain.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {

    // 1. Método mágico: Buscar por nombre
    // Spring crea el SQL solo con leer esto.
    List<Product> findByName(String name);

    // De momento no vamos a meter las queries complejas de la foto
    // para no liarnos, con esto ya funciona la magia básica.
}