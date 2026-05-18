package com.example.demo.controller;

import com.example.demo.domain.Order;
import com.example.demo.domain.User;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
 * Controlador destinado al Área Privada del usuario.
 * Gestiona la visualización del perfil, el historial de pedidos y las acciones 
 * sobre la cuenta personal (como el cambio de avatar o la gestión de favoritos).
 */
@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    /**
     * Carga el panel principal del perfil del cliente, reuniendo sus datos personales,
     * su lista de pedidos y sus productos favoritos.
     * @param principal Objeto de seguridad que contiene al usuario logueado.
     * @return Vista del perfil de usuario (myprofile).
     */
    @GetMapping("/profile")
    public String showProfile(Principal principal, Model model, HttpSession session) {
        String username = principal.getName();
        User user = userService.findByUsername(username);

        model.addAttribute("user", user);

        if (user.getImage() != null && !user.getImage().isEmpty()) {
            session.setAttribute("userAvatar", user.getImage());
        }

        List<Order> pedidos = orderService.findByUser(user);
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("totalPedidos", pedidos != null ? pedidos.size() : 0);
        
        model.addAttribute("wishlist", user.getWishlist());
        model.addAttribute("totalDeseos", user.getWishlist() != null ? user.getWishlist().size() : 0);

        return "myprofile";
    }

    /**
     * Actualiza el avatar del usuario seleccionando uno nuevo desde una API externa (DiceBear).
     * @param avatarUrl URL generada de la nueva imagen de avatar.
     */
    @PostMapping("/profile/avatar/save")
    public String saveAvatar(@RequestParam("avatarUrl") String avatarUrl, Principal principal, HttpSession session) {
        if (principal != null) {
            userService.updateAvatar(principal.getName(), avatarUrl);
            session.setAttribute("userAvatar", avatarUrl);
        }
        return "redirect:/profile";
    }

    /**
     * Alterna (añade si no está, quita si ya está) un producto de la Lista de Deseos del usuario.
     * @param productId Identificador del producto a marcar/desmarcar.
     * @param request Petición HTTP utilizada para redirigir al usuario exactamente 
     * a la página desde donde hizo clic.
     */
    @PostMapping("/wishlist/toggle/{id}")
    public String toggleWishlist(@PathVariable("id") Long productId, Principal principal, HttpServletRequest request) {
        if (principal != null) {
            String username = principal.getName();
            userService.toggleWishlist(username, productId);
        }
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
}