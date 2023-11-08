package com.library.management.dao;

import com.library.management.constants.ResponseConstants;
import com.library.management.dto.SignupRequest;
import com.library.management.dto.UserDto;
import com.library.management.entity.Books;
import com.library.management.entity.UserHistory;
import com.library.management.entity.Users;
import com.library.management.entity.enums.Action;
import com.library.management.exception.AlreadyExistException;
import com.library.management.exception.BookLimitReached;
import com.library.management.exception.BookNotAvailable;
import com.library.management.exception.PasswordException;
import com.library.management.repository.UserRepository;
import com.library.management.response.UserResponse;
import com.library.management.serviceimpl.HistoryServiceImpl;
import com.library.management.util.EmailSender;
import com.library.management.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class UserDao {

    @Autowired private UserRepository userRepository;
    @Autowired private HistoryServiceImpl historyService;

    @Autowired private PasswordUtil passwordUtil;

    @Autowired private EmailSender emailSender;
    @Autowired private HistoryDao historyDao;
    private UserDto userDto = new UserDto();
    private Users user= new Users();

    private UserResponse response = new UserResponse();



    public UserDto findUserDetails(SignupRequest signupRequest) throws AlreadyExistException {

        Users users = userRepository.findByEmail(signupRequest.getEmail());
        Users users1 = userRepository.findByPhoneNumber(signupRequest.getPhoneNumber());
        UserDto userDto = new UserDto();
        if (users == null && users1 == null)
            return null;
        else if (users != null || users1 != null) {
            throw new AlreadyExistException();
        } else if (users != null && users1 != null && (users.getEmail().equals(users1.getEmail())))  //checking both username and phone number are same and already exist or not
            return userDto.convertEntityToDto(users);
        else
            return null;
    }


    public UserDto saveUserDetails(SignupRequest signupRequest) {

       Users userObj = user.convertDtoToEntity(signupRequest);
        userObj.setPassword(passwordUtil.generateToken(userObj.getPassword()));
        Users users =  userRepository.save(userObj);
        return userDto.convertEntityToDto(users);
    }


    public UserDto findByUserId(long empId) {
        Users users = userRepository.findByEmpId(empId);
        return userDto.convertEntityToDto(users);
    }

    public UserDto updateUsers(UserDto userDto) {

        Users users = user.convertDtoToEntity(userDto);
        Users user= userRepository.save(users);
        return userDto.convertEntityToDto(user);
    }

    public UserDto findUserByEmailOrPhoneNumber(String username) {
        Users users = userRepository.findByEmailOrPhoneNumber(username);
        if(users==null){
            return null;
        }
        return userDto.convertEntityToDto(users);
    }


    public UserDto updateUsersWithBooks(UserDto uDto, List<Books> book, UserDto adminDto, String bookName) throws BookLimitReached, BookNotAvailable {
        if(book.size()>2){
            throw new BookLimitReached();
        }
        //to check the list has NO for available entity
       boolean avialability = book.stream().allMatch(b->b.getAvailable().equals(ResponseConstants.NOT_AVAILABLE));
        if(avialability)
        {
            throw new BookNotAvailable(book.get(0).getBookName()+ResponseConstants.BOOK_NOT_AVAILABLE);
        }
        Users users = user.convertDtoToEntity(uDto);
        book = book.stream().map(b->{
            b.setAvailable(ResponseConstants.NOT_AVAILABLE);
            b.setIssuedOn(new Date());
            try {
                b.setReturnDate(setReturnDate());
            } catch (ParseException e) {
                Logger.getLogger(getClass().getName()).info(e.getMessage());
            }
            return b;
        }).collect(Collectors.toList());
        users.setBooks(book);
         userRepository.save(users);
        UserDto dto = userDto.convertEntityToDto(users);
        emailSender.sendBookLendingDetails(dto, adminDto, bookName, Action.BORROW);
        return dto;
    }

    /**
     * To set return date of books to users
     * @return
     * @throws ParseException
     */
    private Date setReturnDate() throws ParseException {
        LocalDate currentDate = LocalDate.now();
        LocalDate newDate = currentDate.plusDays(7);
        Date utilDate = Date.from(newDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = dateFormat.format(utilDate);
        return dateFormat.parse(formattedDate);
    }

    public List<UserDto> findAllUserDetails() {
        List<Users> userList = userRepository.findAllUsers();
        List<UserDto> userDtoList = new ArrayList<>();
        for(Users users : userList){
            userDtoList.add(userDto.convertEntityToDto(users));
        }
        return userDtoList;
    }

    public ResponseEntity<Object> saveMasterUser(SignupRequest signupRequest) throws AlreadyExistException {

        UserDto userDto = findUserDetails(signupRequest);
        if (userDto == null) {
            Users user = new Users().convertDtoToEntity(signupRequest);
            user.setPassword(passwordUtil.generateToken(user.getPassword()));
            Users user1 = userRepository.save(user);
            UserDto uDto = new UserDto().convertEntityToDto(user1);
            historyService.addToUserHistory(uDto, null, Action.MASTER,null);
            return new ResponseEntity<>(new UserResponse().ConvertObjectToResponse(uDto, ResponseConstants.MASTER_USER_ADDED), HttpStatus.OK);
        }
        return new ResponseEntity<>(new UserResponse().ConvertObjectToResponse(ResponseConstants.SIGNUP_EXIST_FAIL_MSG),HttpStatus.BAD_REQUEST);
    }


    public boolean isCorrectPassword(String encrypt, long empId) throws PasswordException {
        Users user = userRepository.findByEmpId(empId);
        List<UserHistory> historyList = historyDao.findByUsername(user.getEmail());
        if(encrypt.equals(user.getPassword())){
            throw new PasswordException("Password already present.Give a new password.");
        }
        for (UserHistory history : historyList) {
            long dayDiff = 0;
            if (history.getNote().contains(encrypt)) {
                LocalDate date1 = history.getUpdatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate date2 = LocalDate.now();

                dayDiff = ChronoUnit.DAYS.between(date1, date2);
                if(dayDiff>30){
                    long monthdiff = ChronoUnit.MONTHS.between(date1,date2);
                    throw new PasswordException("You used this password " + monthdiff + " month ago, please try with new one");
                }
                throw new PasswordException("You used this password " + dayDiff + " days ago, please try with new one");
            }
        }
        return true;

    }
}
