package com.example.newsapp.service;

import com.example.newsapp.dto.NewsCreateRequest;
import com.example.newsapp.dto.NewsDto;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;


import java.time.LocalDate;

public interface NewsService {
    NewsDto getNewsById(Long id);
    NewsDto createNews(NewsCreateRequest dto , Authentication authentication);
    void deleteNews(Long id, Authentication authentication);
    NewsDto updateNews(Long id, NewsCreateRequest dto ,Authentication authentication);
    Page<NewsDto> filterPageNews(String category, String author, LocalDate dateFrom, Long minViews,
                                 int page, int size, String sortBy, String direction);




}

