package com.example.demo.exception;

/**
 * Excepción personalizada que se lanza cuando se intenta acceder a un producto
 * que no existe en el catálogo de la base de datos.
 * Normalmente capturada por los controladores para redirigir a una página de error 404.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException() {
        super();
    }

    public ProductNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor recomendado para búsquedas por ID fallidas.
     * @param id Identificador del producto que no se ha encontrado.
     */
    public ProductNotFoundException(long id) {
        super("Product not found: " + id);
    }
}