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

        boolean existe = false;
        for (OrderDetail item : cart) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + 1);
                item.setPrice(item.getQuantity() * product.getPrice());
                existe = true;
                break;
            }
        }

        if (!existe) {
            OrderDetail nuevaLinea = new OrderDetail();
            nuevaLinea.setProduct(product);
            nuevaLinea.setQuantity(1);
            nuevaLinea.setPrice(product.getPrice());
            cart.add(nuevaLinea);
        }

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
        float total = 0;
        if (cart != null) {
            for (OrderDetail item : cart) {
                total += item.getPrice();
            }
        }
        return total;
    }

    @Override
    public List<OrderDetail> updateProductQuantity(List<OrderDetail> cart, Long productId, int quantity) {
        if (cart != null) {
            if (quantity <= 0) {
                // Si pone 0 o negativo, lo eliminamos
                cart.removeIf(item -> item.getProduct().getId() == productId);
            } else {
                for (OrderDetail item : cart) {
                    if (item.getProduct().getId() == productId) {
                        // Actualizamos cantidad
                        item.setQuantity(quantity);
                        // Recalculamos el subtotal de esa línea (ej: 3 teles x 500 = 1500)
                        item.setPrice(quantity * item.getProduct().getPrice());
                        break;
                    }
                }
            }
        }
        return cart;
    }
}