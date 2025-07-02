package com.example.newsapp.config;


import com.example.newsapp.security.JwtFilter;
import com.example.newsapp.security.handler.RestAccessDeniedHandler;
import com.example.newsapp.security.handler.RestAuthEntryPoint;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter                jwtFilter;
    private final RestAccessDeniedHandler  accessDeniedHandler;   // пришли через конструктор
    private final RestAuthEntryPoint       authEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg)
            throws Exception {
        return cfg.getAuthenticationManager();
    }

    /** Основная конфигурация безопасности */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        /* Swagger-эндпойнты */
                        .requestMatchers("/v3/api-docs", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**",
                                "/swagger-resources/**", "/webjars/**").permitAll()

                        /* открытые эндпойнты приложения */
                        .requestMatchers("/api/login", "/api/register").permitAll()

                        /* админ-операции */
                        .requestMatchers("/api/users/block/**",
                                "/api/users/unblock/**").hasRole("ADMIN")

                        /* всё остальное — после проверки JWT */
                        .anyRequest().authenticated()
                )

                .exceptionHandling(h -> h
                        .accessDeniedHandler(accessDeniedHandler)     // 403 JSON
                        .authenticationEntryPoint(authEntryPoint)     // 401 JSON
                )

                .sessionManagement(s ->                       // no sessions, только JWT
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                /* JWT-фильтр до UsernamePasswordAuthenticationFilter */
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
