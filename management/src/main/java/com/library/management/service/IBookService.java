package com.library.management.service;

import com.library.management.dto.BooksDto;
import com.library.management.dto.UserDto;
import com.library.management.exception.BookLimitReached;
import com.library.management.exception.BookNotAvailable;
import com.library.management.exception.BookNotFoundException;
import com.library.management.exception.LibraryEmptyException;
import com.library.management.exception.NoUserHasBooksException;
import com.library.management.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface IBookService {
    ResponseEntity<?> addBooks(BooksDto booksDto, UserDto dto);

    ResponseEntity<?> addBooksToUser(String username, String bookName, UserDto dto) throws UserNotFoundException, BookNotFoundException, BookLimitReached, BookNotAvailable;

    ResponseEntity<?> getUserDetails(String username,String name) throws UserNotFoundException;

    ResponseEntity<?> getBookDetails(String username, String name) throws LibraryEmptyException;

    ResponseEntity<?> getAllBookDetails() throws NoUserHasBooksException;


    ResponseEntity<?> filterBooks(UserDto dto, String bookName, String author, String genre,String available);

    ResponseEntity<?> updateBooksToUser(String username, String bookName, UserDto dto) throws UserNotFoundException, BookNotAvailable;
}
