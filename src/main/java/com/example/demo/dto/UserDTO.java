package com.example.demo.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String name;
    private String surname;
    private String email;
    private String address;
    private String city;
    private String postalCode;
    private String province;
    private String country;
}