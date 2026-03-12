package com.example.demo.service.impl;

import com.example.demo.domain.Order;
import com.example.demo.domain.User;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // ¡Súper importante! Le dice a Spring que esto es un Servicio
public class OrderServiceImpl implements OrderService {

    @Autowired // ¡Inyectamos la despensa! (El Repositorio)
    private OrderRepository orderRepository;

    @Override
    public List<Order> findByUser(User user) {
        // Ahora sí, orderRepository existe y podemos usarlo
        return orderRepository.findByUser(user);
    }
}