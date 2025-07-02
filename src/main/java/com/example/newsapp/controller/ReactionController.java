package com.example.newsapp.controller;

import com.example.newsapp.dto.ProblemDetails;
import com.example.newsapp.model.ReactionType;
import com.example.newsapp.service.ReactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reaction")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ReactionController {

    private final ReactionService reactionService;


    @Operation(summary = "Поставить реакцию на новость") //не полностью доделан
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь поставил реакцию"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @PostMapping("/{newsId}")
    ResponseEntity<Void> setReaction(@PathVariable Long newsId,
                                                         @RequestParam String reactionType,
                                                         Authentication authentication){
        reactionService.setReaction(newsId, reactionType,authentication);
       return   ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "Удаление реакции")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Реакция удалена"),
            @ApiResponse(responseCode = "404", description = "Реакция не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    )),
            @ApiResponse(responseCode = "403", description = "Нет прав для удаления реакции",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @DeleteMapping("/{newsId}")
    ResponseEntity<Void> removeReaction(@PathVariable Long newsId, Authentication authentication){
        reactionService.removeReaction(newsId, authentication);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Получить подсчет лайков/дизлайков")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Подсчет прошел успешно"),
            @ApiResponse(responseCode = "204", description = "Нет результатов по подсчету",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @GetMapping("/count/{newsId}")
    ResponseEntity<Long> getReactionCountForNews(@PathVariable Long newsId,
                                                                     @RequestParam(required = false) ReactionType reactionType){
        return ResponseEntity.ok(reactionService.getReactionCountForNews(newsId,reactionType));

    }
}
