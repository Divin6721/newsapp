package com.example.newsapp.cache;


import com.example.newsapp.model.ReactionType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactionCacheService {

    private final RedisTemplate<String, Long> reactionCountRedisTemplate;
    private static final Duration TTL = Duration.ofHours(1);


    @PostConstruct
    public void init() {
        try {
            RedisConnection connection = reactionCountRedisTemplate.getConnectionFactory().getConnection();
            log.info("Connected to Redis: {}", connection.ping());
            connection.close();
        } catch (Exception e) {
            log.error("Redis connection error: {}", e.getMessage());
        }
    }

    private String getKey(Long newsId, ReactionType type) {
        return "reaction:" + type.name().toLowerCase() + ":" + newsId;
    }

    public void saveReactionCount(Long newsId, ReactionType type, Long count) {
        if (newsId == null || type == null) {
            throw new IllegalArgumentException("newsId / type не может быть null");
        }
        reactionCountRedisTemplate.opsForValue()
                .set(getKey(newsId, type), count, TTL);
    }


    public Long getReactionCount(Long newsId, ReactionType type) {
        return Optional.ofNullable(
                reactionCountRedisTemplate.opsForValue().get(getKey(newsId, type))
        ).orElse(0L);
    }

    public void deleteReactionCount(Long newsId, ReactionType type) {
         reactionCountRedisTemplate.delete(getKey(newsId, type));
    }
}

