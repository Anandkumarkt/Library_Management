package com.library.management.util.jwt;

import com.library.management.entity.Users;
import com.library.management.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
@Component
public class JwtAuthenticationProvider implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("entered loadUserByUserName method ");
        Users user = userRepository.findByEmail(username);
        if (user == null) {
            Users users = userRepository.findByPhoneNumber(username);
            logger.info("entered findByPhoneNumber method");
            return new org.springframework.security.core.userdetails.User(users.getPhoneNumber(), users.getPassword(),
                    new ArrayList<>());
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                new ArrayList<>());
    }
}
