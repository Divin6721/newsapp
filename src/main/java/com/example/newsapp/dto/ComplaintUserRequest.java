package com.example.newsapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintUserRequest {


    @NotBlank(message = "Текст не должен быть пустым")
    @Size(min = 100, max = 10000, message = "Комментарий должен быть от 100 до 10000 символов")
    private String content;

    @NotNull(message = "ID новости обязателен")
    private Long newsId;





}
