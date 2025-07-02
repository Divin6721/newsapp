package com.example.newsapp.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) //	Когда не найдена сущность
public class NotFoundException extends RuntimeException {
   public NotFoundException(String message){
       super(message);
   }
}
