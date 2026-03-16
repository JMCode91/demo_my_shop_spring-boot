package com.example.demo.controller;

import com.example.demo.domain.Order;
import com.example.demo.domain.User;
import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.security.JwtRequest;
import com.example.demo.security.JwtResponse;
import com.example.demo.security.JwtTokenUtil;
import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class ApiUserController {

    private final Logger logger = LoggerFactory.getLogger(ApiUserController.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService; // Spring usará tu MyShopUserDetailsService

    // --- 1. CREAR USUARIO RECIBIENDO UN DTO ---
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<User> addUser(@RequestBody UserDTO userDTO) {

        User user = convertToEntity(userDTO);
        user.setPassword(UUID.randomUUID().toString());
        userService.add(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }


    // --- 2. DEVOLVER LISTA DE USUARIOS COMO DTOs ---
    @GetMapping(produces = "application/json")
    public ResponseEntity<List<UserDTO>> getUsers() {

        // Cambiamos Set por List para que coincida con tu UserService
        List<User> allUsers = userService.findAll();

        // Transformamos la List de User a una List de UserDTO
        List<UserDTO> dtoList = allUsers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()); // Usamos toList() en vez de toSet()

        return ResponseEntity.ok(dtoList);
    }



    // =========================================================================
    // MÉTODOS TRADUCTORES (DTO <-> Entidad)
    // =========================================================================

    private OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);

        // 1. Mapeamos el ID del usuario
        if (order.getUser() != null) {
            orderDTO.setUserId(order.getUser().getId());
        }

        // 2. Mapeamos la lista de nombres de productos
        // Navegamos: Pedido -> Detalles -> Producto -> Nombre
        if (order.getDetails() != null) {
            orderDTO.setProducts(order.getDetails().stream()
                    .map(detail -> detail.getProduct().getName())
                    .collect(Collectors.toList()));
        }

        return orderDTO;
    }

    // --- 3. OBTENER PEDIDOS DE UN USUARIO ESPECÍFICO ---
    @GetMapping(value = "/{id}/orders", produces = "application/json")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@PathVariable("id") long userId) {

        // 1. Buscamos el usuario (para asegurar que existe)
        User user = userService.findById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // 2. Obtenemos sus pedidos a través del servicio
        List<Order> orders = orderService.findByUser(user);

        // 3. Los convertimos todos a DTOs para no enviar datos sensibles o innecesarios
        List<OrderDTO> orderDTOs = orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(orderDTOs);
    }

    private UserDTO convertToDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }



    private User convertToEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }




    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException de) {
            throw new Exception("User disabled", de);
        } catch (BadCredentialsException bce) {
            throw new Exception("Invalid credentials", bce);
        }
    }


    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> login(@RequestBody JwtRequest authRequest) throws Exception {

        logger.info("BEGIN login");

        // 1. Comprobamos que el usuario y contraseña son correctos
        authenticate(authRequest.getUsername(), authRequest.getPassword());

        // 2. Si son correctos, cargamos sus datos de la base de datos
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        // 3. Generamos el token (la pulsera VIP)
        final String token = jwtTokenUtil.generateToken(userDetails);

        logger.info("END login " + token);

        // 4. Devolvemos el token metido en la "cajita" JwtResponse
        return ResponseEntity.ok(new JwtResponse(token));
    }
}