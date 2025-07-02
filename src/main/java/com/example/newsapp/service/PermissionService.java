package com.example.newsapp.service;

import com.example.newsapp.cache.NewsCacheService;
import com.example.newsapp.cache.UserCacheService;
import com.example.newsapp.dto.NewsDto;
import com.example.newsapp.dto.UserDto;
import com.example.newsapp.exception.AlreadyExistsException;
import com.example.newsapp.exception.InvalidAccessException;
import com.example.newsapp.exception.NotFoundException;
import com.example.newsapp.mapper.NewsMapper;
import com.example.newsapp.mapper.UserMapper;
import com.example.newsapp.model.*;
import com.example.newsapp.repository.*;
import com.example.newsapp.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.Function;


@Service
@RequiredArgsConstructor
public class PermissionService {

    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final NewsRepository newsRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final ReactionRepository reactionRepository;
    private final UserCacheService userCacheService;
    private final NewsCacheService newsCacheService;
    private final ComplaintRepository complaintRepository;

    public void checkUserBlockStatus(User user, boolean shouldBeBlocked) {
        boolean isBlocked = Boolean.TRUE.equals(user.getIsBlocked());
        if (shouldBeBlocked && !isBlocked) {
            throw new IllegalArgumentException("Пользователь с ID " + user.getId() + " уже разблокирован");
        }

        if (!shouldBeBlocked && isBlocked) {
            throw new IllegalArgumentException("Пользователь с ID " + user.getId() + " заблокирован и не может выполнять это действие");
        }
    }

    public void checkValidSubscriptionTarget(User subscriber, User author){
        if(subscriber.getId().equals(author.getId())){
            throw new InvalidAccessException("Нельзя подписаться на самого себя");
        }
        if (author.getRole() != Role.AUTHOR) {
            throw new InvalidAccessException("Можно подписываться только на авторов");
        }
    }

    private User getUserWithCache(
            Supplier<UserDto> getFromCache,
            Supplier<Optional<User>> getFromDb,
            Function<UserDto, String> keyInfo){
        UserDto cached = getFromCache.get();
        if(cached != null){return UserMapper.toEntity(cached);}

        User user = getFromDb.get()
                .orElseThrow(() -> new NotFoundException("Пользователь " + keyInfo.apply(null) + " не найден"));

        userCacheService.saveUser(UserMapper.toUserDto(user));
        return user;
    }

    public User checkUserExistsByEmail(String email) {
        return getUserWithCache(
                ()-> userCacheService.getUserByEmail(email),
                ()-> userRepository.findByEmail(email),
                dto -> "с email " + email
        );
    }

    public User getUserByNameOrThrow(String name) {
      return getUserWithCache(
              ()-> userCacheService.getUserByName(name),
              ()-> userRepository.findByName(name),
              dto-> "с именем " + name
      );
    }



    // Проверка существования пользователя
    public User checkUserExists(Long id){
       return getUserWithCache(
               ()-> userCacheService.getUser(id),
               ()-> userRepository.findById(id),
               dto-> "с ID " + id
       );
    }

    /*// Проверка существования новости
    public News checkNewsExistsCached(Long id) {
        NewsDto cachedNews = newsCacheService.getNews(id);
        if(cachedNews != null){
            return NewsMapper.toEntity(cachedNews);
        }
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Новость с ID " + id + " не найдена"));
        newsCacheService.saveNews(NewsMapper.toDto(news));
        return news;
    }*/
    // PermissionService
    public News checkNewsExists(Long id) {
        // ⚠️  НЕ возвращаем объект из кеша для update/delete
        News news = newsRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Новость с ID " + id + " не найдена"));
        return news;
    }




    // ✅ Только для логина — всегда из БД, чтобы был пароль
    public User getUserForAuthentication(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь с email " + email + " не найден"));
    }

    public Complaint checkComplaintExists(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Жалоба с ID " + id + " не найдена"));
    }

    public Reaction checkReactionExists(Long userId, Long newsId){
        return reactionRepository.findByUserIdAndNewsId(userId, newsId)
                .orElseThrow(() -> new NotFoundException("Реакция не найдена"));
    }

