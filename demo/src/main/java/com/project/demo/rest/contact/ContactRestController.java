package com.project.demo.rest.contact;


import com.project.demo.logic.entity.contact.ContactForm;
import com.project.demo.logic.entity.email.EmailService;
import com.project.demo.logic.entity.user.User;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/contact")
public class ContactRestController {
    @GetMapping
    public String contactTest() {
        return "Contact Test";
    }


    @Autowired
    private EmailService emailService;


    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendEmail(@RequestBody ContactForm contactForm) throws MessagingException {
        String message = "Technical support requested from = " + contactForm.getEmail();
        emailService.sendEmail(contactForm);
        return ResponseEntity.ok("Email sent");
    }
}
