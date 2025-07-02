package com.example.newsapp.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "news_table")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    @NotEmpty
    @Size(min = 5, max = 200)
    private String title;

    @NotEmpty
    @Size(min = 100, max = 20000)
    private String content;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private Integer views;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;


    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
        if(views == null) views = 0;
    }
}
