package com.example.demo.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull; 
import org.springframework.stereotype.Component;

/**
 * Observador (Listener) que se ejecuta automáticamente cuando el trabajo 
 * de Spring Batch termina su ejecución. Se utiliza para generar reportes
 * y auditorías en los logs del servidor.
 */
@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

    private static final Logger logger = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Método que se dispara al finalizar el Job.
     * Comprueba el estado final y realiza una consulta directa a la BD 
     * para auditar el total de productos importados.
     */
    @Override
    public void afterJob(@NonNull JobExecution jobExecution) { 
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            logger.info("✅ ¡EL TRABAJO BATCH HA TERMINADO CON ÉXITO!");

            jdbcTemplate.query("SELECT COUNT(*) FROM products",
                    (rs, row) -> rs.getInt(1)
            ).forEach(count -> logger.info("📊 Ahora mismo hay un total de {} productos en la base de datos.", count));
        }
    }
}