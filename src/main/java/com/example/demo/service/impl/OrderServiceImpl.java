package com.example.demo.service.impl;

import com.example.demo.domain.Order;
import com.example.demo.domain.OrderDetail;
import com.example.demo.domain.Product;
import com.example.demo.domain.User;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Order> findByUser(User user) {
        return orderRepository.findByUser(user);
    }

    @Override
    @Transactional
    public Order saveOrder(List<OrderDetail> cart, User user, float total) {
        
        Order order = new Order();
        order.setNumber(UUID.randomUUID().toString()); 
        order.setUser(user);
        order.setPrice(total); // Gran total (con IVA y descuentos)
        
        // --- FIX: CALCULAR Y GUARDAR EL SUBTOTAL ---
        // Sumamos los precios base puros (sin impuestos ni descuentos) multiplicados por la cantidad
        float subtotalBase = 0.0f;
        for (OrderDetail item : cart) {
            subtotalBase += (item.getProduct().getPrice() * item.getQuantity());
        }
        order.setSubtotal((float) (Math.round(subtotalBase * 100.0) / 100.0));
        // -------------------------------------------

        order = orderRepository.save(order);

        for (OrderDetail item : cart) {
            Product product = item.getProduct();
            
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
            }

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            item.setOrder(order);
            item.setPrice(product.getFinalPrice()); 
            
            orderDetailRepository.save(item);
        }

        return order;
    }
}