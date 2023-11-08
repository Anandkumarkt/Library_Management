package com.library.management.util;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.library.management.constants.TemplateConstants;
import com.library.management.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class OtpUtil {

    private static final Integer OTP_EXPIRE_MINS = 4;

    private static final Integer PWD_EXPIRE_MINS = 10;
    private LoadingCache<String, Integer> otpCache;

    private LoadingCache<Long,String> passwordCache;

    /**
     * @purpose to save the current otp in the device cache
     */
    public OtpUtil() {
        super();
        otpCache = CacheBuilder.newBuilder().expireAfterWrite(OTP_EXPIRE_MINS, TimeUnit.MINUTES)
                .build(new CacheLoader<String, Integer>() {
                    public Integer load(String key) {
                        return 0;
                    }
                });
        passwordCache = CacheBuilder.newBuilder().expireAfterWrite(PWD_EXPIRE_MINS, TimeUnit.MINUTES)
                .build(new CacheLoader<Long, String>() {
                    @Override
                    public String load(Long key) throws Exception {
                        return "";
                    }
                });
    }

    public  boolean tempPasswordVerify(String tempPass, long userId) {

        try {
            if(passwordCache.get(userId).equals(tempPass))
                return true;
            else
                return false;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param key
     * @return int
     * @purpose to generate the otp randomly
     */
    public int generateOTP(String key) {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        otpCache.put(key, otp);

        return otp;
    }

    /**
     * @param key
     * @return int
     * @purpose to get the otp from the cache
     */
    public int getOTP(String key) {
        try {
            int otp = otpCache.get(key);
            clearOTP(key);
            return otp;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @param key
     * @purpose to clear the otp from the cache
     */
    public void clearOTP(String key) {
        otpCache.invalidate(key);
    }

    /**
     *
     * @param userDto
     * @return String
     * @purpose to generate a alphanumeric value
     */
    public String generatePassword(UserDto userDto) {
        int length = 8;
        StringBuilder sbr = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(TemplateConstants.ALPHA_NUMERIC_STRING.length());
            char randomChar = TemplateConstants.ALPHA_NUMERIC_STRING.charAt(index);
            sbr.append(randomChar);
        }
        sbr.append(userDto.getEmpId());
        passwordCache.put(userDto.getEmpId(), sbr.toString());
        return sbr.toString();

    }

    /**
     * To remove the temp password from the password cache
     * @param key
     */
    public void clearPassword(long key) {
        passwordCache.invalidate(key);
    }
}
