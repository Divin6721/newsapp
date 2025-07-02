package com.example.newsapp.service.impl;

import com.example.newsapp.cache.UserCacheService;
import com.example.newsapp.dto.UserDto;
import com.example.newsapp.mapper.UserMapper;
import com.example.newsapp.model.Role;
import com.example.newsapp.model.User;
import com.example.newsapp.repository.UserRepository;
import com.example.newsapp.service.PermissionService;
import com.example.newsapp.service.UserService;
import com.example.newsapp.specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final UserCacheService userCacheService;

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto registerUser(@Valid UserDto dto) {
        permissionService.checkUniqueUserName(dto.getName());
        permissionService.checkUniqueUserEmail(dto.getEmail());
            User user = User.builder()
                    .name(dto.getName())
                    .email(dto.getEmail())
                    .role(Role.valueOf(dto.getRole()))
                    .isBlocked(false)
                    .build();
            User registeredUser = userRepository.save(user);

            return UserMapper.toUserDto(registeredUser);
    }


    @Override
    @PreAuthorize("isAuthenticated()")
    public Page<UserDto> getAllUsers(int page, int size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<User> spec;
        if ("subscribers".equalsIgnoreCase(sortBy)) {
            spec = UserSpecification.orderBySubscribersCount(direction);
        } else {
            Sort sort = Sort.by(
                    "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC,
                    sortBy != null ? sortBy : "name"
            );
            pageable = PageRequest.of(page, size, sort);
            spec = Specification.where(null); // пустая фильтрация
        }

        return userRepository.findAll(spec, pageable).map(UserMapper::toUserDto);
    }

    @Override
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public UserDto getUserById(Long id) {
        User user = permissionService.checkUserExists(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public void deleteUser(Long id, Authentication authentication) {
        User userToDelete  = permissionService.checkUserExists(id);
         userRepository.delete(userToDelete);
         userCacheService.deleteUser(UserMapper.toUserDto(userToDelete));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto blockUser(Long id) {
     User user = permissionService.checkUserExists(id);
     permissionService.checkUserBlockStatus(user,false);
     user.setIsBlocked(true);
     User blockedUser = userRepository.save(user);
     return UserMapper.toUserDto(blockedUser);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto unblockUser(Long id) {
      User user = permissionService.checkUserExists(id);
      permissionService.checkUserBlockStatus(user,true);
      user.setIsBlocked(false);
      User unblockedUser = userRepository.save(user);
      return UserMapper.toUserDto(unblockedUser);

    }

}
