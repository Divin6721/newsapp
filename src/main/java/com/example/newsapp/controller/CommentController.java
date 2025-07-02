package com.example.newsapp.controller;

import com.example.newsapp.dto.CommentCreateRequest;
import com.example.newsapp.dto.CommentDto;
import com.example.newsapp.dto.ProblemDetails;
import com.example.newsapp.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;


    @Operation(summary = "Создание комментария к новости",
            description = "Позволяет пользователю оставить комментарий к конкретной новости")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Комментарий успешно создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    )),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Новость не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @PostMapping("/{newsId}")
    ResponseEntity <CommentDto> createComment(@Valid @RequestBody CommentCreateRequest dto,
                                                                 @PathVariable Long newsId,
                                                                 Authentication authentication){
        CommentDto createdComment = commentService.createComment(dto, newsId, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }


    @Operation(summary = "Удаление комментария к новости",
            description = "Позволяет пользователю удалять комментарий к конкретной новости")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Комментарий успешно удален"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    )),
            @ApiResponse(responseCode = "403", description = "Нет прав для удаления комментария",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    )),
            @ApiResponse(responseCode = "404", description = "Комментарий не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @DeleteMapping("/{commentId}")
    ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                                           Authentication authentication){
        commentService.deleteComment(commentId,authentication);
        return ResponseEntity.noContent().build();

    }


    @Operation(summary = "Фильтр комментарий")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Фильтр комментарий прошел успешно"),
    })
    @GetMapping("/filter")
    ResponseEntity<Page<CommentDto>> getComments(@RequestParam(required = false) Long newsId,
                                                 @RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size,
                                                 @RequestParam(defaultValue = "createdAt") String sortBy,
                                                 @RequestParam(defaultValue = "desc") String direction){
        return ResponseEntity.ok(commentService.getComments(newsId,page,size,sortBy,direction));

    }
}
