package com.example.demo.security;

public class Constants {

    public static final String USER_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";

    public static final String LOGIN_FAILURE_URL = "/login?error=true";
    public static final String LOGIN_URL = "/login";
    public static final String LOGIN_SUCCESS_URL = "/";

    public static final String LOGOUT_URL = "/logout";
    public static final String LOGOUT_SUCCESS_URL = "/login?logout";
    public static final String JSESSIONID = "JSESSIONID";
}


//public static final String LOGOUT_SUCCESS_URL = "/login?message=logout";