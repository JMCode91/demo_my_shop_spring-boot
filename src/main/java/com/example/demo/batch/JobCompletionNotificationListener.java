package com.example.demo.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull; // Importación necesaria
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void afterJob(@NonNull JobExecution jobExecution) { // Etiqueta añadida aquí
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            logger.info("✅ ¡EL TRABAJO BATCH HA TERMINADO CON ÉXITO!");

            // Hacemos una consulta rápida para ver cuántos productos hay ahora
            jdbcTemplate.query("SELECT COUNT(*) FROM products",
                    (rs, row) -> rs.getInt(1)
            ).forEach(count -> logger.info("📊 Ahora mismo hay un total de {} productos en la base de datos.", count));
        }
    }
}