package com.example.demo.batch;

import com.example.demo.domain.Product; 
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull; 
import java.time.LocalDate;

/**
 * Componente que procesa cada línea del CSV de forma individual.
 * Aquí se aplican las reglas de limpieza y validación de datos (Filtros) 
 * antes de la inserción en la base de datos.
 */
public class ProductItemProcessor implements ItemProcessor<Product, Product> {

    /**
     * Transforma el producto entrante.
     * @param product El producto tal y como se leyó del archivo CSV.
     * @return El producto modificado, o 'null' si se debe descartar y no guardar.
     */
    @Override
    public Product process(@NonNull Product product) throws Exception { 
        
        // 1. Asignamos la fecha del sistema en el momento de la importación
        product.setCreationDate(LocalDate.now());
        
        // 2. Por defecto, aseguramos que el producto esté visible en el catálogo
        product.setVisible(true);

        // 3. Regla de Negocio: No importamos productos que no tengan stock inicial
        if (product.getStock() == 0) {
            return null; // Devolver null le indica a Spring Batch que salte este registro
        }

        return product;
    }
}