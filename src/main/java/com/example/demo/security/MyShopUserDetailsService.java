package com.example.demo.security;

import com.example.demo.domain.User;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class MyShopUserDetailsService implements UserDetailsService {

    // Fíjate que aquí también he cambiado el nombre a MyShopUserDetailsService.class
    private Logger logger = LoggerFactory.getLogger(MyShopUserDetailsService.class);

    @Autowired
    @Lazy
    private UserService userService;



    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Buscamos a nuestro usuario en la BD usando tu UserService
        User user = userService.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("Usuario/contraseña incorrectos");
        }

        // Convertimos los roles al formato que entiende Spring
        List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());

        // Creamos y devolvemos el usuario "oficial" de Spring Security
        return buildUserForAuthentication(user, authorities);
    }

    private List<GrantedAuthority> getUserAuthority(Set<Role> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        userRoles.forEach(role -> roles.add(new SimpleGrantedAuthority(role.getName())));
        return new ArrayList<>(roles);
    }

    private UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
        // CUIDADO AQUÍ: la ruta "org.springframework.security.core.userdetails.User" es intencional
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(),
                user.isActive(), true, true, true, authorities);
    }
}