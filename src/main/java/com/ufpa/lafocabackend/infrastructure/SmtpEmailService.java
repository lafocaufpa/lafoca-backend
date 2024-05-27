package com.ufpa.lafocabackend.infrastructure;

import com.ufpa.lafocabackend.core.email.EmailProperties;
import com.ufpa.lafocabackend.domain.exception.EmailException;
import com.ufpa.lafocabackend.domain.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class SmtpEmailService implements EmailService {

    private JavaMailSender javaMailSender;
    private EmailProperties emailProperties;

    public SmtpEmailService(JavaMailSender javaMailSender, EmailProperties emailProperties) {
        this.javaMailSender = javaMailSender;
        this.emailProperties = emailProperties;
    }

    @Override
    public void send(Message message) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            mimeMessageHelper.setSubject(message.getSubject());
            mimeMessageHelper.setText(message.getBody(), true);
            mimeMessageHelper.setTo(message.getRecipients().toArray(new String[0]));
            mimeMessageHelper.setFrom(emailProperties.getSender());
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new EmailException(e);
        }

    }
}
