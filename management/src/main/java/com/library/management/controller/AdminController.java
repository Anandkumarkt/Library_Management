package com.library.management.controller;

import com.library.management.dao.UserDao;
import com.library.management.dto.BooksDto;
import com.library.management.dto.LoginRequest;
import com.library.management.dto.PasswordDto;
import com.library.management.dto.SignupRequest;
import com.library.management.dto.UserDto;
import com.library.management.entity.enums.Role;
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
import com.library.management.service.IBookService;
import com.library.management.service.IUserService;
import com.library.management.util.jwt.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@RestController
@RequestMapping("/api/v1/library")
public class AdminController {


    @Autowired
    private IUserService uService;
    @Autowired
    private IBookService bService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired private UserDao userDao;
    Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/start")
    public ResponseEntity<String> sample() {
        return new ResponseEntity<>("started", HttpStatus.OK);
    }

    /**
     * @purpose to add new member in a database | admin only access
     * @param signupRequest
     * @param request
     * @return
     * @throws IOException
     * @throws AlreadyExistException
     * @throws UserNotAutherized
     * @throws IllegalRoleException
     * @throws UserNotFoundException
     */
    @PostMapping("/add-members")
    public ResponseEntity<?>  signup(@RequestBody SignupRequest signupRequest, HttpServletRequest request) throws IOException, AlreadyExistException, UserNotAutherized, IllegalRoleException, UserNotFoundException, SessionExpirationException {
        UserDto userDto = findUserNameFromToken(request);
        if (userHasAdminAccess(userDto)) {

            if (signupRequest.getRole().equals(String.valueOf(Role.USER.name())) || signupRequest.getRole().equals(String.valueOf(Role.ADMIN.name()))) {
                logger.info("valid member");
                return new ResponseEntity<>(uService.saveUser(signupRequest, userDto), HttpStatus.OK);
            } else {
                logger.info("illegal ROLE");
                throw new IllegalRoleException();
            }

        } else
            return null;
    }


    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestParam  String email) throws UserNotFoundException, IOException {
        return new ResponseEntity<>(uService.forgetPassword(email),HttpStatus.OK);
    }

    /**
     *
     * @param userId
     * @param otp
     * @purpose it is only for demo purpose , no use for this api
     */
    @PostMapping("/otp-verification")
    public ResponseEntity<?> otpVerification(@RequestParam long userId,@RequestParam int otp){

        return new ResponseEntity<>(uService.validateOTP(userId,otp),HttpStatus.OK);

    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordDto passwordDto) throws PasswordException {

        return new ResponseEntity<>(uService.changePassword(passwordDto),HttpStatus.OK);
    }

    /**
     * @purpose to login into application
     * @param loginRequest
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){

        return new ResponseEntity<>(uService.validateCredentials(loginRequest),HttpStatus.OK);
    }

    /**
     * @purpose to add books in a library
     * @param booksDto
     * @param request
     * @return
     * @throws UserNotFoundException
     * @throws UserNotAutherized
     */
    @PostMapping("/add-books-library")
    public ResponseEntity<?> addBooksToLibrary(@RequestBody BooksDto booksDto,HttpServletRequest request) throws UserNotFoundException, UserNotAutherized, SessionExpirationException {

        UserDto dto = findUserNameFromToken(request);
        if (userHasAdminAccess(dto)) {
            return bService.addBooks(booksDto, dto);
        } else
            return null;
    }

    /**
     * @purpose add books to a particular user
     * @param username
     * @param bookName
     * @param request
     * @return
     * @throws UserNotFoundException
     * @throws UserNotAutherized
     * @throws BookLimitReached
     * @throws BookNotFoundException
     * @throws BookNotAvailable
     */
    @PostMapping("/add-books-user")
    public ResponseEntity<?> addBooksToUsers(@RequestParam("username") String username,@RequestParam("bookName") String bookName,HttpServletRequest request) throws UserNotFoundException, UserNotAutherized, BookLimitReached, BookNotFoundException, BookNotAvailable, SessionExpirationException {
        UserDto dto = findUserNameFromToken(request);
        if (userHasAdminAccess(dto)) {
            return bService.addBooksToUser(username, bookName, dto);
        } else
            return null;
    }

    /**
     * @purpose to update a book after returning
     * @param username
     * @param bookName
     * @param request
     * @return
     * @throws UserNotFoundException
     * @throws UserNotAutherized
     */
    @PostMapping("/update-books-user")
    public ResponseEntity<?> updateBooksToUsers(@RequestParam("username") String username,@RequestParam("bookName") String bookName,HttpServletRequest request) throws UserNotFoundException, UserNotAutherized, BookNotAvailable, SessionExpirationException {

        UserDto dto = findUserNameFromToken(request);
        if (userHasAdminAccess(dto)) {
            return bService.updateBooksToUser(username, bookName, dto);
        } else
            return null;
    }

    /**
     * @purpose to view all the user & self details with their books
     * @param request
     * @return
     * @throws UserNotFoundException
     */
    @GetMapping("/get-user-details")
    public ResponseEntity<?> getUserDetails(HttpServletRequest request) throws UserNotFoundException, SessionExpirationException {

        UserDto dto = findUserNameFromToken(request);
        if(checkAdminAccess(dto)){
            return bService.getUserDetails(dto.getEmail(),Role.ADMIN.name());
        }
        else {
            return bService.getUserDetails(dto.getEmail(), Role.USER.name());
        }
    }


    /**
     * @purpose to view all the available books in the library to add in the userList
     * @param request
     * @return
     */
    @GetMapping("/view-all-books")
    public ResponseEntity<?> getBookDetails(HttpServletRequest request) throws LibraryEmptyException, SessionExpirationException {

        UserDto dto = findUserNameFromToken(request);
        if(checkAdminAccess(dto)){
            return bService.getBookDetails(dto.getEmail(), Role.ADMIN.name());
        }
        else{
            return bService.getBookDetails(dto.getEmail(), Role.USER.name());
        }

    }

    @GetMapping("/reserved-books")
    public ResponseEntity<?> reservedBooks(HttpServletRequest request) throws UserNotFoundException, UserNotAutherized, NoUserHasBooksException, SessionExpirationException {
        //write a code  to separate unavailable books from all users list

        UserDto userDto = findUserNameFromToken(request);
        if (userHasAdminAccess(userDto)) {
            return bService.getAllBookDetails();
        } else
            return null;
    }


    @GetMapping("/mail-trigger")
    public ResponseEntity<?> mailTrigger(){
        return uService.autoMailTrigger();
    }

    @GetMapping("/admin-mail-trigger")
    public ResponseEntity<?> manualMailTrigger(HttpServletRequest request) throws UserNotFoundException, UserNotAutherized, SessionExpirationException {
        UserDto dto = findUserNameFromToken(request);
        if (userHasAdminAccess(dto))
            return uService.autoMailTrigger();
        else
            return null;
    }
    @GetMapping("/filter")
    public ResponseEntity<?> filter(@RequestParam(value = "bookName",required = false) String bookName,
                                    @RequestParam(value = "author",required = false) String author,
                                    @RequestParam(value = "genre",required = false)String genre,
                                    @RequestParam(value = "available",required = false)String available,
                                    HttpServletRequest request) throws ChooseFilterException, SessionExpirationException {

        UserDto dto = findUserNameFromToken(request);
        if (bookName == null && author == null && genre == null && available == null) {
            throw new ChooseFilterException();
        }
        if (checkAdminAccess(dto)) {
            return bService.filterBooks(dto, bookName, author, genre, available);
        } else
            return bService.filterBooks(dto, bookName, author, genre, available);
    }

    @GetMapping("/log-out")
    public ResponseEntity<?> logout(HttpServletRequest request) throws SessionExpirationException {
        String token = request.getHeader("Authorization");
        UserDto dto = findUserNameFromToken(request);
        return new ResponseEntity<>(uService.logoutSession(dto,token.substring(7)),HttpStatus.OK);
    }

    private boolean userHasAdminAccess(UserDto dto) throws UserNotFoundException, UserNotAutherized {
        if(dto==null){
            logger.info("user not found");
            throw new UserNotFoundException();
        }
        else if(dto.getRole().equals(String.valueOf(Role.USER.name()))){
            logger.info("user not authorized");
            throw new UserNotAutherized();
        }
        else
            return true;
    }


    private UserDto findUserNameFromToken(HttpServletRequest request) throws SessionExpirationException {
        String token = request.getHeader("Authorization");
        token = token.substring(7);
        String username = jwtUtil.getUsernameFromToken(token);
        if(jwtUtil.isTokenBlackListed(token)){
            logger.error("JWT already expired!!!");
        }
        return userDao.findUserByEmailOrPhoneNumber(username);
    }

    private boolean checkAdminAccess(UserDto dto) {
        return (dto.getRole().equals(Role.ADMIN.name()));
    }



}
