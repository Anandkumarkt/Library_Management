package com.library.management.exception;

import lombok.Getter;

@Getter
public class PasswordException extends Exception{

    private String message;
    public PasswordException(String message) {
        this.message = message;
    }


}
