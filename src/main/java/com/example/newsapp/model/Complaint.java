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
@Table(name = "complaint_table")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10000)
    @NotEmpty
    @Size(min = 100, max = 10000)
    private String content;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime createdAt;


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User fromUser;


    @ManyToOne
    @JoinColumn(name = "news_id", nullable = true)
    @NotNull
    private News news;


    @Column(nullable = true, length = 10000)
    private String response;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm")
    @Column(nullable = true)
    private LocalDateTime respondedAt;

    @PrePersist
    public  void  prePersist(){
        this.createdAt = LocalDateTime.now();
    }
}
