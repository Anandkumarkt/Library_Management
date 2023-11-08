package com.library.management.exception.handler;

import com.library.management.constants.ResponseConstants;
import com.library.management.exception.AlreadyExistException;
import com.library.management.exception.BookLimitReached;
import com.library.management.exception.BookNotAvailable;
import com.library.management.exception.BookNotFoundException;
import com.library.management.exception.ChooseFilterException;
import com.library.management.exception.IllegalRoleException;
import com.library.management.exception.LibraryEmptyException;
import com.library.management.exception.NoUserHasBooksException;
import com.library.management.exception.PasswordException;
import com.library.management.exception.SessionExpirationException;
import com.library.management.exception.UserNotAutherized;
import com.library.management.exception.UserNotFoundException;
import com.library.management.response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<Object> alreadyFoundException(){
        UserResponse response = new UserResponse();
        response.setStatus(HttpStatus.ALREADY_REPORTED);
        response.setMessage(ResponseConstants.SIGNUP_EXIST_FAIL_MSG);
        return new ResponseEntity<>(response,HttpStatus.ALREADY_REPORTED);
    }

    @ExceptionHandler(IllegalRoleException.class)
    public ResponseEntity<Object> illegalRoleException(){
        UserResponse response = new UserResponse();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setMessage(ResponseConstants.ENTER_VALID_ROLE);
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> userNotFoundException(){
        UserResponse response = new UserResponse();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setMessage(ResponseConstants.USER_NOT_FOUND);
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotAutherized.class)
    public ResponseEntity<Object> userNotAuthorized(){
        UserResponse response = new UserResponse();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setMessage(ResponseConstants.USER_NOT_AUTHERIZED);
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<Object> bookNotFoundException(){
        UserResponse response = new UserResponse();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setMessage(ResponseConstants.BOOK_NOT_FOUND);
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(BookLimitReached.class)
    public ResponseEntity<Object> bookLimitReached(){
        UserResponse response = new UserResponse();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setMessage(ResponseConstants.BOOK_LIMIT_REACHED);
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookNotAvailable.class)
    public ResponseEntity<Object> bookNotAvailable(BookNotAvailable ex){
        UserResponse response = new UserResponse();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(LibraryEmptyException.class)
    public ResponseEntity<Object> libraryEmptyException(LibraryEmptyException ex){
        UserResponse response = new UserResponse();
        response.setStatus(HttpStatus.OK);
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @ExceptionHandler(NoUserHasBooksException.class)
    public ResponseEntity<Object> noUserHasBooksException(){
        UserResponse response = new UserResponse();
        response.setStatus(HttpStatus.OK);
        response.setMessage(ResponseConstants.FULL_LIBRARY);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    @ExceptionHandler(ChooseFilterException.class)
    public ResponseEntity<Object> chooseFilterException(){
        UserResponse response = new UserResponse();
        response.setStatus(HttpStatus.OK);
        response.setMessage(ResponseConstants.FILTER_CHECK);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @ExceptionHandler(SessionExpirationException.class)
    public ResponseEntity<Object> sessionExpirationException(){
        UserResponse response = new UserResponse();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setMessage(ResponseConstants.SESSION_INVALID);
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<Object> passwordException(PasswordException ex){
        UserResponse response = new UserResponse();
        response.setStatus(HttpStatus.BAD_REQUEST);
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }



}
