package com.example.demo.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor  
@AllArgsConstructor 
@Entity(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String brand; // <--- NUEVO: Para filtros de marcas

    @Column(columnDefinition = "TEXT") // <--- TEXT permite descripciones muy largas
    private String description;

    @Column(columnDefinition = "TEXT") // <--- NUEVO: Para la tabla de specs técnica
    private String technicalDescription;

    @Column
    private String category;

    @Schema(description = "Precio del producto en euros", example = "15.35")
    @Column
    private float price;

    @Column
    private float discount;

    @Column
    private float taxes;

    @Column
    private Boolean visible;

    @Column
    private int stock;

    @Column
    private String image; // Imagen principal

    // NUEVO: Lista de imágenes adicionales para la galería del detalle
    @ElementCollection 
    @CollectionTable(name = "product_gallery", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> gallery;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Transient
    private float finalPrice;

    @Override
    public String toString() {
        return "Product{" + "name='" + name + '\'' + '}';
    }
}