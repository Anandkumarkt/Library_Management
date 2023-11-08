package com.library.management.util;


import com.library.management.constants.ResponseConstants;
import com.library.management.constants.TemplateConstants;
import com.library.management.dto.BooksDto;
import com.library.management.dto.UserDto;
import com.library.management.entity.EmailTemplate;
import com.library.management.entity.Users;
import com.library.management.entity.enums.Action;
import com.library.management.exception.UserNotFoundException;
import com.library.management.repository.TemplateRepository;
import com.library.management.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Getter
@Setter
@Component
public class EmailSender {

    @Autowired private OtpUtil otpUtil;

    @Autowired private TemplateRepository templateRepository;

    @Autowired private UserRepository userRepository;

    @Autowired
    private ObjectFactory<EmailThread> emailSenderObject;

    @Value("${mail.smtp.user}")
    private String username;
    @Value("${mail.smtp.password}")
    private String password;


    private void sendMailToUser(Session session, String subject, String body,String toEmail){
       final EmailThread thread = emailSenderObject.getObject();
        thread.setSession(session);
        thread.setSubject(subject);
        thread.setBody(body);
        thread.setToEmail(toEmail);

       ThreadPoolUtil.getThreadPool().submit(thread);
    }

    /**
     * To configure the mail
     * @return
     */
    private Session mailConfiguration() {
        Properties props = new Properties();
        try {
            props.load(EmailSender.class.getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    /*used to sent mail in the v1
   private void sendMailToUser(Session session, String subject, String body,String toEmail) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setContent(body, "text/html");
        Transport.send(message);
        System.out.println("mail sent");
    }*/

    public void sendOtpToMail(String email) throws UserNotFoundException {

        Session session = mailConfiguration();
        Users user = userRepository.findByEmail(email);
        if (user==null)
            throw new UserNotFoundException();

        EmailTemplate emailTemplate = templateRepository.findByTemplateKey(TemplateConstants.OTP_TEMPLATE);
        String template = emailTemplate.getTemplate();
        template = template.replace("{To}",user.getName());
        template = template.replace("{Created By}",user.getName());
        template = template.replace("{Request ID}",String.valueOf(user.getEmpId()));
        template = template.replace("{username}",email);
        template = template.replace("{OTP}",String.valueOf(otpUtil.generateOTP(email)));
        template = template.replace("{desc-name}",user.getName());
        template = template.replace("{Web-URL}",TemplateConstants.WEB_LOGIN_URL);

        sendMailToUser(session,ResponseConstants.OTP_SUBJECT,template,email);
        System.out.println("OTP sent successfully.");

    }


    public void sendBookLendingDetails(UserDto dto, UserDto adminDto, String bookName, Action action) {

        Session session = mailConfiguration();

        List<BooksDto> booksList = dto.getBooksList();
        StringBuilder sbr = new StringBuilder();
        BooksDto booksDto =  booksList.stream().filter(i->i.getBookName().equals(bookName)).findFirst().orElse(null);
        if(booksDto==null){
            Logger.getLogger(getClass().getName()).info("Check the template table for the html template");
            return;
        }
        EmailTemplate emailTemplate = templateRepository.findByTemplateKey(TemplateConstants.BOOK_TEMPLATE);
        String template = emailTemplate.getTemplate();
        template = template.replace("{To}", dto.getName());
        template = template.replace("{Created By}", adminDto.getName());
        template = template.replace("{Request ID}",String.valueOf(booksDto.getBookId()));
        template = template.replace("{username}",dto.getEmail());
        template = template.replace("{Product Name}",bookName);
        template =template.replace("{Genre}",booksDto.getGenre());
        template = template.replace("{Admin Name}", adminDto.getEmail());
        template = template.replace("{Borrow Date}",booksDto.getIssuedOn().toString());
        template = template.replace("{Return Date}",booksDto.getReturnDate().toString());
        AtomicInteger count= new AtomicInteger(1);
        if(action.equals(Action.BORROW)) {
            sbr.append("Welcome <b>").append(dto.getName()).append("</b>")
                    .append("<br>Totally you have taken " + booksList.size() + " books <br> <br> ");
            booksList.stream().forEach(book-> sbr.append(String.valueOf(count.getAndIncrement())+". ").append(book.getBookName()).append("<br>"));
        }
        else if(action.equals(Action.RETURN)) {
            sbr.append("Welcome <b>").append(dto.getName()).append("</b><br> <br> You have returned the book : <b>")
                    .append(dto.getBooksList().get(booksList.size() - 1).getBookName())
                    .append("</b> on <b>").append(dto.getBooksList().get(booksList.size() - 1).getReturnDate()).append("</b>");

        }
        sbr.append("<br>Please visit the website to know more - ").append("<a href=").append(TemplateConstants.WEB_LOGIN_URL).append(">login</a>");
        template = template.replace("{Description}",sbr.toString());


        //to send a mail to the user with regarding information
        sendMailToUser(session,ResponseConstants.BOOK_SUBJECT,template,dto.getEmail());
        System.out.println("Mail sent successfully.");
    }

    /**
     * @param user
     * @param dayDiff
     * @param book
     * @purpoes to send a reminder email to user
     */
    public void sendReminderEmail(UserDto user, String dayDiff, BooksDto book) {

        Session session = mailConfiguration();
        String subject=null;
        StringBuilder sbr = new StringBuilder();


        sbr.append("<p>Hi ").append(user.getName()).append("!!!</p>");
        sbr.append("<p>You have taken the following book:</p>");
        sbr.append("<ol>");
        sbr.append("<li>Book Name: ").append(book.getBookName()).append("</li>");
        sbr.append("<li>Return Date: ").append(book.getReturnDate()).append("</li>");
        sbr.append("</ol>");

        if (dayDiff.equalsIgnoreCase("After")) {
            // Notification for exceeding 7 days
            sbr.append("<p>You've taken more than 7 days. Please return the book.</p>");
            subject = ResponseConstants.LENDING_FINISHED_REM;
        } else {
            // Notification for lending time exceeding tomorrow
            sbr.append("<p>Your lending time will exceed tomorrow.</p>");
            subject = ResponseConstants.LENDING_EXCEED_REM;
        }

        sbr.append("<p>Thank You!!!</p>");
        sendMailToUser(session,subject, sbr.toString(), user.getEmail());


    }

    public void sendPasswordMail(UserDto userDto) {
        Session session = mailConfiguration();

        EmailTemplate emailTemplate = templateRepository.findByTemplateKey(TemplateConstants.PASS_TEMPLATE);
        String template = emailTemplate.getTemplate();
        template = template.replace("{To}",userDto.getName());
        template = template.replace("{Created By}",userDto.getName());
        template = template.replace("{Request ID}",String.valueOf(userDto.getEmpId()));
        template = template.replace("{username}",userDto.getEmail());
        template = template.replace("{password}",otpUtil.generatePassword(userDto));
        template = template.replace("{desc-name}",userDto.getName());
        template = template.replace("{Web-URL}",TemplateConstants.WEB_LOGIN_URL);
        template = template.replace("{Pass-URL}",TemplateConstants.PASS_CHANGE_URL);
        sendMailToUser(session,ResponseConstants.PASS_SUBJECT, template,userDto.getEmail());

    }


    public void sendReminderEmail(UserDto user, List<BooksDto> lendingExceedList, List<BooksDto> lendingExceedTmrwList) {
        Session session = mailConfiguration();
        String template1 = null;
        EmailTemplate emailTemplate = templateRepository.findByTemplateKey(TemplateConstants.REMINDER_TEMPLATE);
        String template = emailTemplate.getTemplate();
        template = template.replace("{To}", user.getName());
        template = template.replace("{Created By}", "Library-Management");
        template = template.replace("{Request ID}", String.valueOf(user.getEmpId()));
        template = template.replace("{username}", user.getEmail());
        if (!lendingExceedList.isEmpty()) {
            AtomicInteger count= new AtomicInteger();
            count.getAndIncrement();
            StringBuilder sbr = new StringBuilder();
            sbr.append("<p>Hi ").append(user.getName()).append("!!!</p>");
            sbr.append("<p>You have taken the following books more than 7 days. Please return the book. </p>");
            lendingExceedList.forEach(book -> {
                sbr.append("<p>&nbsp;&nbsp;"+count.getAndIncrement()+".").append("<strong>Book Name: </strong>").append(book.getBookName()).append("&nbsp;&nbsp;&nbsp;&nbsp;");
                sbr.append("<strong>Return Date:</strong> ").append(book.getReturnDate()).append("</p><br>");
            });
            sbr.append("<p>Thank You!!!</p>");
            template1 = template;
            template = template.replace("{Description}", sbr.toString());
            sendMailToUser(session, ResponseConstants.LENDING_FINISHED_REM, template, user.getEmail());
        }
        if (!lendingExceedTmrwList.isEmpty()) {
            AtomicInteger count= new AtomicInteger();
            count.getAndIncrement();
            StringBuilder sbr1 = new StringBuilder();
            sbr1.append("<p>Hi <strong>").append(user.getName()).append("</strong> !!!</p>");
            sbr1.append("<p>Your lending time will end tomorrow.</p>");
            sbr1.append("<p>You have taken the following book:</p>");
            lendingExceedTmrwList.forEach(book -> {
                sbr1.append("<p>&nbsp;&nbsp;"+count.getAndIncrement()+".").append("<strong>Book Name: </strong>").append(book.getBookName()).append("&nbsp;&nbsp;&nbsp;&nbsp;");
                sbr1.append("<strong>Return Date:</strong> ").append(book.getReturnDate()).append("</p><br>");
            });
            sbr1.append("<p>Thank You!!!</p>");
            template1 = template1.replace("{Description}", sbr1.toString());
            sendMailToUser(session, ResponseConstants.LENDING_EXCEED_REM, template1, user.getEmail());

        }
    }


}
