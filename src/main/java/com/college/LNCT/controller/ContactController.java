package com.college.LNCT.controller;

import com.college.LNCT.dto.ContactRequest;
import com.college.LNCT.dto.SubscribeRequest;
import com.college.LNCT.service.ContactService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@Slf4j
public class ContactController {

    @Autowired
    private ContactService contactService;

    // Send contact message
    @PostMapping("/contact")
    public ResponseEntity<String> sendContact(@RequestBody ContactRequest request) {
        log.info("Contact message received from: {}", request.getEmail());

        try {
            contactService.saveContact(request);
            return ResponseEntity.ok("Message received. We will contact you soon.");

        } catch (Exception e) {
            log.error("Error saving contact message", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error sending message");
        }
    }

    // Subscribe to newsletter
    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestBody SubscribeRequest request) {
        log.info("Newsletter subscription request: {}", request.getEmail());

        try {
            contactService.subscribe(request);
            return ResponseEntity.ok("Subscribed successfully!");

        } catch (RuntimeException e) {
            log.warn("Subscription error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (Exception e) {
            log.error("Error subscribing to newsletter", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error subscribing");
        }
    }

    // Unsubscribe from newsletter
    @PostMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribe(@RequestParam String email) {
        log.info("Newsletter unsubscribe request: {}", email);

        try {
            contactService.unsubscribe(email);
            return ResponseEntity.ok("Unsubscribed successfully");

        } catch (RuntimeException e) {
            log.warn("Unsubscribe error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found");

        } catch (Exception e) {
            log.error("Error unsubscribing from newsletter", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error unsubscribing");
        }
    }
}

