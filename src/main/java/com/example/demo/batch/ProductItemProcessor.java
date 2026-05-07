package com.example.demo.batch;

import com.example.demo.domain.Product; 
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull; // Importación añadida
import java.time.LocalDate;

public class ProductItemProcessor implements ItemProcessor<Product, Product> {

    @Override
    public Product process(@NonNull Product product) throws Exception { // Etiqueta añadida
        // 1. Le ponemos la fecha actual de creación
        product.setCreationDate(LocalDate.now());
        
        // HACEMOS QUE TODOS LOS PRODUCTOS IMPORTADOS SEAN VISIBLES POR DEFECTO
        product.setVisible(true);

        // 2. Si el stock es 0, lo descartamos (devolvemos null)
        if (product.getStock() == 0) {
            return null; // El escritor ignorará este producto
        }

        // 3. Si todo está bien, pasamos el producto al siguiente paso (el escritor)
        return product;
    }
}