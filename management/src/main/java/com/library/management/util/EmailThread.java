package com.library.management.util;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.Callable;

@Getter
@Setter
@Component
public class EmailThread implements Callable<String> {

    private Session session;
    private String toEmail;
    private String subject;
    private String body;

    @Value("${mail.smtp.user}")
    private String username;
    @Value("${mail.smtp.password}")
    private String password;


    @Override
    public String call() throws Exception {
        if (session == null) {

            Properties properties = new Properties();
            session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
        }

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(body, "text/html");

            // Use try-with-resources to ensure proper resource management
            try (Transport transport = session.getTransport("smtp")) {
                transport.connect();
                transport.sendMessage(message, message.getAllRecipients());
            }

            System.out.println("Mail sent");
            return "Mail sent";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "Error sending mail: " + e.getMessage();
        }
    }
}
