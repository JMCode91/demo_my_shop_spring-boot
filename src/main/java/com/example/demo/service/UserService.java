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
    // Alterna un producto en la lista de deseos (lo añade si no está, lo quita si ya está)
    void toggleWishlist(String username, Long productId);
    void updateAvatar(String username, String avatarUrl);
}
