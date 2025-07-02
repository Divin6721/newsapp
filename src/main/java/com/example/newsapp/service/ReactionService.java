package com.example.newsapp.service;


import com.example.newsapp.model.ReactionType;
import org.springframework.security.core.Authentication;

public interface ReactionService {

    void setReaction(Long newsId, String reactionType, Authentication authentication);
    void removeReaction(Long newsId, Authentication authentication);
    Long getReactionCountForNews(Long newsId, ReactionType reactionType);

}
