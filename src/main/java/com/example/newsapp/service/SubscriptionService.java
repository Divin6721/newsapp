package com.example.newsapp.service;

import com.example.newsapp.dto.SubscriptionDto;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;

public interface SubscriptionService {
    void subscribe(Long authorId, Authentication authentication);
    void unsubscribe(Long authorId, Authentication authentication);
    Page<SubscriptionDto> getAllSubscriptionUser(Long userId,int page, int size, String sortBy, String direction);
    Page<SubscriptionDto> getAllSubscribersOfAuthor(Long authorId, int page, int size, String sortBy, String direction);
    boolean isSubscribed(Long authorId, Authentication authentication);

}
