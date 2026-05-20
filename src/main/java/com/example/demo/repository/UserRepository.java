package com.example.demo.repository;

import com.example.demo.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio de acceso a datos para las cuentas de Usuario.
 */
@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * Recupera un usuario por su credencial de acceso principal.
     * Imprescindible para el proceso de inicio de sesión (Login).
     */
    User findByUsername(String username);
}