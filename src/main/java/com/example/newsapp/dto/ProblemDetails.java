package com.example.newsapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Стандартная структура ошибки")
public class ProblemDetails {

    @Schema(example = "400", description = "HTTP статус ошибки")
    private int status;

    @Schema(example = "Ошибка валидации", description = "Краткое описание ошибки")
    private String title;

    @Schema(example = "Поле 'email' не должно быть пустым", description = "Подробности об ошибке")
    private String detail;

    @Schema(example = "/api/register", description = "URI, где произошла ошибка")
    private String instance;

    @Schema(example = "validation_error", description = "Машиночитаемый код ошибки")
    private String type;


    public ProblemDetails(int status, String title, String detail, String instance) {
        this.status = status;
        this.title = title;
        this.detail = detail;
        this.instance = instance;
    }
}
