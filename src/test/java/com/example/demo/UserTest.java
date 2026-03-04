package com.example.demo; // Cambia esto si tu paquete se llama distinto


import com.example.demo.domain.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {

    // La etiqueta @Test es la que le dice a JUnit "Oye, ejecuta este método como una prueba"
    @Test
    public void comprobarQueElNombreSeGuardaCorrectamente() {

        // 1. PREPARACIÓN (Arrange): Creamos un objeto de prueba
        User usuarioPrueba = new User();

        // 2. ACCIÓN (Act): Hacemos algo con ese objeto
        usuarioPrueba.setUsername("Batman");

        // 3. COMPROBACIÓN (Assert): JUnit comprueba si el resultado es el que esperábamos
        // La estructura es: assertEquals(LO_QUE_ESPERAS, LO_QUE_REALMENTE_HAY, "Mensaje si falla")
        assertEquals("Superman", usuarioPrueba.getUsername(), "El nombre del usuario debería ser Batman");
    }
}