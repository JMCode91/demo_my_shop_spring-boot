package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    // --- EL CAMBIO CLAVE ---
    // En lugar de 'String order', usamos una Lista de objetos Order.
    // Esto refleja que un usuario tiene un historial de compras.
    @OneToMany(mappedBy = "user")
    private List<Order> orders;
}