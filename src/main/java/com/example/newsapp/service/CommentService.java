package com.example.newsapp.service;

import com.example.newsapp.dto.CommentCreateRequest;
import com.example.newsapp.dto.CommentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

public interface CommentService {
    CommentDto createComment(CommentCreateRequest dto, Long newsId, Authentication authentication);
    void deleteComment(Long commentId, Authentication authentication);
    Page<CommentDto> getComments(Long newsId, int page, int size, String sortBy, String direction);
}
