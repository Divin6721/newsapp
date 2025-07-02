package com.example.newsapp.security;


import com.example.newsapp.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Role role;
    private final Collection<? extends GrantedAuthority> authorities;// права доступа
    private final String name;

    @Override //Spring думает, что у каждого пользователя есть "username", но мы используем email как логин.
    public String getUsername() {return email;}

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
    //не просрочен ли аккаунт
    //не заблокирован ли он
    //действителен ли пароль
    //включён ли аккаунт
}

