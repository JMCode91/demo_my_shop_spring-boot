package com.example.demo.repository;

import com.example.demo.security.Role; 
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio para la gestión de Roles de autorización de Spring Security.
 */
@Repository
public interface RoleRepository extends CrudRepository<Role, Long> {
    
    /**
     * Busca un rol exacto por su nombre identificativo (Ej: "ADMIN").
     */
    Role findByName(String name);
}