package com.example.demo.service.impl;

import com.example.demo.domain.OrderDetail;
import com.example.demo.domain.Product;
import com.example.demo.service.CartService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Override
    public List<OrderDetail> addProduct(List<OrderDetail> cart, Product product) {
        if (cart == null) {
            cart = new ArrayList<>();
        }

        // Si el producto ya existe en la cesta, solo aumentamos la cantidad
        for (OrderDetail item : cart) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + 1);
                // NOTA ARQUITECTÓNICA: Ya no seteamos el precio aquí. 
                // La entidad OrderDetail calcula su propio subtotal en vivo.
                return cart;
            }
        }

        // Si no existe, creamos una nueva línea
        OrderDetail nuevaLinea = new OrderDetail();
        nuevaLinea.setProduct(product);
        nuevaLinea.setQuantity(1);
        cart.add(nuevaLinea);

        return cart;
    }

    @Override
    public List<OrderDetail> removeProduct(List<OrderDetail> cart, Long productId) {
        if (cart != null) {
            cart.removeIf(item -> item.getProduct().getId() == productId);
        }
        return cart;
    }

    @Override
    public float calculateTotal(List<OrderDetail> cart) {
        if (cart == null || cart.isEmpty()) {
            return 0.0f;
        }
        
        float total = 0.0f;
        for (OrderDetail item : cart) {
            // Delega el cálculo a la lógica rica de la entidad
            total += item.getSubtotal(); 
        }
        
        // Redondeamos a 2 decimales para evitar problemas de coma flotante
        return (float) (Math.round(total * 100.0) / 100.0);
    }

    @Override
    public List<OrderDetail> updateProductQuantity(List<OrderDetail> cart, Long productId, int quantity) {
        if (cart != null) {
            if (quantity <= 0) {
                cart.removeIf(item -> item.getProduct().getId() == productId);
            } else {
                for (OrderDetail item : cart) {
                    if (item.getProduct().getId() == productId) {
                        item.setQuantity(quantity);
                        break; // De nuevo, sin matemáticas aquí
                    }
                }
            }
        }
        return cart;
    }
}