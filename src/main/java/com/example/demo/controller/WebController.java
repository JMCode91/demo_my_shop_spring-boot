package com.example.demo.controller;

import com.example.demo.domain.Product;
import com.example.demo.domain.User;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class WebController {

    private final Logger logger = LoggerFactory.getLogger(WebController.class);

    private final ProductService productService;

    public WebController(@NonNull ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    @PostMapping("/")
    public String index(Model model) {

        //aqui ponemos el logger
        logger.info("👋 El usuario ha entrado en la Home y vamos a buscar productos...");

        Set<Product> products = productService.findAllVisible();
        model.addAttribute("products", products);
        return "index";
    }

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/admin/panel")
    public String mostrarPanelAdmin(Model model) {
// Traemos a todos los usuarios de la base de datos
        List<User> listaUsuarios =(List<User>) userRepository.findAll();
// Metemos la lista en el "saquito" llamado 'usuarios' para pasárselo al HTML
        model.addAttribute("usuarios", listaUsuarios);

        // Traemos los productos y los metemos en su saquito
        List<Product> listaProductos = (List<Product>) productRepository.findAll();
        model.addAttribute("productos", listaProductos);

        return "admin"; // Esto llama a tu admin.html
    }

    @RequestMapping("/checkout")
    public String checkout(Model model) {
        return "checkout";
    }


    @GetMapping("/admin/products/new")
    public String formularioNuevoProducto(Model model) {
        // Enviamos un objeto Producto vacío para que el formulario lo rellene
        model.addAttribute("product", new Product());
        return "new-product";
    }


    @PostMapping("/admin/products/save")
    public String guardarProducto(Product product, @RequestParam("imageFile") MultipartFile imageFile) {

        // 1. Comprobamos si el usuario ha subido alguna imagen
        if (!imageFile.isEmpty()) {
            try {
                // 2. Le decimos en qué carpeta exacta de tu proyecto queremos guardar la foto
                String carpetaDestino = "src/main/resources/static/images/";

                // 3. Obtenemos el nombre del archivo (ejemplo: "raton.jpg")
                String nombreArchivo = imageFile.getOriginalFilename();

                // 4. Creamos la ruta completa y copiamos el archivo físico allí
                Path rutaCompleta = Paths.get(carpetaDestino + nombreArchivo);
                Files.write(rutaCompleta, imageFile.getBytes());

                // 5. Guardamos SOLO EL NOMBRE en el objeto producto para la base de datos
                product.setImage(nombreArchivo);

            } catch (IOException e) {
                e.printStackTrace();
                // Aquí podríamos mostrar un error si falla, pero por ahora lo dejamos simple
            }
        }

        // 6. Guardamos el producto (con sus textos y el nombre de la imagen) en MySQL
        productService.save(product);

        return "redirect:/admin/panel";
    }

    @PostMapping("/admin/products/delete/{id}")
    public String borrarProducto(@PathVariable("id") Long id) {
        // Usamos el repositorio que inyectamos antes para fulminar el producto por su ID
        productRepository.deleteById(id);

        // Recargamos el panel de admin mágicamente
        return "redirect:/admin/panel";
    }

    @GetMapping("/admin/products/edit/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
        // 1. Buscamos el producto en la base de datos por su ID
        Product productoAEditar = productRepository.findById(id).orElse(null);

        // 2. Lo metemos en el saquito (modelo) usando el mismo nombre "product" que ya usa tu formulario
        model.addAttribute("product", productoAEditar);

        // 3. ¡Magia! Reutilizamos la misma vista HTML que usamos para crear
        return "new-product";
    }


}
