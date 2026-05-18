package com.example.demo.security;

/**
 * Clase utilitaria que almacena las variables constantes relacionadas 
 * con la seguridad, rutas y roles del sistema. 
 * Evita el uso de "Magic Strings" dispersos por el código.
 */
public class Constants {

    // ==========================================
    // ROLES DEL SISTEMA
    // ==========================================
    public static final String USER_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";

    // ==========================================
    // RUTAS DE AUTENTICACIÓN
    // ==========================================
    public static final String LOGIN_URL = "/login";
    public static final String LOGIN_SUCCESS_URL = "/";
    public static final String LOGIN_FAILURE_URL = "/login?error=true";

    public static final String LOGOUT_URL = "/logout";
    public static final String LOGOUT_SUCCESS_URL = "/login?logout";
    
    // ==========================================
    // SESIÓN HTTP
    // ==========================================
    public static final String JSESSIONID = "JSESSIONID";
    
    // Constructor privado para evitar instanciación de clase utilitaria
    private Constants() {
        throw new UnsupportedOperationException("Esta es una clase de utilidades y no puede ser instanciada");
    }
}