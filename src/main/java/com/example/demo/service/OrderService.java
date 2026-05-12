package com.example.demo.service;

import com.example.demo.domain.Order;
import com.example.demo.domain.OrderDetail;
import com.example.demo.domain.User;

import java.util.List;

public interface OrderService {
    // Método original (Usado por la API y futuro historial de pedidos)
    List<Order> findByUser(User user);
    
    // NUEVO: Método para crear y guardar un pedido desde el Checkout
    Order saveOrder(List<OrderDetail> cart, User user, float total);
}