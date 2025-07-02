package com.example.newsapp.service.impl;

import com.example.newsapp.cache.NewsCacheService;
import com.example.newsapp.dto.NewsCreateRequest;
import com.example.newsapp.dto.NewsDto;
import com.example.newsapp.mapper.NewsMapper;
import com.example.newsapp.model.Role;
import com.example.newsapp.specification.NewsSpecification;
import com.example.newsapp.util.PaginationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import com.example.newsapp.model.Category;
import com.example.newsapp.model.News;
import com.example.newsapp.model.User;
import com.example.newsapp.repository.NewsRepository;
import com.example.newsapp.service.NewsService;
import com.example.newsapp.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsRepository newsRepository;
    private final PermissionService permissionService;
    private final NewsCacheService newsCacheService;


    @Override
    @PreAuthorize("isAuthenticated()")
    public NewsDto getNewsById(Long id) {
        News news = permissionService.checkNewsExists(id);
        return NewsMapper.toDto(news);
    }


    @Override
    @PreAuthorize("isAuthenticated()")
    public NewsDto createNews(NewsCreateRequest dto, Authentication authentication) {
        String email = authentication.getName();
        User user = permissionService.checkUserExistsByEmail(email);
        Role role = user.getRole();
        permissionService.checkCreationAccess(role);

        Category category = permissionService.checkValidCategory(dto.getCategory());

        News news = NewsMapper.toEntity(dto);
        news.setAuthor(user);
        news.setCategory(category);

        News savedNews = newsRepository.save(news);
        newsCacheService.saveNews(NewsMapper.toDto(savedNews));
        return NewsMapper.toDto(savedNews);
    }


    @Override
    @PreAuthorize("isAuthenticated()")
    public void deleteNews(Long id, Authentication authentication) {
        String email = authentication.getName();
        User currentUser = permissionService.checkUserExistsByEmail(email);
        News news = permissionService.checkNewsExists(id);

        permissionService.checkNewsAccess(news, currentUser.getId(), currentUser.getRole());

        newsRepository.delete(news);
        newsCacheService.deleteNews(news.getId());
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public NewsDto updateNews(Long id, NewsCreateRequest dto, Authentication authentication) {
        String email = authentication.getName();
        User user = permissionService.checkUserExistsByEmail(email);
        News news = permissionService.checkNewsExists(id);

        permissionService.checkNewsAccess(news, user.getId(), user.getRole());

        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());

        Category category =permissionService.checkValidCategory(dto.getCategory());
        news.setCategory(category);

        News updatedNews = newsRepository.save(news);
        newsCacheService.saveNews(NewsMapper.toDto(updatedNews));
        return NewsMapper.toDto(updatedNews);
    }

    @Override
    public Page<NewsDto> filterPageNews(String category, String author, LocalDate dateFrom, Long minViews,
                                        int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction != null && direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PaginationUtils.createPageable(page, size, sortBy, direction);

        Specification<News> spec = Specification
                .where(NewsSpecification.hasCategory(category))
                .and(NewsSpecification.hasAuthor(author))
                .and(NewsSpecification.publishedAfter(dateFrom))
                .and(NewsSpecification.minViews(minViews));

        Page<News> newsPage = newsRepository.findAll(spec, pageable);

        return newsPage.map(NewsMapper::toDto);
    }

}
