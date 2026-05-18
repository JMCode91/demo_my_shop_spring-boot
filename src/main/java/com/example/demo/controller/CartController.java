package com.example.demo.controller;

import com.example.demo.domain.Order;
import com.example.demo.domain.OrderDetail;
import com.example.demo.domain.Product;
import com.example.demo.domain.User;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

/**
 * Controlador para la gestión del Carrito de la Compra y el proceso de pago (Checkout).
 * Almacena de forma temporal los productos en la sesión HTTP del usuario antes de persistirlos.
 */
@Controller
public class CartController {

    private final Logger logger = LoggerFactory.getLogger(CartController.class);

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    /**
     * Añade un producto al carrito de sesión del usuario.
     * @param id Identificador del producto a añadir.
     * @param session Sesión HTTP actual para guardar el estado del carrito.
     * @return Redirección a la página principal.
     */
    @PostMapping("/cart/add/{id}")
    @SuppressWarnings("unchecked")
    public String añadirAlCarrito(@PathVariable("id") Long id, HttpSession session) {
        Product productoEncontrado = productService.findById(id);

        if (productoEncontrado != null) {
            List<OrderDetail> carrito = (List<OrderDetail>) session.getAttribute("carrito");
            carrito = cartService.addProduct(carrito, productoEncontrado);
            session.setAttribute("carrito", carrito);
            logger.info("🛒 Producto añadido. Total de artículos en carrito: " + carrito.size());
        }
        return "redirect:/";
    }

    /**
     * Muestra la página del carrito con todos los artículos seleccionados y el total calculado.
     */
    @GetMapping("/cart")
    @SuppressWarnings("unchecked")
    public String verCarrito(HttpSession session, Model model) {
        List<OrderDetail> carrito = (List<OrderDetail>) session.getAttribute("carrito");
        float total = cartService.calculateTotal(carrito);
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);
        return "cart";
    }

    /**
     * Elimina completamente una línea de pedido (producto) del carrito.
     */
    @GetMapping("/cart/remove/{id}")
    @SuppressWarnings("unchecked")
    public String quitarDelCarrito(@PathVariable("id") Long id, HttpSession session) {
        List<OrderDetail> carrito = (List<OrderDetail>) session.getAttribute("carrito");
        carrito = cartService.removeProduct(carrito, id);
        session.setAttribute("carrito", carrito);
        return "redirect:/cart";
    }

    /**
     * Actualiza la cantidad de unidades de un producto específico en el carrito.
     */
    @PostMapping("/cart/update/{id}")
    @SuppressWarnings("unchecked")
    public String actualizarCantidadCarrito(@PathVariable("id") Long id, @RequestParam("cantidad") int cantidad, HttpSession session) {
        List<OrderDetail> carrito = (List<OrderDetail>) session.getAttribute("carrito");
        carrito = cartService.updateProductQuantity(carrito, id, cantidad);
        session.setAttribute("carrito", carrito);
        return "redirect:/cart";
    }

    /**
     * Inicia el proceso de finalización de compra (Checkout), mostrando el resumen final.
     * Requiere que el usuario esté autenticado (protegido por Spring Security).
     */
    @GetMapping("/checkout")
    @SuppressWarnings("unchecked")
    public String revisarPedido(HttpSession session, Model model) {
        List<OrderDetail> carrito = (List<OrderDetail>) session.getAttribute("carrito");

        if (carrito == null || carrito.isEmpty()) {
            return "redirect:/cart";
        }

        float total = cartService.calculateTotal(carrito);
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);
        return "checkout";
    }

    /**
     * Confirma la compra. Transforma el carrito de la sesión HTTP en un Pedido (Order)
     * persistente en la base de datos asociado al usuario actual.
     */
    @PostMapping("/checkout/confirm")
    @SuppressWarnings("unchecked")
    public String confirmarCompra(HttpSession session, Principal principal) {
        List<OrderDetail> carrito = (List<OrderDetail>) session.getAttribute("carrito");
        
        if (carrito == null || carrito.isEmpty()) {
            return "redirect:/cart";
        }

        String username = principal.getName();
        User user = userService.findByUsername(username); 
        float total = cartService.calculateTotal(carrito);

        try {
            Order pedidoGuardado = orderService.saveOrder(carrito, user, total);
            session.removeAttribute("carrito");
            return "redirect:/checkout/success?orderId=" + pedidoGuardado.getId();
            
        } catch (Exception e) {
            return "redirect:/error?message=" + e.getMessage();
        }
    }

    /**
     * Muestra la pantalla de agradecimiento tras una compra exitosa.
     * @param orderId Identificador del pedido generado para futuras referencias o facturas.
     */
    @GetMapping("/checkout/success")
    public String compraExitosa(@RequestParam("orderId") Long orderId, Model model) {
        model.addAttribute("orderId", orderId);
        return "checkout-success";
    }
}