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

@Configuration
public class BatchConfiguration {

    @Bean
    @NonNull
    public FlatFileItemReader<Product> reader() {
        return new FlatFileItemReaderBuilder<Product>()
                .name("productItemReader") 
                .resource(new ClassPathResource("products.csv")) 
                .delimited() 
                // Añadimos brand y technicalDescription
                .names("name", "brand", "description", "category", "price", "discount", "taxes", "stock", "image", "technicalDescription") 
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Product.class); 
                }})
                .build();
    }

    @Bean
    @NonNull
    public ProductItemProcessor processor() {
        return new ProductItemProcessor(); 
    }

    @Bean
    public JdbcBatchItemWriter<Product> writer(@NonNull DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Product>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                // Actualizamos la SQL para que coincida con los nuevos campos
                .sql("INSERT INTO products (name, brand, description, category, price, discount, taxes, stock, image, technical_description, visible, creation_date) " +
                        "VALUES (:name, :brand, :description, :category, :price, :discount, :taxes, :stock, :image, :technicalDescription, :visible, :creationDate)")
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public Step step1(@NonNull JobRepository jobRepository, @NonNull PlatformTransactionManager transactionManager, @NonNull JdbcBatchItemWriter<Product> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Product, Product>chunk(10, transactionManager) 
                .reader(reader())
                .processor(processor())
                .writer(writer)
                .build();
    }

    @Bean
    public Job importProductJob(@NonNull JobRepository jobRepository, @NonNull JobCompletionNotificationListener listener, @NonNull Step step1) {
        // TRUCO: Le llamamos importProductJobV3 para que Spring Batch crea que es nuevo y lo ejecute
        return new JobBuilder("importProductJobV3", jobRepository)
                .listener(listener) 
                .start(step1)       
                .build();
    }
}