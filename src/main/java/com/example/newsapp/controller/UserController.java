package com.example.newsapp.controller;


import com.example.newsapp.dto.ProblemDetails;
import com.example.newsapp.dto.UserDto;
import com.example.newsapp.mapper.UserMapper;
import com.example.newsapp.model.User;
import com.example.newsapp.service.PermissionService;
import com.example.newsapp.service.UserService;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PermissionService permissionService;


    @Operation(summary = "Авторы сортировка", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Сортировка прошла успешно"),
            @ApiResponse(responseCode = "204", description = "Нет результатов по сортировке",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @GetMapping
    public ResponseEntity<Page<UserDto>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size,
                                                     @RequestParam(defaultValue = "name") String sortBy,
                                                     @RequestParam(defaultValue = "asc") String direction){
           return ResponseEntity.ok(userService.getAllUsers
                   (page, size, sortBy, direction));
    }

    @Operation(summary = "Создать нового пользователя") //не полностью доделан
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь создан"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации данных",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @PostMapping
    @Transactional
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto dto){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registerUser(dto));
    }

    @Operation(summary = "Получить пользователя по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь с таким ID не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Удаление пользователя",security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",description = "Пользователь удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    )),
            @ApiResponse(responseCode = "403", description = "Нет прав для удаления пользователя",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication authentication){
        userService.deleteUser(id, authentication);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Блокировка пользователя",security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Пользователь заблокирован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    )),
            @ApiResponse(responseCode = "403", description = "Нет прав для изменения пользователя",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @PutMapping("/block/{id}")
    public ResponseEntity<UserDto> blockUser(@PathVariable Long id){
        return ResponseEntity.ok(userService.blockUser(id));

    }
    @Operation(summary = "Разблокировка пользователя",security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "Пользователь разблокирован"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    )),
            @ApiResponse(responseCode = "403", description = "Нет прав для изменения пользователя",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @PutMapping("/unblock/{id}")
    public ResponseEntity<UserDto> unblockUser(@PathVariable Long id){
        return ResponseEntity.ok(userService.unblockUser(id));

    }


    @Operation(summary = "Получить текущего авторизованного пользователя", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        String email = authentication.getName(); // email из JWT
        User user = permissionService.checkUserExistsByEmail(email);
        return ResponseEntity.ok(UserMapper.toUserDto(user));
    }

}
