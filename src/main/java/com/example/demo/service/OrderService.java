package com.example.demo.service;

import com.example.demo.domain.Order;
import com.example.demo.domain.User;

import java.util.List;

public interface OrderService {
    List<Order> findByUser(User user);
}
