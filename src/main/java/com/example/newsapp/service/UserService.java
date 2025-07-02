package com.example.newsapp.service;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import com.example.newsapp.dto.UserDto;


import java.util.List;

public interface UserService {
    UserDto registerUser(UserDto dto);
    Page<UserDto> getAllUsers(int page, int size, String sortBy, String direction);
    UserDto getUserById(Long id);
    void deleteUser(Long id, Authentication authentication);
    UserDto blockUser(Long id);
    UserDto unblockUser(Long id);

}