    public ReactionType checkValidReactionType(String reactionType) {
        try {
            return ReactionType.valueOf(reactionType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверный тип реакции: " + reactionType);
        }
    }


    public void checkSubscriberAuthorDuplicate(User subscriber, User author){
        if (subscriptionRepository.findBySubscriberAndAuthor(subscriber, author).isPresent()) {
            throw new AlreadyExistsException("Вы уже подписаны на этого автора");
        }
       }

       public Subscription getSubscriptionOrThrow(User subscriber, User author){
        return subscriptionRepository.findBySubscriberAndAuthor(subscriber,author)
                .orElseThrow(() -> new NotFoundException("Подписка не найдена"));
       }
       public Comment checkCommentExists(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(()-> new NotFoundException("Комментарий с ID " + commentId + " не найден"));
       }



    // Проверка уникальности имени
    public void checkUniqueUserName(String name){
        if(userRepository.existsByName(name)){
            throw new IllegalArgumentException("Имя '" + name + "' уже используется");
        }
    }




    // Проверка уникальности email
    public void checkUniqueUserEmail(String email){
        if(userRepository.existsByEmail(email)){
            throw new IllegalArgumentException("Email '" + email + "' уже используется");
        }
    }

    // Проверка корректности роли
    public Role checkValidRole(String role) {
        try {
            Role userRole = Role.valueOf(role.toUpperCase());
            if (userRole != Role.USER && userRole != Role.AUTHOR) {
                throw new IllegalArgumentException("Роль должна быть либо 'USER', либо 'AUTHOR'");
            }
            return userRole;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Некорректная роль: " + role);
        }
    }

    // Проверка корректности категории
    public Category checkValidCategory(String category) {
        try {
            return Category.valueOf(category.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Категория '" + category + "' не найдена");
        }
    }

    // Проверка прав на изменение или удаление новости на удаление
    public void checkNewsAccess(News news, Long currentUserId, Role currentUserRole) {
        if (!Role.ADMIN.equals(currentUserRole) && !news.getAuthor().getId().equals(currentUserId)) {
            throw new InvalidAccessException("Вы не можете изменять или удалять чужие новости");
        }
    }

    public void checkComplaintAccess(User currentUser, Complaint complaint, Role currentUserRole) {
        if (!Role.ADMIN.equals(currentUserRole) &&
                !complaint.getFromUser().getId().equals(currentUser.getId())) {
            throw new InvalidAccessException("Вы не можете изменять или удалять чужие жалобы");
        }
    }


    // Проверка прав на доступ к данным пользователя
    public void checkUserOrAdminAccess(Long ownerId, Long currentUserId, Role currentUserRole, String entityName) {
        if (ownerId == null || currentUserId == null) {
            throw new IllegalArgumentException("Идентификаторы пользователей не могут быть пустыми");
        }
        checkValidRoleNotNull(currentUserRole);
        if (!Role.ADMIN.equals(currentUserRole) && !ownerId.equals(currentUserId)) {
            throw new InvalidAccessException("Вы не можете изменять или удалять данные другого пользователя: " + entityName);
        }
    }

    // Проверка прав администратора на удаление
    public void checkAdminAccess(Role currentUserRole) {
        checkValidRoleNotNull(currentUserRole);
        if (!Role.ADMIN.equals(currentUserRole)) {
            throw new InvalidAccessException("Эта операция доступна только администраторам");
        }
    }

    // Проверка прав на комментарии на удаление
    public void checkCommentAccess(Long authorId, Long currentUserId, Role currentUserRole) {
        if (authorId == null || currentUserId == null) {
            throw new IllegalArgumentException("Идентификаторы пользователей не могут быть пустыми");
        }
        checkValidRoleNotNull(currentUserRole);
        if (!Role.ADMIN.equals(currentUserRole) && !authorId.equals(currentUserId)) {
            throw new InvalidAccessException("Вы не можете изменять или удалять чужие комментарии");
        }
    }
    public void commentAccess(User user, Comment comment) {
        if (!comment.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            throw new InvalidAccessException("Вы не можете удалить этот комментарий");
        }
    }

    // Проверка прав на создание новостей на УДАЛЕНИЕ
    public void checkCreationAccess(Role currentUserRole) {
        checkValidRoleNotNull(currentUserRole);
        if (!Role.AUTHOR.equals(currentUserRole) && !Role.ADMIN.equals(currentUserRole)) {
            throw new InvalidAccessException("Только авторы и администраторы могут создавать новости");
        }
    }

    // Проверка, что роль не пустая
    private void checkValidRoleNotNull(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Роль не может быть пустой");
        }}

        public Role checkValidRoleNotAdmin(String role) {
            try {
                Role userRole = Role.valueOf(role.toUpperCase());
                if (userRole == Role.ADMIN) {
                    throw new InvalidAccessException("Нельзя зарегистрироваться как ADMIN");
                }
                return userRole;
            } catch (IllegalArgumentException e) {
                throw new InvalidAccessException("Некорректная роль: " + role);
            }
        }
    }

