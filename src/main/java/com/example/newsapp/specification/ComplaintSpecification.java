package com.example.newsapp.specification;

import com.example.newsapp.model.Complaint;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ComplaintSpecification {

    public static Specification<Complaint> hasUserId(Long userId) {
        return (root, query, cb) -> userId == null ? null :
                cb.equal(root.get("fromUser").get("id"), userId);
    }

    public static Specification<Complaint> hasNewsId(Long newsId) {
        return (root, query, cb) -> newsId == null ? null :
                cb.equal(root.get("news").get("id"), newsId);
    }

    public static Specification<Complaint> isResponded(Boolean responded) {
        return (root, query, cb) -> {
            if (responded == null) return null;
            return responded
                    ? cb.isNotNull(root.get("response"))
                    : cb.isNull(root.get("response"));
        };
    }
    public static Specification<Complaint> hasNewsTitle(String newsTitle) {
        return (root, query, cb) -> {
            if (newsTitle == null || newsTitle.isBlank()) return null;
            return cb.like(cb.lower(root.get("news").get("title")), "%" + newsTitle.toLowerCase() + "%");
        };
    }

    public static Specification<Complaint> hasUserEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.isBlank()) return null;
            return cb.like(cb.lower(root.get("fromUser").get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    public static Specification<Complaint> createdAfter(LocalDate fromDate) {
        return (root, query, cb) -> {
            if (fromDate == null) return null;
            return cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate.atStartOfDay());
        };
    }

    public static Specification<Complaint> createdBefore(LocalDate toDate) {
        return (root, query, cb) -> {
            if (toDate == null) return null;
            return cb.lessThanOrEqualTo(root.get("createdAt"), toDate.atTime(23, 59, 59));
        };
    }

}

