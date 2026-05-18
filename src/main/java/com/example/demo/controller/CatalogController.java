package com.example.demo.controller;

import com.example.demo.domain.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * Controlador responsable de la visualización pública del escaparate de la tienda.
 * Gestiona el buscador, los filtros, la navegación por categorías, la sección de ofertas
 * y la ficha detallada de cada producto.
 */
@Controller
public class CatalogController {

    @Autowired
    private ProductService productService;

    /**
     * Motor de búsqueda y filtrado avanzado del catálogo.
     * @param query Texto a buscar en el nombre o descripción del producto.
     * @param category Categoría seleccionada.
     * @param brands Lista de marcas seleccionadas en los filtros.
     * @param maxPrice Precio máximo definido en el rango.
     * @param model Modelo para inyectar los resultados y mantener el estado de los filtros.
     * @return Vista del catálogo con los productos filtrados.
     */
    @GetMapping("/search")
    public String search(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "brands", required = false) List<String> brands,
            @RequestParam(value = "maxPrice", required = false) Float maxPrice,
            Model model) {

        List<Product> products = productService.searchAndFilter(query, category, brands, maxPrice);

        if (query != null && !query.isEmpty()) {
            model.addAttribute("pageTitle", "Resultados para: '" + query + "'");
        } else if (category != null && !category.isEmpty()) {
            model.addAttribute("pageTitle", "Categoría: " + category.toUpperCase());
        } else {
            model.addAttribute("pageTitle", "Catálogo completo");
        }
        
        model.addAttribute("products", products);
        model.addAttribute("currentCategory", category);
        model.addAttribute("currentQuery", query);
        
        return "catalog"; 
    }

    /**
     * Muestra todos los productos pertenecientes a una categoría específica.
     * @param cat Identificador de la categoría extraído de la URL.
     * @param model Modelo para inyectar la lista de productos.
     * @return Vista del catálogo.
     */
    @GetMapping("/category/{cat}")
    public String showCategory(@PathVariable("cat") String cat, Model model) {
        List<Product> products = productService.findByCategory(cat);
        String tituloCategoria = cat.substring(0, 1).toUpperCase() + cat.substring(1).replace("-", " ");
        
        model.addAttribute("pageTitle", "Categoría: " + tituloCategoria);
        model.addAttribute("products", products);
        return "catalog"; 
    }

    /**
     * Muestra una página exclusiva con todos los productos que tienen algún descuento aplicado.
     * @return Vista del catálogo adaptada para ofertas.
     */
    @GetMapping("/ofertas")
    public String showOfertas(Model model) {
        List<Product> ofertas = productService.getOfertasActivas();
        model.addAttribute("products", ofertas);
        model.addAttribute("pageTitle", "Ofertas Especiales 💥");
        return "catalog";
    }

    /**
     * Carga la ficha de detalle de un producto específico y calcula un algoritmo
     * de recomendaciones basadas en la misma categoría del producto visualizado.
     * @param id Identificador único del producto.
     * @return Vista de detalle del producto.
     */
    @GetMapping("/product/{id}")
    public String verDetalleProducto(@PathVariable("id") Long id, Model model) {
        Product productoEncontrado = productService.findById(id);
        model.addAttribute("product", productoEncontrado);

        List<Product> recomendados = productService.findByCategory(productoEncontrado.getCategory())
                .stream()
                .filter(p -> p.getId() != id)
                .limit(8)
                .collect(Collectors.toCollection(ArrayList::new));

        if (recomendados.size() < 4) {
            List<Product> extras = new ArrayList<>(productService.findAllVisible());
            final List<Product> actuales = recomendados; 
            
            List<Product> adicionales = extras.stream()
                    .filter(p -> p.getId() != id && !actuales.contains(p))
                    .limit(8 - actuales.size())
                    .collect(Collectors.toList());
            
            recomendados.addAll(adicionales);
        }

        model.addAttribute("recomendados", recomendados);
        return "product";
    }
}