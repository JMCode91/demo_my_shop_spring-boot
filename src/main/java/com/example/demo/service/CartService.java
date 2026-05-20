package com.example.demo.service;

import com.example.demo.domain.OrderDetail;
import com.example.demo.domain.Product;
import java.util.List;

/**
 * Interfaz que define las operaciones de negocio para la gestión del Carrito de la Compra.
 * Estas operaciones manipulan el estado del carrito en memoria (sesión) antes de la persistencia.
 */
public interface CartService {
    
    /**
     * Añade un producto al carrito. Si el producto ya existe, incrementa su cantidad.
     */
    List<OrderDetail> addProduct(List<OrderDetail> cart, Product product);

    /**
     * Elimina completamente un producto del carrito, independientemente de su cantidad.
     */
    List<OrderDetail> removeProduct(List<OrderDetail> cart, Long productId);

    /**
     * Calcula el coste total de todos los artículos en el carrito, aplicando los impuestos
     * y descuentos correspondientes mediante delegación a las entidades del dominio.
     */
    float calculateTotal(List<OrderDetail> cart);

    /**
     * Modifica explícitamente la cantidad de unidades de un producto en el carrito.
     * Si la cantidad es 0 o menor, el producto se elimina de la lista.
     */
    List<OrderDetail> updateProductQuantity(List<OrderDetail> cart, Long productId, int quantity);
}