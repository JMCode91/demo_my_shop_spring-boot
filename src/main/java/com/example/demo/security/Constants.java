package com.example.demo.security;

public class Constants {

    public static final String USER_ROLE = "user";
    public static final String ADMIN_ROLE = "admin";

    public static final String LOGIN_FAILURE_URL = "/login?error=true";
    public static final String LOGIN_URL = "/login";
    public static final String LOGIN_SUCCESS_URL = "/";

    public static final String LOGOUT_URL = "/logout";
    public static final String LOGOUT_SUCCESS_URL = "/?message=logout";
    public static final String JSESSIONID = "JSESSIONID";
}