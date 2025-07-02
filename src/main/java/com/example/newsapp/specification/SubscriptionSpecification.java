package com.example.newsapp.specification;


import com.example.newsapp.model.Subscription;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class SubscriptionSpecification {

    public static Specification<Subscription> bySubscriberId(Long subscriberId) {
        return (root, query, cb) ->
                cb.equal(root.get("subscriber").get("id"), subscriberId);
    }

    public static Specification<Subscription> byAuthorId(Long authorId) {
        return (root, query, cb) ->
                cb.equal(root.get("author").get("id"), authorId);
    }

}