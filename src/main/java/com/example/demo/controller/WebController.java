package com.example.demo.controller;

import com.example.demo.domain.OrderDetail;
import com.example.demo.domain.Product;
import com.example.demo.domain.User;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.ArrayList;


import java.util.Set;

@Controller
public class WebController {

    private final Logger logger = LoggerFactory.getLogger(WebController.class);

    private final ProductService productService;

    public WebController(@NonNull ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/")
    @PostMapping("/")
    public String index(Model model) {
        logger.info("👋 El usuario ha entrado en la Home y vamos a buscar productos...");
        Set<Product> products = productService.findAllVisible();
        model.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/admin/panel")
    public String mostrarPanelAdmin(Model model) {
        // Traemos a todos los usuarios de la base de datos
        List<User> listaUsuarios =(List<User>) userRepository.findAll();
        model.addAttribute("usuarios", listaUsuarios);

        // Traemos los productos
        List<Product> listaProductos = (List<Product>) productRepository.findAll();
        model.addAttribute("productos", listaProductos);

        return "admin";
    }

    // 1. Mostrar la página de resumen del pedido
    @GetMapping("/checkout")
    public String revisarPedido(HttpSession session, Model model) {
        List<OrderDetail> carrito = (List<OrderDetail>) session.getAttribute("carrito");

        // Si algún listillo intenta entrar al checkout escribiendo la URL a mano sin tener nada en la cesta
        if (carrito == null || carrito.isEmpty()) {
            return "redirect:/cart";
        }

        float total = 0;
        for (OrderDetail item : carrito) {
            total += item.getPrice();
        }

        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);

        return "checkout";
    }

    // 2. Procesar la compra al darle al botón final
    @PostMapping("/checkout/confirm")
    public String confirmarCompra(HttpSession session) {

        // ⚠️ ATENCIÓN: Aquí es donde mañana llamaremos a OrderService para guardar en la Base de Datos.
        // Por hoy, cerramos el ciclo simulando el pago y vaciando la "taquilla".

        session.removeAttribute("carrito"); // Destruimos el carrito actual

        // Redirigimos a la portada añadiendo un "chivato" en la URL (?exito=true)
        return "redirect:/?exito=true";
    }



    @PostMapping("/cart/add/{id}")
    public String añadirAlCarrito(@PathVariable("id") Long id, HttpSession session) {

        // 1. Buscamos el producto en la base de datos
        Product productoEncontrado = productRepository.findById(id).orElse(null);

        if (productoEncontrado != null) {
            // 2. Recuperamos el carrito de la sesión actual
            // (Si es la primera vez que entra, el carrito será null)
            List<OrderDetail> carrito = (List<OrderDetail>) session.getAttribute("carrito");

            // Si no había carrito, le creamos uno nuevo (una lista vacía)
            if (carrito == null) {
                carrito = new ArrayList<>();
            }

            // 3. Comprobamos si el producto YA ESTÁ en el carrito
            boolean existe = false;
            for (OrderDetail item : carrito) {
                if (item.getProduct().getId()==(productoEncontrado.getId())) {
                    // Si ya está, le sumamos 1 a la cantidad
                    item.setQuantity(item.getQuantity() + 1);
                    // Actualizamos el precio total de esa línea (ej: 2 teles x 500€ = 1000€)
                    item.setPrice(item.getQuantity() * productoEncontrado.getPrice());
                    existe = true;
                    break;
                }
            }

            // 4. Si es un producto nuevo que no estaba en el carrito
            if (!existe) {
                OrderDetail nuevaLinea = new OrderDetail();
                nuevaLinea.setProduct(productoEncontrado);
                nuevaLinea.setQuantity(1);
                nuevaLinea.setPrice(productoEncontrado.getPrice()); // Precio base por 1 unidad

                // Lo metemos en la lista
                carrito.add(nuevaLinea);
            }

            // 5. Guardamos la lista actualizada de vuelta en la sesión (la taquilla)
            session.setAttribute("carrito", carrito);

            logger.info("🛒 Producto añadido. Total de artículos distintos en el carrito: " + carrito.size());
        }

        // Redirigimos a la portada para que siga comprando
        return "redirect:/";
    }


    @GetMapping("/cart")
    public String verCarrito(HttpSession session, Model model) {
        // 1. Sacamos el carrito de la taquilla
        List<OrderDetail> carrito = (List<OrderDetail>) session.getAttribute("carrito");

        // 2. Calculamos el total de la compra
        float total = 0;
        if (carrito != null) {
            for (OrderDetail item : carrito) {
                total += item.getPrice(); // Sumamos el subtotal de cada línea
            }
        }

        // 3. Metemos los datos en la "mochila" (Model) para que el HTML pueda leerlos
        model.addAttribute("carrito", carrito);
        model.addAttribute("total", total);

        // 4. Le decimos que cargue el archivo cart.html
        return "cart";
    }


    @GetMapping("/cart/remove/{id}")
    public String quitarDelCarrito(@PathVariable("id") Long id, HttpSession session) {

        // 1. Sacamos la mochila actual
        List<OrderDetail> carrito = (List<OrderDetail>) session.getAttribute("carrito");

        if (carrito != null) {
            // 2. Buscamos y eliminamos el producto que coincida con la ID
            // (Usamos un truco avanzado y limpio de Java llamado removeIf)
            carrito.removeIf(item -> item.getProduct().getId() == id);

            // 3. Guardamos la mochila actualizada
            session.setAttribute("carrito", carrito);
        }

        // 4. Recargamos la página del carrito para que se vea el cambio
        return "redirect:/cart";
    }



    @GetMapping("/product/{id}")
    public String verDetalleProducto(@PathVariable("id") Long id, Model model) {
        // 1. Buscamos el producto principal
        Product productoEncontrado = productRepository.findById(id).orElse(null);
        model.addAttribute("product", productoEncontrado);

        // 2. NUEVO: Buscamos TODOS los productos para la zona de recomendados
        Set<Product> recomendados = productService.findAllVisible();
        model.addAttribute("recomendados", recomendados);

        return "product";
    }


}