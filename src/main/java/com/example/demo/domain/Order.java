package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entidad que representa un Pedido finalizado por un usuario.
 * Contiene la relación directa tanto con el cliente (User) como con 
 * las líneas de detalle de los productos comprados (OrderDetail).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String number;

    @Column
    private float subtotal;

    @Column
    private float price; // Precio total final del pedido

    /**
     * Relación 1:N. Un pedido tiene múltiples líneas de detalle.
     * CascadeType.ALL asegura que al guardar el pedido, se guarden sus detalles automáticamente.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderDetail> details;

    /**
     * Relación N:1. Muchos pedidos pertenecen a un solo usuario.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Se sobrescribe el método toString de Lombok para evitar un bucle infinito (StackOverflow)
     * al imprimir el objeto, ya que Order llama a User y User llama a Order recursivamente.
     */
    @Override
    public String toString() {
        return "Order{" +
                "number='" + number + "'}";
    }
}