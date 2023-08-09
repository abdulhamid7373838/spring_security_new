package com.example.spring_security_new.payload;

import lombok.Data;

@Data
public class LoginDto {

    private String username;
    private String password;
}
