package com.example.newsapp.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RefreshTokenService {

    private final Set<String> revokedTokens = ConcurrentHashMap.newKeySet(); // потокобезопасный список отозванных

    public void revokeToken(String token) {
        revokedTokens.add(token);
    }

    public boolean isRevoked(String token) {
        return revokedTokens.contains(token);
    }
}
