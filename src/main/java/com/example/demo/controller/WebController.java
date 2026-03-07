package com.example.demo.controller;

import com.example.demo.domain.OrderDetail;
import com.example.demo.domain.Product;
import com.example.demo.domain.User;
import com.example.demo.service.CartService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Set;

@Controller
public class WebController {

    private final Logger logger = LoggerFactory.getLogger(WebController.class);

    // --- AQUÍ ESTÁN NUESTROS COCINEROS EXCLUSIVAMENTE ---
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;
    // ------------------------------------------------------

    @GetMapping("/")
    @PostMapping("/")
    public String index(Model model) {
        Set<Product> products = productService.findAllVisible();
        model.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/admin/panel")
    public String mostrarPanelAdmin(Model model) {
        // Ahora usamos los Services en lugar de los Repositorios directamente
        List<User> listaUsuarios = userService.findAll();
        model.addAttribute("usuarios", listaUsuarios);

        List<Product> listaProductos = productService.findAll();
        model.addAttribute("productos", listaProductos);

        return "admin";
    }

    @GetMapping("/checkout")
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

    @PostMapping("/checkout/confirm")
    public String confirmarCompra(HttpSession session) {
        session.removeAttribute("carrito");
        return "redirect:/?exito=true";
    }

    @PostMapping("/cart/add/{id}")
    public String añadirAlCarrito(@PathVariable("id") Long id, HttpSession session) {
        // Usamos productService en vez de productRepository
        Product productoEncontrado = productService.findById(id);

        if (productoEncontrado != null) {
            List<OrderDetail> carrito = (List<OrderDetail>) session.getAttribute("carrito");
            carrito = cartService.addProduct(carrito, productoEncontrado);
            session.setAttribute("carrito", carrito);
            logger.info("🛒 Producto añadido. Total de artículos en carrito: " + carrito.size());
        }

        return "redirect:/";
    }

    @GetMapping("/cart")
    public String verCarrito(HttpSession session, Model model) {
        List<OrderDetail> carrito = (List<OrderDetail>) session.getAttribute("carrito");
        float total = cartService.calculateTotal(carrito);
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);

        return "cart";
    }

    @GetMapping("/cart/remove/{id}")
    public String quitarDelCarrito(@PathVariable("id") Long id, HttpSession session) {
        List<OrderDetail> carrito = (List<OrderDetail>) session.getAttribute("carrito");
        carrito = cartService.removeProduct(carrito, id);
        session.setAttribute("carrito", carrito);

        return "redirect:/cart";
    }

    @GetMapping("/product/{id}")
    public String verDetalleProducto(@PathVariable("id") Long id, Model model) {
        // Usamos productService en vez de productRepository
        Product productoEncontrado = productService.findById(id);
        model.addAttribute("product", productoEncontrado);

        Set<Product> recomendados = productService.findAllVisible();
        model.addAttribute("recomendados", recomendados);

        return "product";
    }
}