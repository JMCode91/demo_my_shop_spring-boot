package com.example.demo.scheduler; // ¡Ajusta tu paquete!

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MiReloj {

    private static final Logger logger = LoggerFactory.getLogger(MiReloj.class);

    // cron = "Segundos Minutos Horas Día Mes DíaSemana"
    // "*/10 * * * * *" significa: cada 10 segundos, todos los minutos, todas las horas...
    @Scheduled(cron = "*/10 * * * * *")
    public void darLaHora() {
        String horaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        logger.info("⏰ ¡Ding Dong! El reloj automático te avisa de que son las: {}", horaActual);
    }
}