package com.example.newsapp.mapper;

import com.example.newsapp.dto.RegisterRequest;
import com.example.newsapp.model.Role;
import com.example.newsapp.model.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public class RegisterRequestMapper {

    public static User fromRegisterRequest(RegisterRequest request, PasswordEncoder passwordEncoder){
        if(!request.getPassword().equals(request.getPasswordConfirmation())){
            throw new IllegalArgumentException("Пароль и подтверждение не совпадают");
        }
        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Недопустимая роль. Разрешено: USER или AUTHOR");
        }
        if(role == Role.ADMIN){
            throw new IllegalArgumentException("Регистрация с ролью ADMIN запрещена");}
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isBlocked(false)
                .role(role)
                .build();
    }
}
