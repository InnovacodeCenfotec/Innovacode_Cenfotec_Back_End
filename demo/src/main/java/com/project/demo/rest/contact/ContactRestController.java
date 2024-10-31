package com.project.demo.rest.contact;

import com.project.demo.logic.entity.contact.ContactForm;
import com.project.demo.logic.entity.contact.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
public class ContactRestController {
    @GetMapping
    public String contactTest() {
        return "Contact Test";
    }

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendEmail(@RequestBody ContactForm contactForm) {
        emailService.send(contactForm);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }
}
