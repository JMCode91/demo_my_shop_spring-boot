package com.example.demo.repository;

import com.example.demo.domain.Order;
import com.example.demo.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de acceso a datos para la entidad Order (Pedido).
 */
@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {
    
    /**
     * Recupera el historial completo de pedidos de un cliente específico.
     * @param user Entidad del usuario logueado.
     * @return Lista de pedidos asociados a ese usuario.
     */
    List<Order> findByUser(User user);
}