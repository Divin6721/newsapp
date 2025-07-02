package com.example.newsapp.controller;

import com.example.newsapp.dto.ComplaintAdminResponseRequest;
import com.example.newsapp.dto.ComplaintUserRequest;
import com.example.newsapp.dto.ComplaintDto;
import com.example.newsapp.dto.ProblemDetails;
import com.example.newsapp.service.ComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/complaint")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class ComplaintController {

    private final ComplaintService complaintService;


    @Operation(summary = "Фильтр жалобы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Фильтр жалоб прошла успешно")
            })
    @GetMapping("/filter")
    ResponseEntity<Page<ComplaintDto>> filterComplaints(@RequestParam(required = false) String newsTitle,
                                                        @RequestParam(required = false) String fromUserEmail,
                                                        @RequestParam(required = false) String status,
                                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
                                                        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(required = false) String sortBy,
                                                        @RequestParam(required = false) String direction){

        return ResponseEntity.ok(complaintService.filterComplaints(newsTitle,fromUserEmail,
                status,fromDate,toDate,page,size,sortBy,direction));

    }

    @Operation(summary = "Создать жалобу") //не полностью доделан
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь оставил жалобу"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                    content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProblemDetails.class)
            ))
    })
    @PostMapping("/create")
    ResponseEntity<ComplaintDto> createComplaint(@Valid @RequestBody ComplaintUserRequest complaintDto,
                                                                     Authentication authentication){
        ComplaintDto createdComplain = complaintService.createComplaint(complaintDto, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComplain);

    }

    @Operation(summary = "Удалить жалобу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",description = "Жалоба удалена"),
            @ApiResponse(responseCode = "404", description = "Жалоба не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    )),
            @ApiResponse(responseCode = "403", description = "Нет прав для удаления жалобы",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @DeleteMapping("/{complaintId}")
    ResponseEntity<Void> deleteComplaint(@PathVariable Long complaintId,
                                                             Authentication authentication){

        complaintService.deleteComplaint(complaintId,authentication);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Ответ на жалобу") //не полностью доделан
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь предоставил ответ"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @PostMapping("/respond/{complaintId}")
    ResponseEntity<ComplaintDto> respondToComplaint(@Valid @RequestBody ComplaintAdminResponseRequest dto,
                                                    Authentication authentication){
        ComplaintDto updatedComplaint = complaintService.respondToComplaint(dto,authentication);
        return ResponseEntity.ok(updatedComplaint);

    }
}
