package com.example.newsapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    @NotBlank(message = "Комментарий не должен быть пустым")
    @Size(min = 3, max = 500, message = "Комментарий должен быть от 3 до 500 символов")
    private String content;
}
