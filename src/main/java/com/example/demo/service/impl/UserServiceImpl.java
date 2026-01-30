package com.example.demo.service.impl;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean add(User user) {
        // Lógica de negocio de la Figura 46
        user.setCreationDate(LocalDate.now());
        user.setActive(true);
        userRepository.save(user);
        return true; // Asumimos que siempre va bien por ahora
    }
}