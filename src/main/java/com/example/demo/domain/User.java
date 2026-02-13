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
import java.util.List; // Importante para que funcione la lista

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
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
    private String image;

    @Column
    private LocalDate creationDate;

    @Column
    private LocalDate lastLogin;

    @Column
    private boolean active;

    // --- RELACIÓN CON PEDIDOS (Historial de compras) ---
    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    // --- NUEVA RELACIÓN CON ROLES (Spring Security) ---
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}