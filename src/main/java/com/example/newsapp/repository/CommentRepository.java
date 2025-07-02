package com.example.newsapp.repository;

import com.example.newsapp.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByNewsId(Long newsId, Pageable pageable);

    @Query("SELECT c FROM Comment c")
    Page<Comment> findAllComment(Pageable pageable);
}
