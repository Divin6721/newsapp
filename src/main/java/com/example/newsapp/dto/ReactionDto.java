package com.example.newsapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class ReactionDto {

    private Long id;

    private String reactionType;

    private String userName;

    private Long newsId;
}
