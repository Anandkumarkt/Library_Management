package com.library.management.serviceimpl;

import com.library.management.constants.ResponseConstants;
import com.library.management.dao.UserDao;
import com.library.management.dto.BooksDto;
import com.library.management.dto.LoginRequest;
import com.library.management.dto.PasswordDto;
import com.library.management.dto.SignupRequest;
import com.library.management.dto.UserDto;
import com.library.management.entity.enums.Action;
import com.library.management.exception.AlreadyExistException;
import com.library.management.exception.PasswordException;
import com.library.management.exception.UserNotFoundException;
import com.library.management.response.UserResponse;
import com.library.management.service.IUserService;
import com.library.management.util.*;
import com.library.management.util.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements IUserService {


    @Autowired private UserDao userDao;

    @Autowired private EmailSender emailSender;

    @Autowired private OtpUtil otpUtil;

    @Autowired HistoryServiceImpl historyService;

    @Autowired private JwtUtil jwtUtil;
    @Autowired private PasswordUtil passwordUtil;
    private UserResponse response = new UserResponse();


    /**
     * @param signupRequest
     */
    @Override
    public Object saveUser(SignupRequest signupRequest,UserDto adminDto) throws IOException, AlreadyExistException {

        UserDto user = userDao.findUserDetails(signupRequest);
        if(user==null) {
            UserDto userDto = userDao.saveUserDetails(signupRequest);
            historyService.addToUserHistory(userDto, adminDto, Action.ADD,null);
            return response.ConvertObjectToResponse(userDto, ResponseConstants.SIGNUP_SUCCESS_MSG);
        }
        else{
            return response.ConvertObjectToResponse(ResponseConstants.SIGNUP_EXIST_FAIL_MSG);
        }
    }

    /**
     * @param email
     * @return
     */
    @Override
    public Object forgetPassword(String email) throws IOException, UserNotFoundException {
        UserDto userDto = userDao.findUserByEmailOrPhoneNumber(email);
        if(userDto==null){
            throw new UserNotFoundException();
        }
        emailSender.sendOtpToMail(email);
        return response.ConvertObjectToResponse(userDto,ResponseConstants.OTP_SENT);
    }

    /**
     * @param dto
     * @param token
     * @return
     */
    @Override
    public Object logoutSession(UserDto dto, String token) {

        jwtUtil.addToBlackListToken(token);
        SecurityContextHolder.clearContext();
        return response.ConvertObjectToResponse(dto.getEmail(),ResponseConstants.SESSION_EXPIRED);
    }

    /**
     * @param passwordDto 
     * @return
     */
    @Override
    public Object changePassword(PasswordDto passwordDto) throws PasswordException {
        if(!passwordDto.getPassword().equals(passwordDto.getConfirmPassword()))
            return response.ConvertObjectToResponse(ResponseConstants.PASSWORD_MISMATCH);
        long userId = Long.parseLong(passwordDto.getTempPass().substring(8));
        if (otpUtil.tempPasswordVerify(passwordDto.getTempPass(), userId)) {
            UserDto userDto = userDao.findByUserId(userId);
            if (userDto != null) {
                String encrypt = passwordUtil.generateToken(passwordDto.getPassword());
                if (userDao.isCorrectPassword(encrypt, userId)) {
                    userDto.setPassword(encrypt);
                    historyService.addToUserHistory(userDto, null, Action.CHANGE, encrypt);
                    UserDto dto = userDao.updateUsers(userDto);
                    otpUtil.clearPassword(userId);
                    return response.ConvertObjectToResponse(dto, ResponseConstants.PASSWORD_UPDATED);
                }
            }
        }
        return response.ConvertObjectToResponse(ResponseConstants.WRONG_PASSWORD);
    }


    /**
     * @param userId 
     * @param otp
     * @return
     */
    @Override
    public Object validateOTP(long userId, int otp) {

        UserDto userDto = userDao.findByUserId(userId);

        if(userDto!=null ){
            if(otp==otpUtil.getOTP(userDto.getEmail())){
                emailSender.sendPasswordMail(userDto);
                return response.ConvertObjectToResponse(userDto,ResponseConstants.PASSWORD_SENT);
            }
        }
        return response.ConvertObjectToResponse(ResponseConstants.OTP_ERROR);
    }

    /**
     * @param loginRequest 
     * @return
     */
    @Override
    public Object validateCredentials(LoginRequest loginRequest) {
        Logger.getLogger(getClass().getName()).info("entered validateCredentials--------"+loginRequest.getUsername());
        UserDto userDto=null;
        if(StringUtil.isNotNull(loginRequest.getUsername())) {
             userDto = userDao.findUserByEmailOrPhoneNumber(loginRequest.getUsername());
        }
        if(userDto==null){
            return response.ConvertObjectToResponse(ResponseConstants.INVALID_CREDENTIALS);
        }
        Logger.getLogger(getClass().getName()).info("login password : "+loginRequest.getPassword());
        Logger.getLogger(getClass().getName()).info("user password : "+userDto.getPassword());
        //to check both the encrypted password are same
        if(userDto.getPassword().equals(passwordUtil.generateToken(loginRequest.getPassword()))){
            userDto.setToken(jwtUtil.generateToken(loginRequest.getUsername()));
            return response.ConvertObjectToResponse(userDto,ResponseConstants.LOGIN_SUCCESS);

        }
        else{
            return response.ConvertObjectToResponse(ResponseConstants.INVALID_PASSWORD);
        }
    }

    /**
     * @param booksDto 
     * @return
     */
    @Override
    public ResponseEntity<?> addBooks(BooksDto booksDto) {
        return null;
    }


    /**
     * @return 
     */
    @Override
    public ResponseEntity<?> autoMailTrigger() {

        List<UserDto> userDtoList = userDao.findAllUserDetails();

        userDtoList.stream().forEach(user->{
            List<BooksDto> lendingExceedList = new ArrayList<>();
            List<BooksDto> lendingExceedTmrwList = new ArrayList<>();
            Date currentDate = new Date();
            List<BooksDto> booksDtoList = user.getBooksList();
            booksDtoList.stream().forEach(book->{
               if(currentDate.after(book.getReturnDate())){
                   lendingExceedList.add(book);
                    //emailSender.sendReminderEmail(user,"After",book);

               }
               if(findBeforeDay(book.getReturnDate())){
                   lendingExceedTmrwList.add(book);
                  //  emailSender.sendReminderEmail(user,"Before",book);
               }
            });
            if ((!lendingExceedList.isEmpty() && lendingExceedTmrwList.isEmpty()) || (lendingExceedList.isEmpty() && !lendingExceedTmrwList.isEmpty()) || (!lendingExceedList.isEmpty() && !lendingExceedTmrwList.isEmpty()))
                emailSender.sendReminderEmail(user, lendingExceedList, lendingExceedTmrwList);
        });

        return new ResponseEntity<>(response.ConvertObjectToResponse(null,ResponseConstants.REMINDER_MAIL_SENT),HttpStatus.OK);
    }


    /**
     * @purpose to find given date is yesterday or not
     * @param givenDate
     * @return
     */
    private boolean findBeforeDay(Date givenDate) {
        Date currentDate = new Date();

        // Create Calendar instances
        Calendar givenCalendar = Calendar.getInstance();
        givenCalendar.setTime(currentDate);
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(givenDate);

        // Add/subtract one day from the current date
        currentCalendar.add(Calendar.DAY_OF_MONTH, -1);

        // Compare the given date with yesterday's date
        return givenCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR)
                && givenCalendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR);
    }
}
