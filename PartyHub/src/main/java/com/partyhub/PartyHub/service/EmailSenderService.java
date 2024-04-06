package com.partyhub.PartyHub.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;

public interface EmailSenderService {
    void sendEmail(String to, String subject, String body);
    void sendHtmlEmail(String to, String subject, String body);
}
