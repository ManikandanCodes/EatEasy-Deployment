package com.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public boolean sendEmail(String to, String subject, String body) {
        if (mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                message.setFrom("eateasy.demo@gmail.com");
                mailSender.send(message);
                System.out.println("Email sent successfully to " + to);
                return true;
            } catch (Exception e) {
                System.err.println("Failed to send email to " + to + ": " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } else {
            System.err.println("JavaMailSender is not configured.");
            return false;
        }
    }
}
