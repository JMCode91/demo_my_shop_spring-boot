package com.example.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class KeepAliveService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Se ejecuta cada 10 minutos (600000 milisegundos)
    @Scheduled(fixedRate = 600000)
    public void pingDatabase() {
        try {
            jdbcTemplate.execute("SELECT 1");
            System.out.println("💓 Latido enviado: Aiven sigue despierto.");
        } catch (Exception e) {
            System.out.println("⚠️ Error en el latido.");
        }
    }
}