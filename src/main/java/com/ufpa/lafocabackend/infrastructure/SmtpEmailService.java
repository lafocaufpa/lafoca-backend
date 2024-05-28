package com.ufpa.lafocabackend.infrastructure;

import com.ufpa.lafocabackend.core.email.EmailProperties;
import com.ufpa.lafocabackend.domain.exception.EmailException;
import com.ufpa.lafocabackend.domain.service.EmailService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;

@Component
public class SmtpEmailService implements EmailService {

    private JavaMailSender javaMailSender;
    private EmailProperties emailProperties;
    private Configuration freeMarkerConfig;

    public SmtpEmailService(JavaMailSender javaMailSender, EmailProperties emailProperties, Configuration freeMarkerConfig) {
        this.javaMailSender = javaMailSender;
        this.emailProperties = emailProperties;
        this.freeMarkerConfig = freeMarkerConfig;
    }

    @Override
    public void send(Message message) {

        String body = processTemplate(message);

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, "UTF-8");
            mimeMessageHelper.setSubject(message.getSubject());
            mimeMessageHelper.setText(body, true);
            mimeMessageHelper.setTo(message.getRecipients().toArray(new String[0]));
            mimeMessageHelper.setFrom(emailProperties.getSender());
            javaMailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new EmailException(e);
        }

    }

    private String processTemplate (Message message) {
        try {
            Template template = freeMarkerConfig.getTemplate(message.getBody());

            return FreeMarkerTemplateUtils.processTemplateIntoString(template, message.getVariables());

        } catch (Exception e) {
            throw new EmailException(e);
        }
    }
}
