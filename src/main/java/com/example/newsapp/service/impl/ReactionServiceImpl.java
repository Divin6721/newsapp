package com.example.newsapp.service.impl;

import com.example.newsapp.cache.ReactionCacheService;
import com.example.newsapp.dto.ReactionDto;
import com.example.newsapp.mapper.ReactionMapper;
import com.example.newsapp.model.News;
import com.example.newsapp.model.Reaction;
import com.example.newsapp.model.ReactionType;
import com.example.newsapp.model.User;
import com.example.newsapp.repository.ReactionRepository;
import com.example.newsapp.security.CustomUserDetails;
import com.example.newsapp.service.PermissionService;
import com.example.newsapp.service.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {

   private final ReactionRepository reactionRepository;
   private final ReactionCacheService reactionCacheService;
   private final PermissionService permissionService;

    @Override
    @PreAuthorize("hasAnyRole('USER')")
    public void setReaction(Long newsId, String reactionTypeStr, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        News news = permissionService.checkNewsExists(newsId);
        User user = new User();
        user.setId(userId);

        ReactionType reactionType = permissionService.checkValidReactionType(reactionTypeStr);

        Optional<Reaction> existingReaction = reactionRepository.findByUserIdAndNewsId(userId, newsId);

        if (existingReaction.isPresent()) {
            Reaction existing = existingReaction.get();
            existing.setReactionType(reactionType);
            reactionRepository.save(existing);
        } else {
            ReactionDto dto = ReactionDto.builder().reactionType(reactionTypeStr).build();
            Reaction reaction = ReactionMapper.toEntity(dto, user, news);
            reactionRepository.save(reaction);
        }

        // ✅ Пересчёт и обновление кэша
        long count = reactionRepository.countByNewsIdAndReactionType(newsId, reactionType);
        reactionCacheService.saveReactionCount(newsId, reactionType, count);
    }





    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('USER')")//вопрос чтобы юзер свою реакцию удалял
    public void removeReaction(Long newsId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getId();

        permissionService.checkNewsExists(newsId);

        // Получаем саму реакцию (нам нужен её тип — LIKE или DISLIKE)
        Reaction reaction = permissionService.checkReactionExists(userId, newsId);
        ReactionType type = reaction.getReactionType();

        //  Удаляем
        reactionRepository.deleteByUserIdAndNewsId(userId, newsId);

        // Пересчитываем и обновляем Redis
        long count = reactionRepository.countByNewsIdAndReactionType(newsId, type);
        reactionCacheService.saveReactionCount(newsId, type, count);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public Long getReactionCountForNews(Long newsId, ReactionType reactionType) {
       Long cachedCount = reactionRepository.countByNewsIdAndReactionType(newsId, reactionType);
       if(cachedCount != null){
           return cachedCount;
       }

       Long count = reactionRepository.countByNewsIdAndReactionType(newsId, reactionType);

       reactionCacheService.saveReactionCount(newsId, reactionType, count);
       return count;
    }
}
