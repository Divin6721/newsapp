package com.example.newsapp.config;


import com.example.newsapp.dto.NewsDto;
import com.example.newsapp.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, UserDto> userDtoRedisTemplate(
            RedisConnectionFactory cf,
            @Qualifier("redisMapper") ObjectMapper mapper) {

        var tpl = new RedisTemplate<String, UserDto>();
        tpl.setConnectionFactory(cf);
        tpl.setKeySerializer(new StringRedisSerializer());
        tpl.setValueSerializer(new GenericJackson2JsonRedisSerializer(mapper));
        tpl.afterPropertiesSet();              // ⚙️ «подготовить» template
        return tpl;
    }

    @Bean
    public RedisTemplate<String, NewsDto> newsDtoRedisTemplate(
            RedisConnectionFactory cf,
            @Qualifier("redisMapper") ObjectMapper mapper) {

        var tpl = new RedisTemplate<String, NewsDto>();
        tpl.setConnectionFactory(cf);
        tpl.setKeySerializer(new StringRedisSerializer());
        tpl.setValueSerializer(new GenericJackson2JsonRedisSerializer(mapper));
        tpl.afterPropertiesSet();
        return tpl;


    }


    @Bean
    public RedisTemplate<String, Long> reactionCountRedisTemplate(
            RedisConnectionFactory cf,
            @Qualifier("redisMapper") ObjectMapper mapper) {
        var tpl = new RedisTemplate<String, Long>();
        tpl.setConnectionFactory(cf);
        tpl.setKeySerializer(new StringRedisSerializer());
        tpl.setValueSerializer(new GenericToStringSerializer<>(Long.class));
        tpl.afterPropertiesSet();
        return tpl;
    }

}
//Создаёт и настраивает RedisTemplate<String, UserDto> — шаблон для хранения объектов UserDto в Redis.
//Ключи будут строками (String).
//Значения будут сериализоваться/десериализоваться как JSON с помощью Jackson.
