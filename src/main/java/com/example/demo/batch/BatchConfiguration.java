package com.example.demo.batch;

import com.example.demo.domain.Product;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
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

import javax.sql.DataSource;

/**
 * Configuración principal del proceso de importación por lotes (Spring Batch).
 * Define la tubería ETL (Extract, Transform, Load) para leer productos desde un archivo CSV,
 * procesarlos y guardarlos masivamente en la base de datos.
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
     * TRANSFORM (Procesador): Aplica la lógica de negocio a cada producto leído 
     * antes de guardarlo en la base de datos.
     */
    @Bean
    @NonNull
    public ProductItemProcessor processor() {
        return new ProductItemProcessor(); 
    }

    /**
     * LOAD (Escritor): Inserta los productos procesados en la tabla 'products' de la base de datos.
     * Utiliza JdbcBatchItemWriter para optimizar las inserciones masivas (Bulk Insert).
     */
    @Bean
    public JdbcBatchItemWriter<Product> writer(@NonNull DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Product>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO products (name, brand, description, category, price, discount, taxes, stock, image, technical_description, visible, creation_date) " +
                        "VALUES (:name, :brand, :description, :category, :price, :discount, :taxes, :stock, :image, :technicalDescription, :visible, :creationDate)")
                .dataSource(dataSource)
                .build();
    }

    /**
     * Define un "Paso" (Step) del trabajo. Agrupa el lector, el procesador y el escritor.
     * Se configura para procesar la información en bloques (chunks) de 10 en 10
     * para no saturar la memoria RAM del servidor.
     */
    @Bean
    public Step step1(@NonNull JobRepository jobRepository, @NonNull PlatformTransactionManager transactionManager, @NonNull JdbcBatchItemWriter<Product> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Product, Product>chunk(10, transactionManager) 
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }

    /**
     * Define el "Trabajo" (Job) completo de importación.
     * Escucha los eventos de finalización para mostrar el resumen en los logs.
     */
    @Bean
    public Job importProductJob(@NonNull JobRepository jobRepository, @NonNull JobCompletionNotificationListener listener, @NonNull Step step1) {
        return new JobBuilder("importProductJobV3", jobRepository)
                .listener(listener) 
                .start(step1)       
                .build();
    }
}