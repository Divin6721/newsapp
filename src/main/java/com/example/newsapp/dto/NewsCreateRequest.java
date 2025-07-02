package com.example.newsapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor // ✅ ОБЯЗАТЕЛЬНО для Jackson
@AllArgsConstructor
public class NewsCreateRequest {

    @NotBlank(message = "Заголовок не должен быть пустым")
    @Size(min = 5, max = 200, message = "Заголовок должен быть от 5 до 200 символов")
    private String title;

    @NotBlank(message = "Текст не должен быть пустым")
    @Size(min = 100, max = 20000, message = "Текст должен быть от 100 до 20000 символов")
    private String content;

    @NotBlank(message = "Категория не должна быть пустой")
    private String category;
}
