package com.example.demo.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Entidad que representa una Factura en el sistema.
 * A diferencia del Pedido (Order), la factura guarda una "foto fija" (snapshot)
 * de los datos del cliente en el momento de la compra por motivos legales y contables.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String number;

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
    private float subtotal;

    @Column
    private float taxes;

    @Column
    private float total;

    @Column
    private LocalDate creationDate;

    @Column
    private LocalDate dueDate;

    // Se guarda como String (username/email) intencionalmente para no perder 
    // la referencia en la factura si el usuario se elimina de la base de datos en el futuro.
    @Column
    private String user; 
}