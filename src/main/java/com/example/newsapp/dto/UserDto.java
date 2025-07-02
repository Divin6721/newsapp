package com.example.newsapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 3, max = 10, message = "Имя должно быть от 3 до 10 символов")
    private String name;

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Неверный формат Email")
    private String email;


    @NotBlank(message = "Роль не должна быть пустой")
    private String role;

    private Boolean isBlocked;
}
