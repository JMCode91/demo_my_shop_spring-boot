package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una línea individual dentro de un Pedido o Carrito.
 * Relaciona una cantidad específica de un Producto con un Pedido concreto.
 */
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
    private float price; // Precio unitario congelado en el momento de la compra

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    /**
     * Lógica Matemática de Dominio.
     * Calcula dinámicamente el subtotal de esta línea (Precio final del producto * Cantidad).
     * @Transient indica que este valor no se guarda en una columna de la BD, se calcula al vuelo.
     */
    @Transient 
    public float getSubtotal() {
        if (this.product == null) return 0.0f;
        return (float) (Math.round((this.product.getFinalPrice() * this.quantity) * 100.0) / 100.0);
    }
}