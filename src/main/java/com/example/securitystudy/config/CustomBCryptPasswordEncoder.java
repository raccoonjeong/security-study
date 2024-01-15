package com.example.securitystudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class CustomBCryptPasswordEncoder extends BCryptPasswordEncoder {
}
