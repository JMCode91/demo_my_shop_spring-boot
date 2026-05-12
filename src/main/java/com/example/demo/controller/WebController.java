package com.example.demo.controller;

import com.example.demo.domain.OrderDetail;
import com.example.demo.domain.Product;
import com.example.demo.domain.User;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import com.example.demo.service.ProductService;
import com.example.demo.service.UserService;
import com.example.demo.domain.Order;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WebController {

    private final Logger logger = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/")
    public String index(Model model) {
        // En lugar de enviar todo, cogemos solo 8 productos destacados
        List<Product> todosVisible = new java.util.ArrayList<>(productService.findAllVisible());
        
        List<Product> destacados = todosVisible.stream()
                // Priorizamos los que tienen descuento
                .sorted((p1, p2) -> Float.compare(p2.getDiscount(), p1.getDiscount())) 
                .limit(8)
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("products", destacados);
        return "index";
    }

    @GetMapping("/admin/panel")
    public String mostrarPanelAdmin(Model model) {
        List<User> listaUsuarios = userService.findAll();
        model.addAttribute("usuarios", listaUsuarios);

        List<Product> listaProductos = productService.findAll();
        model.addAttribute("productos", listaProductos);

        return "admin";
    }

    @GetMapping("/checkout")
    @SuppressWarnings("unchecked") // <-- Le decimos al compilador que confíe en nuestra conversión
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
    @SuppressWarnings("unchecked")
    public String confirmarCompra(HttpSession session, java.security.Principal principal) {
        List<OrderDetail> carrito = (List<OrderDetail>) session.getAttribute("carrito");
        
        if (carrito == null || carrito.isEmpty()) {
            return "redirect:/cart";
        }

        // 1. Obtenemos el usuario actual
        String username = principal.getName();
        User user = userService.findByUsername(username); 

        // 2. Calculamos total final
        float total = cartService.calculateTotal(carrito);

        // 3. Persistimos en Base de Datos
        try {
            Order pedidoGuardado = orderService.saveOrder(carrito, user, total);
            
            // Limpiamos carrito
            session.removeAttribute("carrito");
            
            // Redirigimos a una página de éxito pasando el ID del pedido para la factura
            return "redirect:/checkout/success?orderId=" + pedidoGuardado.getId();
            
        } catch (Exception e) {
            return "redirect:/error?message=" + e.getMessage();
        }
    }

    @GetMapping("/checkout/success")
    public String compraExitosa(@RequestParam("orderId") Long orderId, Model model) {
        // Pasamos el ID del pedido a la vista para poder mostrarlo 
        // y usarlo luego en el botón de descargar PDF
        model.addAttribute("orderId", orderId);
        return "checkout-success";
    }

    @PostMapping("/cart/add/{id}")
    @SuppressWarnings("unchecked") // <-- Añadido aquí también
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

    @GetMapping("/cart")
    @SuppressWarnings("unchecked") // <-- Añadido aquí también
    public String verCarrito(HttpSession session, Model model) {
        List<OrderDetail> carrito = (List<OrderDetail>) session.getAttribute("carrito");
        float total = cartService.calculateTotal(carrito);
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);

        return "cart";
    }

    @GetMapping("/cart/remove/{id}")
    @SuppressWarnings("unchecked") // <-- Añadido aquí también
    public String quitarDelCarrito(@PathVariable("id") Long id, HttpSession session) {
        List<OrderDetail> carrito = (List<OrderDetail>) session.getAttribute("carrito");
        carrito = cartService.removeProduct(carrito, id);
        session.setAttribute("carrito", carrito);

        return "redirect:/cart";
    }


    @PostMapping("/cart/update/{id}")
    @SuppressWarnings("unchecked") // <-- Añadido aquí también
    public String actualizarCantidadCarrito(@PathVariable("id") Long id, @RequestParam("cantidad") int cantidad, HttpSession session) {
        List<OrderDetail> carrito = (List<OrderDetail>) session.getAttribute("carrito");
        carrito = cartService.updateProductQuantity(carrito, id, cantidad);
        session.setAttribute("carrito", carrito);

        return "redirect:/cart";
    }

    @GetMapping("/product/{id}")
    public String verDetalleProducto(@PathVariable("id") Long id, Model model) {
        Product productoEncontrado = productService.findById(id);
        model.addAttribute("product", productoEncontrado);

        // 1. Buscamos de la misma categoría
        List<Product> recomendados = productService.findByCategory(productoEncontrado.getCategory())
                .stream()
                .filter(p -> p.getId() != id)
                .limit(8)
                // Usamos una lista mutable para poder añadirle elementos después
                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));

        // 2. Si hay menos de 4, rellenamos con otros productos del catálogo
        if (recomendados.size() < 4) {
            List<Product> extras = new java.util.ArrayList<>(productService.findAllVisible());
            
            // Creamos una referencia inmutable (final) para que el Stream pueda leerla sin dar error
            final List<Product> actuales = recomendados; 
            
            List<Product> adicionales = extras.stream()
                    .filter(p -> p.getId() != id && !actuales.contains(p))
                    .limit(8 - actuales.size()) // Solo cogemos los que faltan para llegar a 8
                    .collect(java.util.stream.Collectors.toList());
            
            // Añadimos los extra a la lista original
            recomendados.addAll(adicionales);
        }

        model.addAttribute("recomendados", recomendados);
        return "product";
    }


   // 1. Endpoint para el BUSCADOR
    // 1. Endpoint para el BUSCADOR Y FILTROS
    @GetMapping("/search")
    public String search(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "brands", required = false) List<String> brands,
            @RequestParam(value = "maxPrice", required = false) Float maxPrice,
            org.springframework.ui.Model model) {

        // Llamamos a nuestro nuevo super-buscador
        List<Product> products = productService.searchAndFilter(query, category, brands, maxPrice);

        // Ajustamos el título de la página
        if (query != null && !query.isEmpty()) {
            model.addAttribute("pageTitle", "Resultados para: '" + query + "'");
        } else if (category != null && !category.isEmpty()) {
            model.addAttribute("pageTitle", "Categoría: " + category.toUpperCase());
        } else {
            model.addAttribute("pageTitle", "Catálogo completo");
        }
        
        model.addAttribute("products", products);
        
        // Devolvemos los filtros al modelo para que la vista los recuerde
        model.addAttribute("currentCategory", category);
        model.addAttribute("currentQuery", query);
        
        return "catalog"; 
    }

    // 2. Endpoint para las CATEGORÍAS (Menú superior)
    @GetMapping("/category/{cat}")
    public String showCategory(@PathVariable("cat") String cat, org.springframework.ui.Model model) {
        List<Product> products = productService.findByCategory(cat);
        
        // Ponemos la primera letra en mayúscula para que quede bonito el título
        String tituloCategoria = cat.substring(0, 1).toUpperCase() + cat.substring(1).replace("-", " ");
        
        model.addAttribute("pageTitle", "Categoría: " + tituloCategoria);
        model.addAttribute("products", products);
        return "catalog"; // Nos llevará a la misma plantilla
    }


    // 3. Endpoint para el PERFIL DE USUARIO
    @GetMapping("/profile")
    public String showProfile() {
        // No necesitamos pasar el usuario por el Model porque Thymeleaf 
        // lo lee directamente del contexto de Spring Security.
        return "myprofile";
    }
}