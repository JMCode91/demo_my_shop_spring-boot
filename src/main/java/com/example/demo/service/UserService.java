package com.example.demo.service;

import com.example.demo.domain.User;

import java.util.List;

public interface UserService {
    boolean add(User user);
    User findByUsername(String username);
    List<User> findAll();
    void deleteById(Long id);
    User findById(Long id);
    void update(User user);
}
