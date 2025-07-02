package com.example.newsapp.cache;


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
public class UserCacheService {

    private final RedisTemplate<String, UserDto> userDtoRedisTemplate;
    private static final Duration TTL = Duration.ofHours(2);


    @PostConstruct
    public void init() {
        try {
            RedisConnection connection = userDtoRedisTemplate.getConnectionFactory().getConnection();
            log.info("Connected to Redis: {}", connection.ping());
            connection.close();
        } catch (Exception e) {
            log.error("Redis connection error: {}", e.getMessage());
        }
    }

    /** Безопасно берём из Redis, иначе null */
    private UserDto safe(Object o) {
        return o instanceof UserDto dto ? dto : null;
    }
    public void saveUser(UserDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("Пользователь не может быть null");
        }
        boolean saved = false;

        if (userDto.getId() != null) {
            userDtoRedisTemplate.opsForValue()
                    .set(getKey(userDto.getId()), userDto, TTL);
            saved = true;
        }
        if (userDto.getEmail() != null) {
            userDtoRedisTemplate.opsForValue()
                    .set(getEmailKey(userDto.getEmail()), userDto, TTL);
            saved = true;
        }
        if (userDto.getName() != null) {
            userDtoRedisTemplate.opsForValue()
                    .set(getNameKey(userDto.getName()), userDto, TTL);
            saved = true;
        }
        if (!saved) {
            throw new IllegalArgumentException(
                    "Невозможно сохранить пользователя в Redis: не указан id, email или name");
        }
    }


    public UserDto getUser(Long id){
       return safe(userDtoRedisTemplate.opsForValue().get(getKey(id)));
    }

    public UserDto getUserByEmail(String email) {
        return safe(userDtoRedisTemplate.opsForValue().get(getEmailKey(email)));
    }

    public UserDto getUserByName(String name){
        return safe(userDtoRedisTemplate.opsForValue().get(getNameKey(name)));
    }

    public void deleteUser(UserDto userDto){
        if (userDto.getId() != null) {
            userDtoRedisTemplate.delete(getKey(userDto.getId()));
        }
        if (userDto.getEmail() != null) {
            userDtoRedisTemplate.delete(getEmailKey(userDto.getEmail()));
        }
        if (userDto.getName() != null) {
            userDtoRedisTemplate.delete(getNameKey(userDto.getName()));
        }
    }

    private String getKey(Long id){
        return "user:"+id;
    }

    private String getEmailKey(String email) {
        return "user:email:" + email.trim();
    }

    private String getNameKey(String name) {
        return "user:name:" + name;
    }

}

//Redis хранит пользователей по ключу "user:<id>".
//Мы можем:
//сохранить пользователя в Redis на 2 часа,
//получить его,
//удалить его.
//Всё безопасно, типизировано, читаемо, и работает через JSON
//редис хранит объект юзер и значения:
//"id": 42,
//  "name": "Alice",
//  "email": "alice@example.com",
//  "role": "USER",
//  "isBlocked": false