package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "order_details")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Cambiado a Long (objeto)

    @Column(nullable = false)
    private int quantity; // Cambiado a int (número entero de unidades)

    @Column
    private float price; // Precio total de esta línea (precio del producto * cantidad)

    // --- MAGIA AQUÍ: Relación real con el Producto ---
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    // --- MAGIA AQUÍ: Relación real con el Pedido ---
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}