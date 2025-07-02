package com.example.newsapp.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Set;

public class PaginationUtils {

    private static final int MAX_PAGE_SIZE = 100;

    // Разрешённые поля для сортировки
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("createdAt", "views", "title");

    public static Pageable createPageable(int page, int size, String sortBy, String direction) {
        // Ограничиваем размер страницы максимумом
        int safeSize = Math.min(size, MAX_PAGE_SIZE);

        // Проверяем, разрешено ли поле для сортировки
        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";

        // Устанавливаем направление сортировки (asc по умолчанию)
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

        // Создаём объект Pageable с сортировкой
        return PageRequest.of(page, safeSize, Sort.by(sortDirection, safeSortBy));
    }
}
