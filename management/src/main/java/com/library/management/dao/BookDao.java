package com.library.management.dao;

import com.library.management.constants.ResponseConstants;
import com.library.management.dto.BooksDto;
import com.library.management.dto.LibraryDto;
import com.library.management.dto.UserDto;
import com.library.management.entity.Books;
import com.library.management.entity.Users;
import com.library.management.entity.enums.Action;
import com.library.management.exception.BookLimitReached;
import com.library.management.exception.BookNotAvailable;
import com.library.management.exception.BookNotFoundException;
import com.library.management.exception.LibraryEmptyException;
import com.library.management.exception.NoUserHasBooksException;
import com.library.management.exception.UserNotFoundException;
import com.library.management.repository.BookHistoryRepository;
import com.library.management.repository.BookRepository;
import com.library.management.repository.UserRepository;
import com.library.management.serviceimpl.HistoryServiceImpl;
import com.library.management.util.EmailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BookDao {

    @Autowired private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private BookHistoryRepository bookhistRepo;
    @Autowired private HistoryServiceImpl historyServiceImpl;

    @Autowired private EmailSender emailSender;

    @Autowired private UserDao userDao;

    public BooksDto addBooks(BooksDto booksDto) {
        Books bookObj = new Books();
        Books books = bookObj.convertDtoToEntity(booksDto);
        books.setAvailable(ResponseConstants.AVAILABLE);
        Books books1 = bookRepository.save(books);
        return booksDto.convertEntityToDto(books1);

    }

    public BooksDto findByBookName(String bookName) {
        Books books =  bookRepository.findByBookName(bookName);
        if(books!=null) {
            BooksDto dto = new BooksDto();
            return dto.convertEntityToDto(books);
        }
        return null;
    }

    public UserDto addBooksToUsers(String bookName, String username,UserDto adminDto) throws UserNotFoundException, BookNotFoundException, BookLimitReached, BookNotAvailable {
       List<Books> book =new ArrayList<>();
        UserDto uDto = userDao.findUserByEmailOrPhoneNumber(username);
        if(uDto!=null) {
            book.addAll(bookRepository.findByUserId(uDto.getEmpId()));
            Books books = bookRepository.findByBookName(bookName);
            if(books==null){
                throw new BookNotFoundException();
            }
             book.add(books);
            return userDao.updateUsersWithBooks(uDto,book,adminDto,bookName);
        }
        else{
            throw new UserNotFoundException();
        }

    }

    public UserDto updateBooksToUsers(String bookName, String username, UserDto adminDto) throws UserNotFoundException, BookNotAvailable {
        UserDto uDto = userDao.findUserByEmailOrPhoneNumber(username);
        UserDto mailDto=uDto;
        if(uDto!=null){
            List<Books> bookList = bookRepository.findByUserId(uDto.getEmpId());
            if(bookList.isEmpty()){
                throw new BookNotAvailable(ResponseConstants.USER_NOT_HAVE_THIS_BOOK);
            }

            List<Books> remainBook = new ArrayList<>();
            bookList.stream().forEach(books -> {
                if(books.getBookName().equals(bookName)){
                    books.setAvailable(ResponseConstants.AVAILABLE);
                    books.setIssuedOn(null);
                    books.setReturnDate(null);

                }
                else {
                    remainBook.add(books);
                }
            });
            Users users = new Users().convertDtoToEntity(uDto);
            users.setBooks(bookList);
            users = userRepository.save(users);
            bookList.forEach(book->{
                bookRepository.updateUserId(book.getBookId());
            });
            users.setBooks(remainBook);
            uDto = new UserDto().convertEntityToDto(users);
            historyServiceImpl.addToBookHistory(username,bookName,mailDto.getBooksList(),Action.UPDATE);
            emailSender.sendBookLendingDetails(mailDto,adminDto,bookName, Action.RETURN);
            return uDto;
        }
        throw new UserNotFoundException();
    }

    /**
     * to list all the books accoring to the role
     * @param available
     * @return
     */
    public List<BooksDto> findAllBooks(String available) throws LibraryEmptyException {
        List<Books> books;
        //to get the books for user view
        if(available!=null) {
             books = bookRepository.findAllBooksByAvailability(available);
        }//to get the books for admin's view
        else{
            books = bookRepository.findAll();
        }
        if(books.isEmpty()){
            throw new LibraryEmptyException(ResponseConstants.EMPTY_LIBRARY);
        }
//        List<BooksDto> booksDtoList = books.stream().map(book->{
//
//            dto = dto.convertEntityToDto(book);
//            return dto;
//        }).collect(Collectors.toList());
        BooksDto dto = new BooksDto();
        return books.stream().map(dto::convertEntityToDto).collect(Collectors.toList());
    }

    public List<LibraryDto> findAllBooksLenderDetails() throws NoUserHasBooksException {

        List<Books> booksList = bookRepository.findAllBooksByAvailability(ResponseConstants.NOT_AVAILABLE);
        if(booksList.isEmpty()){
            throw new NoUserHasBooksException();
        }

        return booksList.stream().map(book->{
            LibraryDto dto = new LibraryDto();
            return dto.convertEntityToDto(book);
        }).collect(Collectors.toList());
    }

    public List<LibraryDto> filterBooks(String bookName, String author, String genre, String available) {
        if (bookName != null)
            bookName = bookName.toLowerCase();
        if (author != null)
            author = author.toLowerCase();
        if (genre != null)
            genre = genre.toLowerCase();
        if (available != null)
            available = available.toLowerCase();

        List<Books> booksList = bookRepository.filterBooks(Optional.ofNullable(bookName),
                Optional.ofNullable(author),
                Optional.ofNullable(genre),
                Optional.ofNullable(available));

        if (booksList.isEmpty()) {
            try {
                throw new LibraryEmptyException(ResponseConstants.FILTER_ERROR);
            } catch (LibraryEmptyException e) {
                throw new RuntimeException(e);
            }
        }
        LibraryDto dto = new LibraryDto();
        return booksList.stream().map(dto::convertEntityToDto).collect(Collectors.toList());

    }



}
