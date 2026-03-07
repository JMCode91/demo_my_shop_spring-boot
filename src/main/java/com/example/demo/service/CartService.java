package com.example.demo.service;

import com.example.demo.domain.OrderDetail;
import com.example.demo.domain.Product;
import java.util.List;

public interface CartService {
    // Añade un producto a la lista (sumando cantidad si ya existe)
    List<OrderDetail> addProduct(List<OrderDetail> cart, Product product);

    // Elimina un producto de la lista
    List<OrderDetail> removeProduct(List<OrderDetail> cart, Long productId);

    // Calcula el coste total del carrito
    float calculateTotal(List<OrderDetail> cart);

    // Actualiza la cantidad de un producto en el carrito
    List<OrderDetail> updateProductQuantity(List<OrderDetail> cart, Long productId, int quantity);
}