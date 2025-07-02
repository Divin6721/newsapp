package com.example.newsapp.specification;

import com.example.newsapp.model.User;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;


public class UserSpecification {

    public static Specification<User> orderBySubscribersCount(String direction) {
        return (root, query, cb) -> {
            root.join("subscribers", JoinType.LEFT);
            query.groupBy(root.get("id"));

            if ("desc".equalsIgnoreCase(direction)) {
                query.orderBy(cb.desc(cb.count(root.get("subscribers"))));
            } else {
                query.orderBy(cb.asc(cb.count(root.get("subscribers"))));
            }

            return cb.conjunction();
        };
    }
}
