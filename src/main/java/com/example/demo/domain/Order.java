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
    private float price;

    @Column
    private String details;

    // --- EL CAMBIO CLAVE ---
    // Cambiamos 'String user' por el Objeto 'User'.
    // Esto conecta físicamente el Pedido con el Usuario en la base de datos.
    @ManyToOne
    @JoinColumn(name = "user_id") // En la BD la columna se llamará user_id
    private User user;

    @Override
    public String toString() {
        return "Order{" +
                "number='" + number + "'}" ;
    }
}