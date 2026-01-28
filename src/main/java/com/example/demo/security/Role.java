package com.example.demo.security; // O el paquete que tengas

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name; // Ej: "ADMIN", "USER"...
}