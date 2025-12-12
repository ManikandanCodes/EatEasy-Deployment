package com.example.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key:}")
    private String brevoApiKey;

    @Value("${brevo.sender.email:eateasy.demo@gmail.com}")
    private String senderEmail;

    @Value("${brevo.sender.name:EatEasy App}")
    private String senderName;

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean sendEmail(String to, String subject, String body) {
        if (brevoApiKey == null || brevoApiKey.isEmpty()) {
            System.err.println("Brevo API Key is missing. Cannot send email.");
            return false;
        }

        String url = "https://api.brevo.com/v3/smtp/email";

        // Request Headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);
        headers.set("accept", "application/json");

        // Request Body
        Map<String, Object> requestBody = new HashMap<>();

        // Sender
        Map<String, String> sender = new HashMap<>();
        sender.put("name", senderName);
        sender.put("email", senderEmail);
        requestBody.put("sender", sender);

        // Recipient
        Map<String, String> recipient = new HashMap<>();
        recipient.put("email", to);
        requestBody.put("to", Collections.singletonList(recipient));

        // Content
        requestBody.put("subject", subject);
        requestBody.put("htmlContent", body); // Assuming body is safe HTML or plain text

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Email sent successfully to " + to + " via Brevo API.");
                return true;
            } else {
                System.err.println("Failed to send email via Brevo. Status: " + response.getStatusCode());
                System.err.println("Response: " + response.getBody());
                return false;
            }
        } catch (Exception e) {
            System.err.println("Exception sending email via Brevo to " + to + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
