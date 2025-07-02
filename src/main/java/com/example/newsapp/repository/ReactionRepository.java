package com.example.newsapp.repository;

import com.example.newsapp.model.Reaction;
import com.example.newsapp.model.ReactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface ReactionRepository extends JpaRepository<Reaction,Long> {
    Optional<Reaction> findByUserIdAndNewsId(Long userId, Long newsId);//ставил ли пользователь реакцию на новость.
    Long countByNewsIdAndReactionType(Long newsId, ReactionType reactionType);//сколько лайков/дизлайков у новости.
    @Modifying
    void deleteByUserIdAndNewsId(Long userId, Long newsId);//удалить реакцию пользователя с новости.
}
