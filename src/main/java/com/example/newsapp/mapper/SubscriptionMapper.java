package com.example.newsapp.mapper;

import com.example.newsapp.dto.SubscriptionDto;
import com.example.newsapp.model.Subscription;
import com.example.newsapp.model.User;

public class SubscriptionMapper {

    public static SubscriptionDto toDto(Subscription subscription) {
        return SubscriptionDto.builder()
                .id(subscription.getId())
                .subscriberId(subscription.getSubscriber().getId())
                .authorId(subscription.getAuthor().getId())
                .createdAt(subscription.getCreatedAt())
                .build();

    }
    public static Subscription toEntity(User subscriber, User author) {
        return Subscription.builder()
                .subscriber(subscriber)
                .author(author)
                .build();
    }
}
