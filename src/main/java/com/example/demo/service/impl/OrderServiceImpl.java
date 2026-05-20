package com.example.demo.service.impl;

import com.example.demo.domain.Order;
import com.example.demo.domain.OrderDetail;
import com.example.demo.domain.Product;
import com.example.demo.domain.User;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Implementación de la lógica de negocio para los Pedidos.
 * Orquesta la persistencia en múltiples tablas garantizando la integridad de los datos.
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Order> findByUser(User user) {
        return orderRepository.findByUser(user);
    }

    /**
     * La anotación @Transactional es vital aquí. Asegura que si el servidor falla a mitad del proceso
     * (por ejemplo, al actualizar el stock del producto 3), se haga un "Rollback" automático 
     * y no se guarde el pedido a medias, evitando inconsistencias en contabilidad.
     */
    @Override
    @Transactional
    public Order saveOrder(List<OrderDetail> cart, User user, float total) {
        
        Order order = new Order();
        // Genera un número de pedido único universal (Ej: a1b2c3d4...)
        order.setNumber(UUID.randomUUID().toString()); 
        order.setUser(user);
        order.setPrice(total); // Gran total (con IVA y descuentos)
        
        // --- CÁLCULO DE SUBTOTAL ---
        // Sumamos los precios base puros (sin impuestos ni descuentos) 
        float subtotalBase = 0.0f;
        for (OrderDetail item : cart) {
            subtotalBase += (item.getProduct().getPrice() * item.getQuantity());
        }
        order.setSubtotal((float) (Math.round(subtotalBase * 100.0) / 100.0));

        // 1. Persistimos el "Cuerpo" (Cabecera) del pedido primero para obtener su ID
        order = orderRepository.save(order);

        // 2. Iteramos sobre las líneas para persistir el "Detalle" y descontar Stock
        for (OrderDetail item : cart) {
            Product product = item.getProduct();
            
            // Validación defensiva: Evitar vender lo que no tenemos
            if (product.getStock() < item.getQuantity()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + product.getName());
            }

            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            item.setOrder(order);
            // Congelamos el precio de venta en el detalle para el historial
            item.setPrice(product.getFinalPrice()); 
            
            orderDetailRepository.save(item);
        }

        return order;
    }
}