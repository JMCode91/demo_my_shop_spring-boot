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
    // una linea de pedido
    // Atributos: id, quantity, price, product, order

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private float quantity;

    @Column
    private float price;

    @Column(name = "product_reference")
    private String product;

    @Column(name = "order_id")
    private String order;
}
