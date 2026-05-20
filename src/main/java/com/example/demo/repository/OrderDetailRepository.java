package com.example.demo.repository;

import com.example.demo.domain.OrderDetail;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos para las líneas de detalle de los pedidos.
 */
@Repository
public interface OrderDetailRepository extends CrudRepository<OrderDetail, Long> {
}