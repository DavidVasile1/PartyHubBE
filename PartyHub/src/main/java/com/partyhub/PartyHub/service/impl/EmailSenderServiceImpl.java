package com.partyhub.PartyHub.service.impl;

import com.partyhub.PartyHub.service.EmailSenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import lombok.RequiredArgsConstructor;

import java.util.logging.Logger;
import java.util.logging.Level;

@Service
@RequiredArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {
    private final JavaMailSender mailSender;
    private static final Logger logger = Logger.getLogger(EmailSenderServiceImpl.class.getName());


    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);  // Enable HTML content

            mailSender.send(message);
        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "Failed to send email to " + to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
    public void sendHtmlEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // Enable HTML content

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

}
