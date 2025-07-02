package com.example.newsapp.mapper;

import com.example.newsapp.dto.ComplaintAdminResponseRequest;
import com.example.newsapp.dto.ComplaintUserRequest;
import com.example.newsapp.dto.ComplaintDto;
import com.example.newsapp.model.Complaint;
import com.example.newsapp.model.News;
import com.example.newsapp.model.User;

public class ComplaintMapper {

    public static ComplaintDto toDto(Complaint complaint) {
        return ComplaintDto.builder()
                .id(complaint.getId())
                .content(complaint.getContent())
                .fromUserName(complaint.getFromUser().getName())
                .newsId(complaint.getNews().getId())
                .response(complaint.getResponse())
                .createdAt(complaint.getCreatedAt())
                .respondedAt(complaint.getRespondedAt())
                .build();
    }
    public static Complaint toEntity(ComplaintUserRequest dto, User user, News news){
        return Complaint.builder()
                .content(dto.getContent())
                .fromUser(user)
                .news(news)
                .build();
    }
}