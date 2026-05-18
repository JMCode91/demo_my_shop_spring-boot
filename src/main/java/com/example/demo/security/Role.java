package com.example.demo.security;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa los roles de autorización del sistema.
 * Define qué permisos tiene un usuario (Ej. "ADMIN" o "USER") para acceder 
 * a zonas restringidas de la aplicación.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "roles")
@Table
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @Column(unique = true, nullable = false)
    private String name;
}