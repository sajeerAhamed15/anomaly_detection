package com.pickme.anomalydetection.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class MailService {
    @Autowired
    private JavaMailSender sender;


    @Value( "${mail.recievers}" )
    private String mailRecievers;

    public Boolean sendMail(String body) {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setTo(mailRecievers.split(","));
            helper.setSubject("Card Transactions Issue");
            helper.setText(body);
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
        sender.send(message);
        return true;
    }
}