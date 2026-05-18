package com.example.demo.security;

import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Servicio de adaptación entre la base de datos y Spring Security.
 * Implementa la interfaz UserDetailsService requerida por el framework de seguridad
 * para el proceso de autenticación (Login).
 */
@Service
public class MyShopUserDetailsService implements UserDetailsService {

    // Se inyecta de forma 'Lazy' (perezosa) para evitar dependencias circulares 
    // al arrancar el contexto de seguridad de Spring.
    @Autowired
    @Lazy
    private UserService userService;

    /**
     * Busca al usuario en la base de datos y lo transforma en un objeto UserDetails
     * que Spring Security puede entender y validar.
     * * @param username El nombre de usuario (email o nick) introducido en el login.
     * @return Objeto UserDetails con las credenciales y roles del usuario.
     * @throws UsernameNotFoundException Si el usuario no existe en la base de datos.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Buscamos a nuestro usuario en la BD usando la lógica de negocio
        User user = userService.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Usuario/contraseña incorrectos");
        }

        // Convertimos los roles del modelo al formato de autoridades de Spring
        List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());

        // Creamos y devolvemos el usuario "oficial" de Spring Security
        return buildUserForAuthentication(user, authorities);
    }

    /**
     * Convierte el conjunto de roles de nuestra entidad a una lista de GrantedAuthority.
     */
    private List<GrantedAuthority> getUserAuthority(Set<Role> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        userRoles.forEach(role -> roles.add(new SimpleGrantedAuthority(role.getName())));
        return new ArrayList<>(roles);
    }

    /**
     * Construye el objeto UserDetails de Spring Security mapeando las propiedades de nuestra entidad.
     */
    private UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
        // Se usa el User de Spring Security (paquete explícito para evitar colisión de nombres con nuestro dominio)
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                user.isActive(), true, true, true, authorities);
    }
}