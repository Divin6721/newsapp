package com.example.newsapp.controller;

import com.example.newsapp.dto.*;
import com.example.newsapp.mapper.RegisterRequestMapper;
import com.example.newsapp.mapper.UserMapper;
import com.example.newsapp.model.User;
import com.example.newsapp.repository.UserRepository;
import com.example.newsapp.security.JwtUtil;
import com.example.newsapp.service.PermissionService;
import com.example.newsapp.service.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;//Основной механизм Spring Security для проверки логина и пароля
    private final JwtUtil jwtUtil;//Класс, который создаёт и проверяет JWT-токены
    private final UserDetailsService userDetailsService;//	Интерфейс Spring — загружает пользователя по email (username)
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionService permissionService;
    private final RefreshTokenService refreshTokenService;


    @Operation(summary = "Вход в систему (логин)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная авторизация"),
            @ApiResponse(responseCode = "401",
                    description = "Неверный логин или пароль",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            User user = permissionService.getUserForAuthentication(loginRequest.getEmail());
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            log.info("✅ Пользователь {} вошёл в систему", loginRequest.getEmail());

            return ResponseEntity.ok(new JwtResponse(accessToken, refreshToken));
        } catch (Exception e) {
            log.error("❌ Ошибка при логине пользователя {}: {}", loginRequest.getEmail(), e.getMessage());

            ProblemDetails error = ProblemDetails.builder()
                    .status(401)
                    .title("Ошибка авторизации")
                    .detail(e.getMessage())
                    .instance("/api/login")
                    .type("auth_error")
                    .build();

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @Operation(summary = "Регистрация нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации или дубликат",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetails.class)
                    ))
})
    @PostMapping("/register")
    @Transactional
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest request) {
        permissionService.checkUniqueUserEmail(request.getEmail());
        permissionService.checkUniqueUserName(request.getName());

        User user = RegisterRequestMapper.fromRegisterRequest(request, passwordEncoder);
        user = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserMapper.toUserDto(user));
    }




    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestBody TokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (refreshTokenService.isRevoked(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        try {
            String email = jwtUtil.extractUsername(refreshToken);
            refreshTokenService.revokeToken(refreshToken); // ✅ ротация

            User user = permissionService.getUserForAuthentication(email); // 💡 получаем пользователя

            String newAccessToken = jwtUtil.generateAccessToken(user);
            String newRefreshToken = jwtUtil.generateRefreshToken(user);

            return ResponseEntity.ok(new JwtResponse(newAccessToken, newRefreshToken));
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

}


