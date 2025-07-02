package com.example.newsapp.service.impl;

import com.example.newsapp.dto.CommentCreateRequest;
import com.example.newsapp.dto.CommentDto;
import com.example.newsapp.mapper.CommentMapper;
import com.example.newsapp.model.Comment;
import com.example.newsapp.model.News;
import com.example.newsapp.model.User;
import com.example.newsapp.repository.CommentRepository;
import com.example.newsapp.security.CustomUserDetails;
import com.example.newsapp.service.CommentService;
import com.example.newsapp.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PermissionService permissionService;




    @Override
    @PreAuthorize("hasAnyRole('USER')")
    public CommentDto createComment(CommentCreateRequest dto, Long newsId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        News news = permissionService.checkNewsExists(newsId);
        User user = new User();
        user.setId(userId);
        user.setName(userDetails.getName());
        Comment comment = CommentMapper.toEntity(dto, user, news);
        Comment saved = commentRepository.save(comment);
        return CommentMapper.toDto(saved);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public void deleteComment(Long commentId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();
        User user = new User();
        user.setId(userId);
        user.setRole(userDetails.getRole());
        Comment comment = permissionService.checkCommentExists(commentId);
        permissionService.commentAccess(user,comment);
        commentRepository.delete(comment);

    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Page<CommentDto> getComments(Long newsId, int page, int size, String sortBy, String direction) {

        Sort.Direction sortDirection = direction != null && direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<Comment> commentPage;

        if (newsId != null) {
            commentPage = commentRepository.findByNewsId(newsId, pageable);
        } else {
            commentPage = commentRepository.findAllComment(pageable);
        }
        return commentPage.map(CommentMapper::toDto);
    }



}
