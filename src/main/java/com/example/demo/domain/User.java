package com.example.demo.domain;

import com.example.demo.security.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

/**
 * Entidad que representa a un cliente o administrador del sistema.
 * Gestiona sus datos personales, credenciales de acceso, historial de pedidos 
 * y su lista de productos favoritos.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column
    private String password;

    @Column
    private String nif;

    @Column
    private String name;

    @Column
    private String surname;

    @Column
    private String email;

    @Column
    private String address;

    @Column
    private String city;

    @Column
    private String postalCode;

    @Column
    private String province;

    @Column
    private String country;

    @Column
    private String image; // URL del Avatar

    @Column
    private LocalDate creationDate;

    @Column
    private LocalDate lastLogin;

    @Column
    private boolean active;

    /**
     * Relación 1:N. Un usuario puede tener un historial de múltiples pedidos.
     */
    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    /**
     * Relación N:M para los roles de Spring Security (Ej: ADMIN, USER).
     * FetchType.EAGER asegura que los roles se carguen inmediatamente al hacer login.
     */
    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    /**
     * Relación N:M para la Lista de Deseos (Wishlist).
     * Relaciona a un usuario con múltiples productos que le gustan.
     */
    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_wishlist",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id"))
    private Set<Product> wishlist = new HashSet<>();
}