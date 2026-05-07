package com.example.demo.service.impl;

import com.example.demo.domain.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.Role;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String USER_ROLE = "user";

    @Override
    public boolean add(User user) {
        // Seguridad preventiva
        if (user == null) return false;

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        user.setCreationDate(LocalDate.now());
        user.setActive(true);

        Role userRole = roleRepository.findByName(USER_ROLE);
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));

        userRepository.save(user);

        return true;
    }

    @Override
    public User findByUsername(String username) {
        if (username == null) return null;
        return userRepository.findByUsername(username);
    }


    @Override
    public List<User> findAll() {
        return (List<User>) userRepository.findAll();
    }


    @Override
    public void deleteById(Long id) {
        // SEGURIDAD: Evitamos el aviso garantizando que el ID no es nulo
        if (id != null) {
            userRepository.deleteById(id);
        }
    }

    @Override
    public User findById(Long id) {
        // SEGURIDAD: Evitamos el aviso garantizando que el ID no es nulo
        if (id == null) {
            return null;
        }
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void update(User user) {
        // 1. Comprobamos que el usuario no sea nulo
        if (user == null) {
            return;
        }

        // 2. Extraemos el ID a una variable y comprobamos
        Long userId = user.getId();
        if (userId == null) {
            return;
        }

        // 3. ¡Ahora usamos la variable userId! El editor ya confía en ella
        User usuarioExistente = userRepository.findById(userId).orElse(null);

        if (usuarioExistente != null) {
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(usuarioExistente.getPassword());
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            user.setCreationDate(usuarioExistente.getCreationDate());
        }

        userRepository.save(user);
    }
}