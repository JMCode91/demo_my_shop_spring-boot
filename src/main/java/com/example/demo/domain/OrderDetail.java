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
    private Long id; 

    @Column(nullable = false)
    private int quantity; 

    @Column
    private float price; // Se usará solo para fijar el precio al finalizar la compra

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    // --- LÓGICA MATEMÁTICA DE DOMINIO (Cesta Viva) ---
    @Transient // No se guarda en BD, se calcula al vuelo
    public float getSubtotal() {
        if (this.product == null) return 0.0f;
        return (float) (Math.round((this.product.getFinalPrice() * this.quantity) * 100.0) / 100.0);
    }
}