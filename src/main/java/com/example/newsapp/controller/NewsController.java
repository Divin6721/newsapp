package com.example.newsapp.controller;

import com.example.newsapp.dto.NewsCreateRequest;
import com.example.newsapp.dto.NewsDto;
import com.example.newsapp.dto.ProblemDetails;
import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import com.example.newsapp.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;



@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class NewsController {

    private final NewsService newsService;

    @Operation(summary = "Получить новость по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Новость успешно найдена"),
            @ApiResponse(responseCode = "404", description = "Новость с таким ID не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @GetMapping("/{id}")
    public ResponseEntity<NewsDto>getNewsById(@PathVariable Long id){
        NewsDto dto = newsService.getNewsById(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Создать новость")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",description = "Новость создана"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    )),
            @ApiResponse(responseCode = "403", description = "Нет прав для создания новости",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @PostMapping
    @Transactional
    public ResponseEntity<NewsDto> createNews(@Valid @RequestBody NewsCreateRequest newsDto,
                              Authentication authentication){
        NewsDto createdNews = newsService.createNews(newsDto, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNews);
    }

    @Operation(summary = "Фильтр новостей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Фильтр новостей прошел успешно")
    })
    @GetMapping("/filter")
    public ResponseEntity<Page<NewsDto>> filterNews(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) Long minViews,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction
    ) {
        return ResponseEntity.ok(newsService.filterPageNews
                (category, author, dateFrom, minViews, page, size, sortBy, direction));
    }

    @Operation(summary = "Удаление новости")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",description = "Новость удалена"),
            @ApiResponse(responseCode = "404", description = "Новость не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    )),
            @ApiResponse(responseCode = "403", description = "Нет прав для удаления новости",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteNews(@PathVariable Long id, Authentication authentication){
        newsService.deleteNews(id,authentication);
         return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Изменение новости")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Новость изменена"),
            @ApiResponse(responseCode = "404", description = "Новость не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    )),
            @ApiResponse(responseCode = "403", description = "Нет прав для изменения новости",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<NewsDto> updateNews(@PathVariable Long id, @Valid @RequestBody NewsCreateRequest newsDto, Authentication authentication){
        return ResponseEntity.ok(newsService.updateNews(id, newsDto, authentication));
    }


}
