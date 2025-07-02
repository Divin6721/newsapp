package com.example.newsapp.specification;

import com.example.newsapp.model.News;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class NewsSpecification {

    public static Specification<News> hasCategory(String category) {
        return (root, query, cb)
                -> category == null ? null : cb.equal(root.get("category"), category);
    }

    public static Specification<News> hasAuthor(String authorName) {
        return (root, query, cb)
                -> authorName == null ? null : cb.equal(root.get("author").get("name"), authorName);
    }

    public static Specification<News> publishedAfter(LocalDate date) {
        return (root, query, cb)
                -> date == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), date);
    }

    public static Specification<News> minViews(Long views) {
        return (root, query, cb)
                -> views == null ? null : cb.greaterThanOrEqualTo(root.get("views"), views);
    }
}
