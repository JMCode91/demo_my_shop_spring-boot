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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

@Controller
public class WebController {

    private final Logger logger = LoggerFactory.getLogger(WebController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @GetMapping("/")
    @PostMapping("/")
    public String index(Model model) {
        Set<Product> products = productService.findAllVisible();
        model.addAttribute("products", products);
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
    public String confirmarCompra(HttpSession session) {
        session.removeAttribute("carrito");
        return "redirect:/?exito=true";
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

        Set<Product> recomendados = productService.findAllVisible();
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
}