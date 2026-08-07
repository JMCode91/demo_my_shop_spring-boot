package com.example.demo.batch;

import com.example.demo.domain.Product; 
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull; 
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Componente que procesa cada línea del CSV de forma individual.
 * Aplica reglas de limpieza y soporte flexible para separadores de imágenes (comas o puntos y comas).
 */
public class ProductItemProcessor implements ItemProcessor<Product, Product> {

    @Override
    public Product process(@NonNull Product product) throws Exception { 
        
        // 1. Asignamos la fecha y visibilidad por defecto
        product.setCreationDate(LocalDate.now());
        product.setVisible(true);

        // 2. Descartamos productos sin stock
        if (product.getStock() == 0) {
            return null; 
        }

        // 3. Inicializamos la lista de galería por seguridad para evitar nulos
        product.setGallery(new ArrayList<>());

        // 4. Lógica flexible de separación de imágenes (soporta comas y puntos y comas)
        String rawImages = product.getImage();
        if (rawImages != null && !rawImages.isEmpty()) {
            // Divide la cadena usando tanto la coma (,) como el punto y coma (;)
            String[] imageUrls = rawImages.split("[,;]");
            
            // La primera imagen se queda como principal
            if (imageUrls.length > 0) {
                product.setImage(imageUrls[0].trim());
            }
            
            // Las imágenes restantes se añaden a la galería secundaria
            if (imageUrls.length > 1) {
                List<String> secondaryImages = new ArrayList<>();
                for (int i = 1; i < imageUrls.length; i++) {
                    String trimmedUrl = imageUrls[i].trim();
                    if (!trimmedUrl.isEmpty()) {
                        secondaryImages.add(trimmedUrl);
                    }
                }
                product.setGallery(secondaryImages);
            }
        }

        return product;
    }
}