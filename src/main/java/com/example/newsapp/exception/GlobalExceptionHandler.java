package com.example.newsapp.exception;

import com.example.newsapp.dto.ProblemDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /* ------------------------------------------------------------------
       400 – Ошибки валидации аргументов (@Valid в контроллерах)
     ------------------------------------------------------------------ */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetails> handleValidation(MethodArgumentNotValidException ex,
                                                           HttpServletRequest req) {

        // собираем ВСЕ сообщения сразу, а не только первое
        String detail = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> "поле «%s» %s".formatted(e.getField(), e.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        return badRequest(detail, req);
    }

    /* ------------------------------------------------------------------
       400 – Нарушение валидации на уровне сервисов / DTO
     ------------------------------------------------------------------ */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetails> handleIllegalArgument(IllegalArgumentException ex,
                                                                HttpServletRequest req) {
        return badRequest(ex.getMessage(), req);
    }

    /**
     * Ответ 400 с единым форматом
     */
    private ResponseEntity<ProblemDetails> badRequest(String detail,
                                                      HttpServletRequest req) {
        return build(400, "Ошибка валидации", detail, req);
    }

    /**
     * Конструктор ProblemDetails + ResponseEntity
     */
    private ResponseEntity<ProblemDetails> build(int status,
                                                 String title,
                                                 String detail,
                                                 HttpServletRequest req) {
        return ResponseEntity.status(status)
                .body(new ProblemDetails(status, title, detail, req.getRequestURI()));
    }

    /* ------------------------------------------------------------------
       400 – Нарушение ограничений JPA / Bean-Validation из глубины
     ------------------------------------------------------------------ */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetails> handleConstraint(ConstraintViolationException ex,
                                                           HttpServletRequest req) {

        String detail = ex.getConstraintViolations().stream()
                .map(v -> "поле «%s» %s".formatted(v.getPropertyPath(), v.getMessage()))
                .collect(Collectors.joining("; "));

        return badRequest(detail, req);
    }


     /* ------------------------------------------------------------------
       403 – Доступ запрещён
     ------------------------------------------------------------------ */

    @ExceptionHandler({
            AccessDeniedException.class,      // включает AuthorizationDeniedException
            InvalidAccessException.class      // ваше пользовательское
    })
    public ResponseEntity<ProblemDetails> handleAccessDenied(RuntimeException ex,
                                                             HttpServletRequest req) {
        log.info("Access denied: {}", ex.getMessage());
        return build(403, "Доступ запрещён",
                ex.getMessage() != null ? ex.getMessage()
                        : "У вас нет прав для этого действия",
                req);
    }


    /* ------------------------------------------------------------------
       404 – Сущность не найдена
     ------------------------------------------------------------------ */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetails> handleNotFound(NotFoundException ex,
                                                         HttpServletRequest req) {
        log.info("Not found: {}", ex.getMessage());
        return build(404, "Не найдено", ex.getMessage(), req);
    }

    /* ------------------------------------------------------------------
       409 – Конфликт данных  (например нарушение UNIQUE в БД)
     ------------------------------------------------------------------ */
    @ExceptionHandler({
            AlreadyExistsException.class,          // своя бизнес-ошибка
            DataIntegrityViolationException.class  // от Spring DAO
    })
    public ResponseEntity<ProblemDetails> handleConflict(Exception ex,
                                                         HttpServletRequest req) {

        String msg = (ex instanceof AlreadyExistsException)
                ? ex.getMessage()                               // своё сообщение
                : "Конфликт данных в базе";                    // общее сообщение

        log.info("Conflict: {}", msg);
        return build(409, "Конфликт", msg, req);
    }

    /* ------------------------------------------------------------------
       500 – Любая неперехваченная ошибка
     ------------------------------------------------------------------ */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetails> handleAny(Exception ex,
                                                    HttpServletRequest req) {

        // ошибки, пришедшие от генерации Swagger-доков, пропускаем
        if (req.getRequestURI().startsWith("/v3/api-docs")) {
            log.debug("Skip swagger error: {}", ex.getMessage());
            return null;               // отдать в обработку дальше по цепочке
        }

        log.error("Unexpected error", ex);
        return build(500, "Внутренняя ошибка",
                "Попробуйте позже", req);
    }



}
