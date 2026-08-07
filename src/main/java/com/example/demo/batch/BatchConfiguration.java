package com.example.demo.batch;

import com.example.demo.domain.Product;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

/**
 * Configuración principal del proceso de importación por lotes (Spring Batch).
 * Utiliza JpaItemWriter para asegurar que se respeten las relaciones de JPA (como @ElementCollection).
 */
@Configuration
public class BatchConfiguration {

    /**
     * EXTRACT (Lector): Lee el archivo products.csv línea por línea y mapea 
     * cada columna a las propiedades del objeto Product.
     */
    @Bean
    @NonNull
    public FlatFileItemReader<Product> reader() {
        return new FlatFileItemReaderBuilder<Product>()
                .name("productItemReader") 
                .resource(new ClassPathResource("products.csv")) 
                .delimited() 
                .names("name", "brand", "description", "category", "price", "discount", "taxes", "stock", "image", "technicalDescription") 
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Product.class); 
                }})
                .build();
    }

    /**
     * TRANSFORM (Procesador): Aplica la lógica de negocio y separa las imágenes.
     */
    @Bean
    @NonNull
    public ProductItemProcessor processor() {
        return new ProductItemProcessor(); 
    }

    /**
     * LOAD (Escritor JPA): Inserta los productos y sus colecciones asociadas (@ElementCollection)
     */
    @Bean
    public JpaItemWriter<Product> writer(@NonNull EntityManagerFactory entityManagerFactory) {
        return new JpaItemWriterBuilder<Product>()
                .entityManagerFactory(entityManagerFactory)
                .usePersist(true) // ¡LA LÍNEA MÁGICA! Obliga a Hibernate a comportarse como el formulario web
                .build();
    }

    /**
     * Define el "Paso" (Step) del trabajo agrupando lector, procesador y escritor en bloques de 10.
     */
    @Bean
    public Step step1(@NonNull JobRepository jobRepository, @NonNull PlatformTransactionManager transactionManager, @NonNull JpaItemWriter<Product> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Product, Product>chunk(10, transactionManager) 
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }

    /**
     * Define el "Trabajo" (Job) completo de importación.
     */
    @Bean
    public Job importProductJob(@NonNull JobRepository jobRepository, @NonNull JobCompletionNotificationListener listener, @NonNull Step step1) {
        return new JobBuilder("importProductJobV3", jobRepository)
                .listener(listener) 
                .start(step1)      
                .build();
    }
}