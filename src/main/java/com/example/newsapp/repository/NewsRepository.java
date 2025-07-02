package com.example.newsapp.repository;


import com.example.newsapp.model.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface NewsRepository extends JpaRepository<News,Long>, JpaSpecificationExecutor<News> {
}
