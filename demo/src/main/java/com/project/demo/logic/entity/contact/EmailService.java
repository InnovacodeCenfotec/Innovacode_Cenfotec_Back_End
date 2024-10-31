package com.project.demo.logic.entity.contact;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailService {
    Logger logger = LogManager.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender emailSender;

    @Value("$(spring.mail.username)")
    private String emailTo;

    @Async
    public void send(ContactForm contactForm) {
        MimeMessagePreparator preparator = new MimeMessagePreparator(){

            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(emailTo));
                mimeMessage.setSubject(contactForm.getSubject());

                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                helper.setText(
                        "<html>" +
                                "<body>" +
                                    "Email sent by: " + contactForm.getName() + "<br/>" +
                                    "Email address: " + contactForm.getEmail() +
                                    "<br/><br/>" +
                                    contactForm.getMessage() +
                                "</body>" +
                         "</html>", true
                );
            }
        };

        try {
            emailSender.send(preparator);
            logger.info("Email sent successfully");
        } catch (Exception e) {
            logger.error("Error sending email");
            throw e;
        }
    }
}
