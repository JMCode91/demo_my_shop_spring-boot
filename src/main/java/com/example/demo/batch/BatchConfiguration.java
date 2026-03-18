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
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class BatchConfiguration {

    // ==========================================
    // TRABAJADOR 1: EL LECTOR (Reader)
    // ==========================================
    @Bean
    public FlatFileItemReader<Product> reader() {
        return new FlatFileItemReaderBuilder<Product>()
                .name("productItemReader") // Le damos un nombre interno
                .resource(new ClassPathResource("products.csv")) // Le decimos dónde está el archivo
                .delimited() // Le indicamos que es un archivo separado por comas
                .names("name", "description", "category", "price", "discount", "taxes", "stock") // Los nombres de las columnas
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Product.class); // Le decimos que convierta cada línea en un objeto Product
                }})
                .build();
    }

    // ==========================================
    // TRABAJADOR 2: EL TRANSFORMADOR (Processor)
    // ==========================================
    @Bean
    public ProductItemProcessor processor() {
        return new ProductItemProcessor(); // Este es el que tú creaste antes
    }

    // ==========================================
    // TRABAJADOR 3: EL ESCRITOR (Writer)
    // ==========================================
    @Bean
    public JdbcBatchItemWriter<Product> writer(DataSource dataSource) {
        // En Spring Batch 5, le pasamos el DataSource (tu conexión a la BD) directamente
        return new JdbcBatchItemWriterBuilder<Product>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO products (name, description, category, price, discount, taxes, stock, creation_date) " +
                        "VALUES (:name, :description, :category, :price, :discount, :taxes, :stock, :creationDate)")
                .dataSource(dataSource)
                .build();
    }


    @Bean
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager, JdbcBatchItemWriter<Product> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Product, Product>chunk(10, transactionManager) // Procesamos los datos de 10 en 10
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }

    // ==========================================
    // EL TRABAJO COMPLETO (El Job)
    // ==========================================
    @Bean
    public Job importProductJob(JobRepository jobRepository, JobCompletionNotificationListener listener, Step step1) {
        return new JobBuilder("importProductJob", jobRepository)
                .listener(listener) // Le ponemos el "chivato" que nos avisará cuando acabe
                .start(step1)       // Arrancamos el paso 1
                .build();
    }}