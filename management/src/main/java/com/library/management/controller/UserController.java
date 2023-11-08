package com.library.management.controller;

import com.library.management.dao.UserDao;
import com.library.management.dto.SignupRequest;
import com.library.management.exception.AlreadyExistException;
import com.library.management.exception.UserNotAutherized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/library")
public class UserController {

    @Autowired
    private UserDao userDao;



    @Value("${app.jwtSecret}")
    private String masterCode;


    @PostMapping("/master-user")
    public ResponseEntity<Object> createMasterUser(@RequestBody SignupRequest signupRequest, HttpServletRequest request) throws UserNotAutherized, AlreadyExistException {

        String code = request.getHeader("MasterCode");
        if(code.equals(masterCode)){

            return userDao.saveMasterUser(signupRequest);
        }
        else{
            throw new UserNotAutherized();
        }
    }






}
