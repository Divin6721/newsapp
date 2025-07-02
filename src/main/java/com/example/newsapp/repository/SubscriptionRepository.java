package com.example.newsapp.repository;

import com.example.newsapp.model.Subscription;
import com.example.newsapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>,
        JpaSpecificationExecutor<Subscription> {
    Optional<Subscription> findBySubscriberAndAuthor(User subscriber, User author);
    boolean existsBySubscriberIdAndAuthorId(Long subscriberId, Long authorId);

}
