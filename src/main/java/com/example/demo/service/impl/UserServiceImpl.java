package com.example.demo.service.impl;

import com.example.demo.domain.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.Role;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    // 1. Inyectamos el repositorio de Roles para poder buscarlos en la base de datos
    @Autowired
    private RoleRepository roleRepository;

    // Constante con el nombre del rol por defecto en minúsculas (según la teoría del curso)
    private static final String USER_ROLE = "user";

    @Override
    public boolean add(User user) {

        // 2. MAGIA DE SEGURIDAD: Encriptamos la contraseña introducida por el usuario
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        // 3. Lógica de negocio (fechas y estado)
        user.setCreationDate(LocalDate.now());
        user.setActive(true);

        // 4. MAGIA DE ROLES: Buscamos el rol "user" y se lo asignamos
        Role userRole = roleRepository.findByName(USER_ROLE);
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));

        // 5. Guardamos en la base de datos
        userRepository.save(user);

        return true;
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}