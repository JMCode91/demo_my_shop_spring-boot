package com.example.demo.service;

import com.example.demo.domain.Order;
import com.example.demo.domain.OrderDetail;
import com.example.demo.domain.User;

import java.util.List;

/**
 * Interfaz que define las operaciones de negocio para la gestión de Pedidos.
 * Abstrae la lógica de persistencia y cálculo final (Checkout) hacia la base de datos.
 */
public interface OrderService {
    
    /**
     * Recupera el historial de compras de un cliente.
     * @param user Entidad del usuario que solicita su historial.
     * @return Lista cronológica de sus pedidos.
     */
    List<Order> findByUser(User user);
    
    /**
     * Lógica "Core" del proceso de compra.
     * Transforma un carrito temporal de sesión en un Pedido formal en la base de datos,
     * actualizando simultáneamente el stock de los productos vendidos.
     * * @param cart Lista de detalles del carrito confirmados.
     * @param user Usuario que realiza la compra.
     * @param total Precio final calculado del pedido.
     * @return La entidad Order persistida con su ID autogenerado.
     */
    Order saveOrder(List<OrderDetail> cart, User user, float total);
}