package com.example.newsapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank
    @Size(min =3, message = " не должено быть короче 3 символов")
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6, message = " должен быть не короче 6 символов")
    private String password;

    @NotBlank
    private String passwordConfirmation;

    private String role; // 'USER' или 'AUTHOR'
}

