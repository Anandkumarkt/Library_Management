package com.library.management.exception;

import com.library.management.entity.Books;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class BookNotAvailable extends Exception{

    private String message;
    public BookNotAvailable(){

    }

    public BookNotAvailable(String message) {

        this.message = message;

    }
}
