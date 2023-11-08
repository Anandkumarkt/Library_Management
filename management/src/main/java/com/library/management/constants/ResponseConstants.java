package com.library.management.constants;

public class ResponseConstants {

    private ResponseConstants(){
        throw new IllegalStateException();
    }
    public static final String SIGNUP_SUCCESS_MSG = "user saved successfully";
    public static final String SIGNUP_EXIST_FAIL_MSG = "user already exist";
    public static final String OTP_VALIDATED = "otp validated, user saved";
    public static final String OTP_ERROR = "please enter valid OTP";
    public static final String USER_NOT_VERIFIED = "user not verified , check your mail for OTP" ;

    public static final String INVALID_CREDENTIALS = "invalid credentials";

    public static final String LOGIN_SUCCESS = "login session created";

    public static final String INVALID_PASSWORD = "Check your password!!";
    public static final String ENTER_VALID_ROLE = "enter valid Role (ADMIN,USER)";
    public static final String USER_NOT_FOUND = "User not exist";
    public static final String USER_NOT_AUTHERIZED = "User not have permission to access this request";
    public static final String BOOK_ALREADY_EXISTS = "Book already exist";
    public static final String BOOK_ENROLLED = "Book enrolled in library";

    public static final String BOOK_ADDED_TO_USERS = "Book added to users";

    public static final String BOOK_NOT_FOUND = "Selected book is not available in library";
    public static final String BOOK_LIMIT_REACHED = "User already reached books limit (2)";
    public static final String AVAILABLE = "YES";

    public static final String NOT_AVAILABLE = "NO";
    public static final String BOOK_NOT_AVAILABLE = "Book is not available currently, check after this weekend";
    public static final String BOOK_SUBJECT = "Book Details From Library";
    public static final String USER_LISTED = "Users Listed";
    public static final String BOOK_LISTED = "Books Listed";
    public static final String EMPTY_LIBRARY = "All books are reserved ~ Sorry for this. Will let you know once books are available";
    public static final String FULL_LIBRARY = "All books are available! Notify the customers";
    public static final String REMINDER_MAIL_SENT = "Reminder Mail sent to Users";

    public static final String FILTER_CHECK="Click any one to filter out ";
    public static final String FILTER_ERROR = "No books available with this filter";
    public static final String USER_NOT_HAVE_THIS_BOOK = "User don't have this book";

    public static final String BOOK_UPDATED_TO_USERS = "Book Details Updated to User";
    public static final String MASTER_USER_ADDED = "MASTER User Created";
    public static final String PASSWORD_SENT = "OTP Validated..! TEMP pass sent to your mail";
    public static final String OTP_SENT = "OTP sent to your mail";
    public static final String OTP_SUBJECT = "OTP Verification | E-Library";
    public static final String PASS_SUBJECT  = "Temporary Password | E-Library";
    public static final String LENDING_FINISHED_REM = "Gentle Reminder!! | Lending Time Finished | Library";
    public static final String LENDING_EXCEED_REM = "Gentle Reminder!! | Lending Time End Tomorrow | Library";
    public static final String WRONG_PASSWORD = "Given Temp Password is Invalid";
    public static final String PASSWORD_MISMATCH = "Password mismatch.";
    public static final String PASSWORD_UPDATED = "Password updated successfully";
    public static final String SESSION_EXPIRED = "user logged out successfully";
    public static final String SESSION_INVALID = "Invalid Session Token";
}
