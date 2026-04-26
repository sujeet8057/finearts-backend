package com.college.LNCT.service;

import com.college.LNCT.dto.ContactRequest;
import com.college.LNCT.dto.SubscribeRequest;
import com.college.LNCT.entity.Contact;
import com.college.LNCT.entity.NewsletterSubscriber;
import com.college.LNCT.repository.ContactRepository;
import com.college.LNCT.repository.NewsletterRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private NewsletterRepository newsletterRepository;

    // Save contact message
    public void saveContact(ContactRequest request) {
        Contact contact = Contact.builder()
                .email(request.getEmail())
                .subject(request.getSubject())
                .message(request.getMessage())
                .isRead(false)
                .build();

        contactRepository.save(contact);
        log.info("Contact message saved from: {}", request.getEmail());

        // TODO: Send email notification to admin
    }

    // Subscribe to newsletter
    public void subscribe(SubscribeRequest request) {
        // Check if already subscribed
        if (newsletterRepository.findByEmail(request.getEmail()).isPresent()) {
            NewsletterSubscriber subscriber = newsletterRepository.findByEmail(request.getEmail()).get();
            if (subscriber.getIsActive()) {
                throw new RuntimeException("Already subscribed");
            }
            // Reactivate subscription
            subscriber.setIsActive(true);
            newsletterRepository.save(subscriber);
            log.info("Newsletter subscription reactivated: {}", request.getEmail());
            return;
        }

        NewsletterSubscriber subscriber = NewsletterSubscriber.builder()
                .email(request.getEmail())
                .isActive(true)
                .build();

        newsletterRepository.save(subscriber);
        log.info("Newsletter subscriber added: {}", request.getEmail());

        // TODO: Send welcome email
    }

    // Unsubscribe from newsletter
    public void unsubscribe(String email) {
        NewsletterSubscriber subscriber = newsletterRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        subscriber.setIsActive(false);
        newsletterRepository.save(subscriber);
        log.info("Newsletter unsubscribed: {}", email);
    }
}

