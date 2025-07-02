package com.example.newsapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users_table")
public class User {

    @Column(name = "name", unique = true, nullable = false, length = 10)
    @NotBlank
    @Size(min = 3, max = 10)
    private String name;

    @Column(unique = true, nullable = false)
    @NotBlank
    @Email
    private String email;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="is_blocked")
    private Boolean isBlocked;

    @Column(nullable = false)
    @NotBlank
    @Size(min = 6)
    private String password;

    private  String passwordConfirmation; //подтверждение пароля

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

}
