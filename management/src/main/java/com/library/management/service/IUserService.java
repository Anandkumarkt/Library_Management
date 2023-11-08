package com.library.management.service;

import com.library.management.dto.BooksDto;
import com.library.management.dto.LoginRequest;
import com.library.management.dto.PasswordDto;
import com.library.management.dto.SignupRequest;
import com.library.management.dto.UserDto;
import com.library.management.exception.AlreadyExistException;
import com.library.management.exception.PasswordException;
import com.library.management.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface IUserService {
    Object saveUser(SignupRequest signupRequest, UserDto userDto) throws IOException, AlreadyExistException;

    Object validateOTP(long userId, int otp);

    Object validateCredentials(LoginRequest loginRequest);

    ResponseEntity<?> addBooks(BooksDto booksDto);

    ResponseEntity<?> autoMailTrigger();

    Object forgetPassword(String email) throws IOException, UserNotFoundException;

    Object changePassword(PasswordDto passwordDto) throws PasswordException;

    Object logoutSession(UserDto dto, String token);
}
