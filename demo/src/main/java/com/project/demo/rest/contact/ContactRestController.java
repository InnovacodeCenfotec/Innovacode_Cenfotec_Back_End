package com.project.demo.rest.contact;

import com.project.demo.logic.entity.contact.ContactForm;
import com.project.demo.logic.entity.email.EmailService;
import com.project.demo.logic.entity.user.User;
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
    public ResponseEntity<?> sendEmail(@RequestBody ContactForm contactForm) {
        emailService.sendEmail(contactForm);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    /*
    * @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody User user) {
        String token = userService.createPasswordResetToken(user);
        if (token == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        String resetLink = "Enter in this link to reset your password: " + "http://localhost:4200/reset-password" + " Your token is " + token;
        emailService.sendEmail(user.getEmail(), "Password Reset Request", "To reset your password, click the link below:\n" + resetLink);
        return ResponseEntity.ok("Password reset link sent to your email");
    }
    * */
}
