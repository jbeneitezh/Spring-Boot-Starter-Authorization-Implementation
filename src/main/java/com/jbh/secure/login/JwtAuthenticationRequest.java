package com.jbh.secure.login;

import lombok.Data;

@Data
public class JwtAuthenticationRequest {
    private String email;
    private String password;
}
