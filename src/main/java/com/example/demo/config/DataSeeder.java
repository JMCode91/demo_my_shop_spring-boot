package com.example.demo.config;

import com.example.demo.domain.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Inyectamos tus dos repositorios y el encriptador de contraseñas
    public DataSeeder(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {

        // 1. Gestionar el Rol ADMIN de forma segura
        Role adminRole = roleRepository.findByName("ADMIN");
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole = roleRepository.save(adminRole);
            System.out.println("🛡️ Rol ADMIN creado en la base de datos.");
        }

        // 2. Gestionar el Rol USER (para cuando se registren clientes normales)
        Role userRole = roleRepository.findByName("USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("USER");
            roleRepository.save(userRole);
            System.out.println("👤 Rol USER creado en la base de datos.");
        }

        // 3. Crear el usuario administrador si no existe
        if (userRepository.findByUsername("admin") == null) {
            System.out.println("🌱 Usuario admin no detectado. Creando administrador por defecto...");

            User adminUser = new User();
            adminUser.setUsername("admin");
            // ¡Importantísimo encriptar la contraseña para que Spring Security te deje entrar!
            adminUser.setPassword(passwordEncoder.encode("1234"));
            adminUser.setName("Administrador");
            adminUser.setEmail("admin@myshop.com");
            adminUser.setActive(true);
            adminUser.setCreationDate(LocalDate.now());

            // Le asignamos el rol que hemos rescatado/creado en el paso 1
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            adminUser.setRoles(roles);

            userRepository.save(adminUser);
            System.out.println("🚀 ¡Usuario Administrador (admin / 1234) creado con éxito!");
        } else {
            System.out.println("✅ La base de datos ya está inicializada. No se requiere sembrado.");
        }
    }
}