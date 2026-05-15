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


    // Este método se ejecuta AUTOMÁTICAMENTE antes de cargar cualquier página HTML.
    // Inyecta una variable llamada "userWishlistIds" en todas las vistas Thymeleaf.
    @org.springframework.web.bind.annotation.ModelAttribute("userWishlistIds")
    public List<Long> populateWishlist(java.security.Principal principal, HttpSession session) { // NUEVO: Añadimos HttpSession
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            if (user != null) {
                
                // NUEVO: Solución al Bug del Header.
                // Si el usuario tiene sesión iniciada, pero su avatar aún no está en la memoria (ej. acaba de hacer login),
                // lo leemos del objeto 'user' que ya hemos sacado de la base de datos y lo guardamos.
                if (session.getAttribute("userAvatar") == null && user.getImage() != null && !user.getImage().isEmpty()) {
                    session.setAttribute("userAvatar", user.getImage());
                }

                if (user.getWishlist() != null) {
                    // Si hay usuario, devolvemos una lista solo con los IDs de sus productos favoritos
                    return user.getWishlist().stream()
                            .map(Product::getId)
                            .collect(java.util.stream.Collectors.toList());
                }
            }
        }
        // Si no está logueado o no tiene favoritos, devolvemos una lista vacía
        return new java.util.ArrayList<>();
    }

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
    public String showProfile(java.security.Principal principal, Model model, HttpSession session) { // NUEVO: Añadido parámetro HttpSession
        String username = principal.getName();
        User user = userService.findByUsername(username);

        model.addAttribute("user", user);

        // NUEVO: Guardar el avatar en la sesión de memoria para que el header pueda leerlo en cualquier página
        if (user.getImage() != null && !user.getImage().isEmpty()) {
            session.setAttribute("userAvatar", user.getImage());
        }

        // 1. Datos de Pedidos (Intacto)
        List<Order> pedidos = orderService.findByUser(user);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("totalPedidos", pedidos != null ? pedidos.size() : 0);
        
        // 2. Datos de la Lista de Deseos (Intacto)
        model.addAttribute("wishlist", user.getWishlist());
        model.addAttribute("totalDeseos", user.getWishlist() != null ? user.getWishlist().size() : 0);

        return "myprofile";
    }


    // ==========================================
    // INTEGRACIÓN API EXTERNA: Galeria de avatares
    // ==========================================

    // Endpoint para guardar el avatar desde la galería
    @PostMapping("/profile/avatar/save")
public String saveAvatar(@RequestParam("avatarUrl") String avatarUrl, java.security.Principal principal, HttpSession session) {
    if (principal != null) {
        // Invocamos el nuevo método atómico del Service
        userService.updateAvatar(principal.getName(), avatarUrl);
        
        // Actualizamos la sesión para el Header
        session.setAttribute("userAvatar", avatarUrl);
    }
    return "redirect:/profile";
}


    // 4. Endpoint para AÑADIR/QUITAR de la Lista de Deseos
    @PostMapping("/wishlist/toggle/{id}")
    public String toggleWishlist(@PathVariable("id") Long productId, java.security.Principal principal, jakarta.servlet.http.HttpServletRequest request) {
        
        // 1. Verificamos que haya un usuario conectado
        if (principal != null) {
            String username = principal.getName();
            // 2. Llamamos a la lógica de negocio que construimos antes
            userService.toggleWishlist(username, productId);
        }

        // 3. Truco de Arquitectura: Redirigimos al usuario a la página exacta desde donde hizo clic.
        // Esto es útil porque el usuario puede dar "Me gusta" desde el catálogo o desde la ficha del producto.
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }


    // ==========================================
    // 5. PÁGINAS LEGALES Y DE CONTACTO
    // ==========================================
    
    @GetMapping("/aviso-legal")
    public String showAvisoLegal(Model model) {
        model.addAttribute("pageTitle", "Aviso Legal");
        return "aviso-legal";
    }

    @GetMapping("/privacidad")
    public String showPrivacidad(Model model) {
        model.addAttribute("pageTitle", "Política de Privacidad");
        return "privacidad";
    }

    @GetMapping("/contacto")
    public String showContacto(Model model) {
        model.addAttribute("pageTitle", "Contacto");
        return "contacto";
    }

    @GetMapping("/centro-ayuda")
    public String showCentroAyuda(Model model) {
        model.addAttribute("pageTitle", "Centro de Ayuda");
        return "centro-ayuda";
    }

    @GetMapping("/devoluciones")
    public String showDevoluciones(Model model) {
        model.addAttribute("pageTitle", "Devoluciones");
        return "devoluciones";
    }

    @GetMapping("/envios")
    public String showEnvios(Model model) {
        model.addAttribute("pageTitle", "Envíos y Entregas");
        return "envios";
    }

    @GetMapping("/condiciones-compra")
    public String showCondicionesCompra(Model model) {
        model.addAttribute("pageTitle", "Condiciones de Compra");
        return "condiciones-compra";
    }

    @GetMapping("/cookies")
    public String showCookies(Model model) {
        model.addAttribute("pageTitle", "Política de Cookies");
        return "cookies";
    }

    // 6. Endpoint para la página de OFERTAS
    @GetMapping("/ofertas")
    public String showOfertas(org.springframework.ui.Model model) {
        // 1. Llamamos a nuestro servicio que ya filtra por descuento y visibilidad
        List<Product> ofertas = productService.getOfertasActivas();
        
        // 2. Inyectamos los productos rebajados en el modelo
        model.addAttribute("products", ofertas);
        
        // 3. Cambiamos el título de la página dinámicamente para reutilizar catalog.html
        model.addAttribute("pageTitle", "Ofertas Especiales 💥");
        
        // 4. Devolvemos la vista del catálogo
        return "catalog";
    }

}