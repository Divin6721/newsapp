package com.example.newsapp.service.impl;

import com.example.newsapp.dto.SubscriptionDto;
import com.example.newsapp.mapper.SubscriptionMapper;
import com.example.newsapp.security.CustomUserDetails;

import com.example.newsapp.model.Subscription;
import com.example.newsapp.model.User;
import com.example.newsapp.repository.SubscriptionRepository;
import com.example.newsapp.repository.UserRepository;
import com.example.newsapp.service.PermissionService;
import com.example.newsapp.service.SubscriptionService;
import com.example.newsapp.specification.SubscriptionSpecification;
import com.example.newsapp.util.PaginationUtils;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final PermissionService permissionService;

    @Override //проверить этот метод
    @PreAuthorize("hasAnyRole('USER')")
    public void subscribe(Long authorId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long subscriberId = userDetails.getId();

        User subscriber = new User();
        subscriber.setId(subscriberId);

        User author = permissionService.checkUserExists(authorId);

        permissionService.checkValidSubscriptionTarget(subscriber, author);
        permissionService.checkSubscriberAuthorDuplicate(subscriber, author);

        Subscription subscription = SubscriptionMapper.toEntity(subscriber, author);
        subscription.setCreatedAt(LocalDateTime.now());

        subscriptionRepository.save(subscription);
    }

    @Override
    @PreAuthorize("hasAnyRole('USER')")
    public void unsubscribe(Long authorId, Authentication authentication) {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      Long subscriberId = userDetails.getId();

      User subscriber = new User();
      subscriber.setId(subscriberId);

      User author = permissionService.checkUserExists(authorId);

      Subscription subscription = permissionService.getSubscriptionOrThrow(subscriber, author);
      subscriptionRepository.delete(subscription);

    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Page<SubscriptionDto> getAllSubscriptionUser(Long userId, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction != null && direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Specification<Subscription> spec = SubscriptionSpecification.bySubscriberId(userId);

        Page<Subscription> subscriptions = subscriptionRepository.findAll(spec, pageable);

        return subscriptions.map(SubscriptionMapper::toDto);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Page<SubscriptionDto> getAllSubscribersOfAuthor(Long authorId, int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction != null && direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Specification<Subscription> spec = SubscriptionSpecification.byAuthorId(authorId);

        Page<Subscription> subscriptions = subscriptionRepository.findAll(spec, pageable);

        return subscriptions.map(SubscriptionMapper::toDto);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public boolean isSubscribed(Long authorId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long subscriberId = userDetails.getId();

        return subscriptionRepository.existsBySubscriberIdAndAuthorId(subscriberId, authorId);
    }
}
