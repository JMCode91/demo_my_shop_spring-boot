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
    private String brand;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String technicalDescription;

    @Column
    private String category;

    @Schema(description = "Precio del producto base (sin IVA)", example = "15.35")
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
    private String image;

    @ElementCollection 
    @CollectionTable(name = "product_gallery", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> gallery;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    // Mantenemos el campo transient por compatibilidad con posibles queries previas
    @Transient
    private float finalPrice;

    // --- LÓGICA MATEMÁTICA DE DOMINIO ---

    // 1. Obtener precio tachado (Precio Base + IVA)
    public float getPriceWithTax() {
        if (this.taxes <= 0) return this.price;
        float taxMultiplier = 1 + (this.taxes / 100.0f);
        return (float) (Math.round((this.price * taxMultiplier) * 100.0) / 100.0);
    }

    // 2. Sobrescribimos el Getter de finalPrice (Precio Base + IVA - Descuento)
    public float getFinalPrice() {
        float baseWithTax = getPriceWithTax();
        if (this.discount <= 0) return baseWithTax;
        
        float discountMultiplier = 1 - (this.discount / 100.0f);
        return (float) (Math.round((baseWithTax * discountMultiplier) * 100.0) / 100.0);
    }

    @Override
    public String toString() {
        return "Product{" + "name='" + name + '\'' + '}';
    }
}