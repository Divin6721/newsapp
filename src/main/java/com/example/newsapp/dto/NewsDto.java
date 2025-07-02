package com.example.newsapp.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor // ✅ ОБЯЗАТЕЛЬНО для Jackson
@AllArgsConstructor
public class NewsDto {

    private Long id;

    private String title;

    private String content;

    private String authorName;

    private String category;

    private Integer views;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime createdAt;
}
