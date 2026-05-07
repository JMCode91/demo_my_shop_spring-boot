package com.example.demo.client;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

public class ApiClient {
    // Este es un programa independiente. ¡Simula ser la App móvil o una web externa!
    public static void main(String[] args) {

        // 1. Instanciamos nuestro "Postman" de Java
        RestTemplate restTemplate = new RestTemplate();

        // Esta es la URL de TU servidor (asegúrate de que está arrancado)
        String url = "http://localhost:8080/api/products";

        System.out.println("\n=== INICIANDO SIMULACIÓN DE CLIENTE ===\n");

        // -----------------------------------------------------------------
        // FIGURA 43: GET Directo (Nos devuelve el JSON ya convertido a Objeto)
        // -----------------------------------------------------------------
        System.out.println(">>> 1. Probando GET (Lista directa):");
        ProductClientModel[] productsArray = restTemplate.getForObject(url, ProductClientModel[].class);

        if (productsArray != null) {
            List<ProductClientModel> productList = Arrays.asList(productsArray);
            System.out.println("Se han descargado " + productList.size() + " productos.");
            System.out.println("Primer producto: " + productList.get(0).getName());
        }


        // -----------------------------------------------------------------
        // FIGURA 44: GET con ResponseEntity (Para ver el código HTTP 200, 404...)
        // -----------------------------------------------------------------
        System.out.println("\n>>> 2. Probando GET (Con ResponseEntity):");
        ResponseEntity<ProductClientModel[]> responseEntity = restTemplate.getForEntity(url, ProductClientModel[].class);

        System.out.println("Código de estado HTTP devuelto: " + responseEntity.getStatusCode());
        if (responseEntity.getBody() != null) {
            System.out.println("Cuerpo recibido correctamente.");
        }


        // -----------------------------------------------------------------
        // FIGURA 45: POST (Crear un producto nuevo desde el cliente)
        // -----------------------------------------------------------------
        System.out.println("\n>>> 3. Probando POST (Crear nuevo producto):");

        // Preparamos el producto que queremos enviar
        ProductClientModel nuevoProducto = new ProductClientModel();
        nuevoProducto.setName("Teclado Mecánico de Consola");
        nuevoProducto.setPrice(85.50f);
        nuevoProducto.setCategory("Periféricos");

        // Hacemos el envío (POST)
        ResponseEntity<ProductClientModel> postResponse = restTemplate.postForEntity(url, nuevoProducto, ProductClientModel.class);

        System.out.println("Código de estado al crear: " + postResponse.getStatusCode()); // Debería ser 201 CREATED
        
        // CORRECCIÓN: Guardamos el cuerpo y evitamos cualquier nulo al imprimir
        ProductClientModel cuerpo = postResponse.getBody();
        if (cuerpo != null) {
            System.out.println("Producto creado en el servidor con ID: " + String.valueOf(cuerpo.getId()));
        } else {
            System.out.println("El producto se creó, pero el servidor no devolvió el cuerpo.");
        }

        System.out.println("\n=== SIMULACIÓN TERMINADA ===");
    }
}
