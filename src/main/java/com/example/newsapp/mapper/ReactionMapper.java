package com.example.newsapp.mapper;

import com.example.newsapp.dto.ReactionDto;
import com.example.newsapp.model.News;
import com.example.newsapp.model.Reaction;
import com.example.newsapp.model.ReactionType;
import com.example.newsapp.model.User;

    public class ReactionMapper {

        public static ReactionDto toDto(Reaction reaction) {
            return ReactionDto.builder()
                    .id(reaction.getId())
                    .reactionType(reaction.getReactionType().name())
                    .userName(reaction.getUser().getName())
                    .newsId(reaction.getNews().getId())
                    .build();
        }

        public static Reaction toEntity(ReactionDto dto, User user, News news) {
            ReactionType reactionType;

            try {
                reactionType = ReactionType.valueOf(dto.getReactionType().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Недопустимый тип реакции: " + dto.getReactionType());
            }

            return Reaction.builder()
                    .reactionType(reactionType)
                    .user(user)
                    .news(news)
                    .build();
        }
    }


