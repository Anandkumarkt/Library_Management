package com.library.management.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LibraryEmptyException extends Exception{

    private String message;

    public LibraryEmptyException(){

    }

    public LibraryEmptyException(String message){
        this.message = message;
    }
}
