package com.example.demo.service;

import com.example.demo.domain.User;

public interface UserService {
    boolean add(User user);
    User findByUsername(String username);
}
