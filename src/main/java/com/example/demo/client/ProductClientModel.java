package com.example.demo.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductClientModel {
    private long id;
    private String name;
    private String description;
    private String category;
    private float price;

    // Si la API nos manda "stock" o "discount" (que los tiene),
    // esta clase los ignorará y no dará error gracias al @JsonIgnoreProperties
}