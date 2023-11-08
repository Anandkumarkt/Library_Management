package com.library.management.constants;

public class TemplateConstants {



    private TemplateConstants(){
        throw new IllegalStateException();
    }
    public static final String WEB_LOGIN_URL = "http:/localhost:8081/api/v1/library/login";

    public static final CharSequence PASS_CHANGE_URL = "http:/localhost:8081/api/v1/library/change-password";
    public static final String BOOK_TEMPLATE = "book_lending_details";
    public static final String OTP_TEMPLATE = "otp_details";
    public static final String PASS_TEMPLATE = "password_details";
    public static final String REMINDER_TEMPLATE = "reminder_details";
    public static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
}
