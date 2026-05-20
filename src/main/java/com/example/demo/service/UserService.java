package com.example.demo.service;

import com.example.demo.domain.User;

import java.util.List;

/**
 * Interfaz que define las operaciones de negocio sobre los Usuarios.
 */
public interface UserService {
    
    boolean add(User user);
    
    User findByUsername(String username);
    
    List<User> findAll();
    
    void deleteById(Long id);
    
    User findById(Long id);
    
    void update(User user);
    
    /**
     * Alterna un producto en la lista de deseos del usuario.
     */
    void toggleWishlist(String username, Long productId);
    
    void updateAvatar(String username, String avatarUrl);
}