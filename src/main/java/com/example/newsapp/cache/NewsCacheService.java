package com.example.newsapp.cache;

import com.example.newsapp.dto.NewsDto;
import com.example.newsapp.dto.UserDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsCacheService {


    private final RedisTemplate<String, NewsDto> newsDtoRedisTemplate;
    private static final Duration TTL = Duration.ofHours(12);


    @PostConstruct
    public void init() {
        try {
            RedisConnection connection = newsDtoRedisTemplate.getConnectionFactory().getConnection();
            log.info("Connected to Redis: {}", connection.ping());
            connection.close();
        } catch (Exception e) {
            log.error("Redis connection error: {}", e.getMessage());
        }
    }

    private NewsDto safe(Object o){
        return o instanceof NewsDto dto ? dto : null;
    }

    public void saveNews(NewsDto newsDto) {
        if (newsDto == null || newsDto.getId() == null) {
            throw new IllegalArgumentException("newsDto / id не может быть null");
        }
        // 👇 добавили третьим аргументом TTL
        newsDtoRedisTemplate.opsForValue()
                .set(getKey(newsDto.getId()), newsDto, TTL);
    }


    public NewsDto getNews(Long id){
        return safe(newsDtoRedisTemplate.opsForValue().get(getKey(id)));
    }

    public void deleteNews(Long id){
        newsDtoRedisTemplate.delete(getKey(id));
    }


    private String getKey(Long id){
        return "news:" + id;
    }
}
