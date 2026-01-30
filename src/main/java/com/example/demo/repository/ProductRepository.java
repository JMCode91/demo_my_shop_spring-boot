package com.example.demo.repository;

import com.example.demo.domain.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {
    Set<Product> findByVisible(boolean visible);
}