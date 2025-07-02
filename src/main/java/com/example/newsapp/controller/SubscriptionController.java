package com.example.newsapp.controller;


import com.example.newsapp.dto.ProblemDetails;
import com.example.newsapp.dto.SubscriptionDto;
import com.example.newsapp.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;


    @Operation(summary = "Подписаться") //не полностью доделан
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь подписан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @PostMapping("/{authorId}")
     public ResponseEntity<Void> subscribe(@PathVariable Long authorId, Authentication authentication){
        subscriptionService.subscribe(authorId, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }




    @Operation(summary = "Удаление подписки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",description = "Подписка удалена"),
            @ApiResponse(responseCode = "404", description = "Подписка не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    )),
            @ApiResponse(responseCode = "403", description = "Нет прав для удаления подписки",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @DeleteMapping("/{authorId}")
    public ResponseEntity<Void> unsubscribe(@PathVariable Long authorId, Authentication authentication){
        subscriptionService.unsubscribe(authorId, authentication);
        return ResponseEntity.noContent().build();
    }




    @Operation(summary = "Сортировка подписки по пользователю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Сортировка прошла успешно"),
            @ApiResponse(responseCode = "204", description = "Нет результатов по сортировке",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<SubscriptionDto>> getAllSubscriptionUser(@PathVariable Long userId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "10") int size,
                                                                        @RequestParam(required = false) String sortBy,
                                                                        @RequestParam(required = false) String direction){
        return ResponseEntity.ok(subscriptionService.getAllSubscriptionUser
                (userId, page, size, sortBy, direction));
    }



    @Operation(summary = "Сортировка подписки по автору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Сортировка прошла успешно"),
            @ApiResponse(responseCode = "204", description = "Нет результатов по сортировке",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<SubscriptionDto>> getAllSubscribersOfAuthor(@PathVariable Long authorId,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size,
                                                                           @RequestParam(required = false) String sortBy,
                                                                           @RequestParam(required = false) String direction){
        return ResponseEntity.ok(subscriptionService.getAllSubscribersOfAuthor
                (authorId, page, size, sortBy, direction));
    }



    @Operation(summary = "Проверка подписки")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Подписка на автора успешна"),
            @ApiResponse(responseCode = "204", description = "Нет результатов по подписке",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @GetMapping("/{authorId}")
    public ResponseEntity<Boolean> isSubscribed(@PathVariable Long authorId, Authentication authentication){
        return ResponseEntity.ok(subscriptionService.isSubscribed(authorId,authentication));
    }

}
