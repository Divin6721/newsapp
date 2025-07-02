package com.example.newsapp.mapper;


import com.example.newsapp.dto.NewsCreateRequest;
import com.example.newsapp.dto.NewsDto;
import com.example.newsapp.model.Category;
import com.example.newsapp.model.News;

public class NewsMapper {

    public static NewsDto toDto(News news){
        return NewsDto.builder()
                .id(news.getId())
                .title(news.getTitle())
                .content(news.getContent())
                .authorName(news.getAuthor().getName())
                .category(news.getCategory().name())
                .views(news.getViews())
                .createdAt(news.getCreatedAt())
                .build();
    }
    public static News toEntity(NewsCreateRequest dto) {
        return News.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .category(Category.valueOf(dto.getCategory().toUpperCase()))
                .build();
    }
}
