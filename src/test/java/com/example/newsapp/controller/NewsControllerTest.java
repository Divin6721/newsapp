package com.example.newsapp.controller;

import com.example.newsapp.dto.NewsDto;
import com.example.newsapp.security.JwtFilter;
import com.example.newsapp.service.NewsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import org.springframework.test.web.servlet.MockMvc;


import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(NewsController.class)
public class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsService newsService;

    @MockBean
    private JwtFilter jwtFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    public  void  testFilterPageNews() throws Exception{
        NewsDto news = NewsDto.builder()
                .id(1L)
                .title("Filtered News") // ✅ теперь совпадает
                .content("Content me")
                .authorName("Author")
                .category("Politics")
                .build();

        Page<NewsDto> page = new PageImpl<>(List.of(news));

        Mockito.when(newsService.filterPageNews(
                Mockito.eq("Politics"),
                Mockito.eq("John"),
                Mockito.any(),
                Mockito.eq(100L),
                Mockito.eq(0), Mockito.eq(10),
                Mockito.eq("views"), Mockito.eq("desc")
        )).thenReturn(page);
        mockMvc.perform(get("/api/news/filter")
                        .param("category", "Politics")
                        .param("author", "John")
                        .param("minViews", "100")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "views")
                        .param("direction", "desc")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Filtered News"));
    }



}*/
