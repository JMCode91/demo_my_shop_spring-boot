package com.example.demo.service.impl;

import com.example.demo.domain.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.Role;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import com.example.demo.repository.ProductRepository;

/**
 * Implementación de la lógica de negocio para la gestión de Cuentas de Usuario.
 * Aplica encriptación a las contraseñas y asigna roles por defecto en el registro.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ProductRepository productRepository;

    // FIX de Seguridad: Mantenemos la concordancia exacta con la BD
    private static final String USER_ROLE = "USER";

    @Override
    public boolean add(User user) {
        if (user == null) return false;

        // Encriptamos la contraseña antes de guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreationDate(LocalDate.now());
        user.setActive(true);

        // Asignamos el rol estándar para todos los nuevos registros
        Role userRole = roleRepository.findByName(USER_ROLE);
        if (userRole != null) {
            user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
        }

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
        if (id != null) {
            userRepository.deleteById(id);
        }
    }

    @Override
    public User findById(Long id) {
        if (id == null) return null;
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public void update(User user) {
        if (user == null) return;

        Long userId = user.getId();
        if (userId == null) return;

        User usuarioExistente = userRepository.findById(userId).orElse(null);

        if (usuarioExistente != null) {
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                // Si el admin no pone contraseña nueva, mantenemos el hash anterior
                user.setPassword(usuarioExistente.getPassword());
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            user.setCreationDate(usuarioExistente.getCreationDate());
        }

        userRepository.save(user);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void toggleWishlist(String username, Long productId) {
        if (username == null || productId == null) return;

        User user = userRepository.findByUsername(username);
        com.example.demo.domain.Product product = productRepository.findById(productId).orElse(null);

        if (user != null && product != null) {
            if (user.getWishlist().contains(product)) {
                user.getWishlist().remove(product);
            } else {
                user.getWishlist().add(product);
            }
            userRepository.save(user);
        }
    }

    @Override
    public void updateAvatar(String username, String avatarUrl) {
        User user = userRepository.findByUsername(username); 
        if (user != null) {
            user.setImage(avatarUrl);
            userRepository.save(user); 
        }
    }

    /**
     * Método utilitario de emergencia (Backdoor temporal) para restablecer 
     * el acceso de administrador en caso de pérdida durante desarrollo.
     */
    public void forceReset() {
        User admin = userRepository.findByUsername("admin");
        if (admin != null) {
            admin.setPassword(passwordEncoder.encode("1234"));
            userRepository.save(admin);
        }
    }
}