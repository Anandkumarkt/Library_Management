package com.library.management.serviceimpl;

import com.library.management.constants.ResponseConstants;
import com.library.management.dao.BookDao;
import com.library.management.dao.UserDao;
import com.library.management.dto.BooksDto;
import com.library.management.dto.LibraryDto;
import com.library.management.dto.UserDto;
import com.library.management.entity.enums.Action;
import com.library.management.entity.enums.Role;
import com.library.management.exception.BookLimitReached;
import com.library.management.exception.BookNotAvailable;
import com.library.management.exception.BookNotFoundException;
import com.library.management.exception.LibraryEmptyException;
import com.library.management.exception.NoUserHasBooksException;
import com.library.management.exception.UserNotFoundException;
import com.library.management.response.UserResponse;
import com.library.management.service.IBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements IBookService {


    @Autowired private BookDao bookDao;
    @Autowired private UserDao userDao;
    @Autowired private HistoryServiceImpl historyServiceImpl;

    private UserResponse response = new UserResponse();
    /**
     * @param booksDto
     * @param userDto
     * @return
     */
    @Override
    public ResponseEntity<?> addBooks(BooksDto booksDto, UserDto userDto) {

        BooksDto booksDto1 = bookDao.findByBookName(booksDto.getBookName());
        if(booksDto1==null){
            BooksDto dto = bookDao.addBooks(booksDto);
            List<BooksDto> booksList = new ArrayList<>();
            booksList.add(dto);
            historyServiceImpl.addToBookHistory(userDto.getEmail(), dto.getBookName(),booksList,Action.ADDBOOK);
            return new ResponseEntity<>(response.ConvertObjectToResponse(dto,ResponseConstants.BOOK_ENROLLED),HttpStatus.OK);
        }
        else if(booksDto1.getBookName().equalsIgnoreCase(booksDto.getBookName())){
            return new ResponseEntity<>(response.ConvertObjectToResponse(ResponseConstants.BOOK_ALREADY_EXISTS), HttpStatus.ALREADY_REPORTED);
        }
        else{
            return null;
        }
    }

    /**
     * @param username 
     * @param bookName
     * @return
     */
    @Override
    public ResponseEntity<?> addBooksToUser(String username, String bookName,UserDto adminDto) throws UserNotFoundException, BookNotFoundException, BookLimitReached, BookNotAvailable {

            UserDto booksDto = bookDao.addBooksToUsers(bookName,username,adminDto);
            historyServiceImpl.addToBookHistory(username,bookName,booksDto.getBooksList(), Action.ADD);
            return new ResponseEntity<>(response.ConvertObjectToResponse(booksDto,ResponseConstants.BOOK_ADDED_TO_USERS),HttpStatus.OK);

    }



    /**
     * @param username
     * @param bookName
     * @param adminDto
     * @return
     */
    @Override
    public ResponseEntity<?> updateBooksToUser(String username, String bookName, UserDto adminDto) throws UserNotFoundException, BookNotAvailable {

        UserDto bookDto = bookDao.updateBooksToUsers(bookName,username,adminDto);
        return new ResponseEntity<>(response.ConvertObjectToResponse(bookDto,ResponseConstants.BOOK_UPDATED_TO_USERS),HttpStatus.OK);
    }

    /**
     * @param username 
     * @param name
     * @return
     */
    @Override
    public ResponseEntity<?> getUserDetails(String username, String name) throws UserNotFoundException {
        List<UserDto> userList = new ArrayList<>();
        if(Role.ADMIN.name().equals(name)){
            userList = userDao.findAllUserDetails();
        }
        else{
            UserDto userDto = userDao.findUserByEmailOrPhoneNumber(username);
            if (userDto == null)
                throw new UserNotFoundException();
            else
                userList.add(userDto);
        }

        return new ResponseEntity<>(response.ConvertObjectToResponse(userList,ResponseConstants.USER_LISTED),HttpStatus.OK);
    }

    /**
     * @param username 
     * @param name
     * @return
     */
    @Override
    public ResponseEntity<?> getBookDetails(String username, String name) throws LibraryEmptyException {
        List<BooksDto> bookList;
        if(Role.ADMIN.name().equals(name)){
            bookList = bookDao.findAllBooks(null);
            List<LibraryDto> libList = bookList.stream().map(book->{
                LibraryDto dto = new LibraryDto();
                return dto.convertEntityToDto(book);
            }).collect(Collectors.toList());
            return new ResponseEntity<>(response.ConvertObjectToResponse(libList,ResponseConstants.BOOK_LISTED),HttpStatus.OK);
        }
        else{
            bookList = bookDao.findAllBooks(ResponseConstants.AVAILABLE);
            return new ResponseEntity<>(response.ConvertObjectToResponse(bookList,ResponseConstants.BOOK_LISTED),HttpStatus.OK);
        }

    }

    /**
     * @return 
     */
    @Override
    public ResponseEntity<?> getAllBookDetails() throws NoUserHasBooksException {

        List<LibraryDto> booksDtoList = bookDao.findAllBooksLenderDetails();

        return new ResponseEntity<>(response.ConvertObjectToResponse(booksDtoList,ResponseConstants.BOOK_LISTED),HttpStatus.OK);
    }

    /**
     * @param dto 
     * @param bookName
     * @param author
     * @param genre
     * @return
     */
    @Override
    public ResponseEntity<?> filterBooks(UserDto dto, String bookName, String author, String genre,String available) {
        List<LibraryDto> bookList;
        if(dto.getRole().equals(Role.ADMIN.name())){
             bookList = bookDao.filterBooks(bookName,author,genre,available);
        }
        else
            bookList=bookDao.filterBooks(bookName,author,genre,ResponseConstants.AVAILABLE);
        return new ResponseEntity<>(response.ConvertObjectToResponse(bookList,ResponseConstants.BOOK_LISTED),HttpStatus.OK);
    }







}
