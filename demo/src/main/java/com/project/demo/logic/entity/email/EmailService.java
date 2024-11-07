package com.project.demo.logic.entity.email;


import com.project.demo.logic.entity.contact.ContactForm;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;


@Service
public class EmailService {




    @Autowired
    private JavaMailSender mailSender;


    @Value("${spring.mail.username}")
    private String innovaCode;


    public void sendEmail(@RequestBody ContactForm contactForm) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject(contactForm.getSubject());


        String html =
                "<!doctype html>\n" +
                        "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\"\n" +
                        "      xmlns:th=\"http://www.thymeleaf.org\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\"\n" +
                        "          content=\"width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0\">\n" +
                        "    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n" +
                        "    <title>Email</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div> <h1> Technical Support Inquiry</h1> </div>\n" +
                        "<div> Sender: <b>" + contactForm.getName() + "</b></div>\n" +
                        "<div> Email: <b>" + contactForm.getEmail() + "</b></div>\n" +
                        "<div> Message: <b>" + contactForm.getMessage() + "</b></div>\n" +
                        "</body>\n" +
                        "</html>\n";
        helper.setText(html,true);
        helper.setTo(innovaCode);
        helper.setFrom(contactForm.getEmail());
        mailSender.send(mimeMessage);
    }
}
