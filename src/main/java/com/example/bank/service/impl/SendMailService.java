package com.example.bank.service.impl;

import com.example.bank.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class SendMailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void sendVerificationMail(String to, String subject, User user,String templateName) throws MessagingException {
        final Context ctx = new Context();
        ctx.setVariable("user", user);
        ctx.setVariable("name", user.getName());

        final String htmlContent = templateEngine.process(templateName, ctx);

        final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        message.setSubject(subject);
        message.setTo(to);

        message.setText(htmlContent, true);

        javaMailSender.send(mimeMessage);
    }
}