package com.example.newsapp.mapper;


import com.example.newsapp.dto.UserDto;
import com.example.newsapp.model.Role;
import com.example.newsapp.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .isBlocked(user.getIsBlocked())
                .build();
    }
    public static User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }

        return User.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .role(Role.valueOf(dto.getRole())) // преобразуем строку в enum
                .isBlocked(dto.getIsBlocked() != null ? dto.getIsBlocked() : false)
                .build();
    }
}


