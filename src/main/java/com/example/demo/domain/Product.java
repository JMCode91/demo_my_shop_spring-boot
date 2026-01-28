package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor  // Necesario para JPA (Base de datos)
@AllArgsConstructor // <--- ESTO ES LO QUE FALTABA (Necesario para @Builder)
@Entity(name = "products")
public class Product {
    // atributos: id, name, description, category, price, discount, taxes, visible, stock, image

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column
    private String category;

    @Column
    private float price;

    @Column
    private float discount;

    @Column
    private float taxes;

    @Column
    private boolean visible;

    @Column
    private int stock;

    @Column
    private String image;

    // Nota: Al usar @Data, Lombok ya crea un toString() con todos los campos.
    // Pero si prefieres este personalizado que solo muestra el nombre, está perfecto dejarlo.
    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                '}';
    }
}

