package com.example.demo.controller;

import com.example.demo.domain.Product;
import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase de intercepción global para controladores.
 * Los métodos anotados aquí se ejecutarán automáticamente de forma transversal
 * en todas las peticiones, permitiendo inyectar variables globales en las plantillas.
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    /**
     * Método global que comprueba si hay un usuario logueado en cada petición.
     * Si lo hay, extrae los identificadores de sus productos favoritos y su avatar 
     * para que la cabecera (Header) de la web se renderice correctamente en todas las páginas.
     * @return Lista con los IDs de los productos en la Wishlist del usuario.
     */
    @ModelAttribute("userWishlistIds")
    public List<Long> populateWishlist(Principal principal, HttpSession session) {
        if (principal != null) {
            User user = userService.findByUsername(principal.getName());
            if (user != null) {
                
                if (session.getAttribute("userAvatar") == null && user.getImage() != null && !user.getImage().isEmpty()) {
                    session.setAttribute("userAvatar", user.getImage());
                }

                if (user.getWishlist() != null) {
                    return user.getWishlist().stream()
                            .map(Product::getId)
                            .collect(Collectors.toList());
                }
            }
        }
        return new ArrayList<>();
    }
}