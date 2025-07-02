package com.example.newsapp.mapper;

import com.example.newsapp.dto.CommentCreateRequest;
import com.example.newsapp.dto.CommentDto;
import com.example.newsapp.model.Comment;
import com.example.newsapp.model.News;
import com.example.newsapp.model.User;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .userName(comment.getUser().getName())
                .newsId(comment.getNews().getId())
                .build();
    }
    public static Comment toEntity(CommentCreateRequest dto, User user, News news) {
        return Comment.builder()
                .content(dto.getContent())
                .user(user)
                .news(news)
                .build();
    }
}
