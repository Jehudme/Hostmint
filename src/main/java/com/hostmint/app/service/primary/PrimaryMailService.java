package com.hostmint.app.service.primary;

import com.hostmint.app.aop.audit.Auditable;
import com.hostmint.app.service.MailService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring6.SpringTemplateEngine;
import tech.jhipster.config.JHipsterProperties;

@Service
@Primary
public class PrimaryMailService extends MailService {

    public PrimaryMailService(
        JHipsterProperties jHipsterProperties,
        JavaMailSender javaMailSender,
        MessageSource messageSource,
        SpringTemplateEngine templateEngine
    ) {
        super(jHipsterProperties, javaMailSender, messageSource, templateEngine);
    }

    @Override
    @Auditable(action = "EMAIL_SENT", message = "An email was successfully sent by the system.")
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        super.sendEmail(to, subject, content, isMultipart, isHtml);
    }
}
