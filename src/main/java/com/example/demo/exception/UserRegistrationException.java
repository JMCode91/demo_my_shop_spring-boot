package com.example.demo.exception;

/**
 * Excepción personalizada que encapsula cualquier error ocurrido durante
 * el proceso de registro de un nuevo cliente (duplicidad de email, fallo de validación, etc.).
 * Es capturada por el AuthController para mostrar un mensaje amigable en el formulario.
 */
public class UserRegistrationException extends RuntimeException {

    public UserRegistrationException(String message) {
        super(message);
    }
}