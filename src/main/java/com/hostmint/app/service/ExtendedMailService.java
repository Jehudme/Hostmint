package com.hostmint.app.service;

import com.hostmint.app.aop.audit.Audit;
import com.hostmint.app.domain.User;
import com.hostmint.app.domain.enumeration.LogLevel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import tech.jhipster.config.JHipsterProperties;

@Service
@Primary
public class ExtendedMailService extends MailService {

    private final Logger log = LoggerFactory.getLogger(ExtendedMailService.class);

    private final JHipsterProperties jHipsterProperties;
    private final JavaMailSender javaMailSender;
    private final MessageSource messageSource;
    private final SpringTemplateEngine templateEngine;

    public ExtendedMailService(
        JHipsterProperties jHipsterProperties,
        JavaMailSender javaMailSender,
        MessageSource messageSource,
        SpringTemplateEngine templateEngine
    ) {
        super(jHipsterProperties, javaMailSender, messageSource, templateEngine);
        this.jHipsterProperties = jHipsterProperties;
        this.javaMailSender = javaMailSender;
        this.messageSource = messageSource;
        this.templateEngine = templateEngine;
    }

    @Override
    @Async
    @Audit(action = "EMAIL_SENT", entity = "#to", message = "'Subject: ' + #subject")
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        this.sendEmailWithException(to, subject, content, isMultipart, isHtml);
    }

    @Override
    @Async
    @Audit(action = "EMAIL_ACTIVATION_SENT", entity = "#user.login", message = "'To: ' + #user.email")
    public void sendActivationEmail(User user) {
        sendEmailFromTemplateWithException(user, "mail/activationEmail", "email.activation.title");
    }

    @Override
    @Async
    @Audit(action = "EMAIL_CREATION_SENT", entity = "#user.login", message = "'To: ' + #user.email")
    public void sendCreationEmail(User user) {
        sendEmailFromTemplateWithException(user, "mail/creationEmail", "email.activation.title");
    }

    @Override
    @Async
    @Audit(action = "EMAIL_PASSWORD_RESET_SENT", entity = "#user.login", level = LogLevel.WARN, message = "'To: ' + #user.email")
    public void sendPasswordResetMail(User user) {
        sendEmailFromTemplateWithException(user, "mail/passwordResetEmail", "email.reset.title");
    }

    /**
     * REPLACEMENT LOGIC:
     * This method does NOT swallow exceptions. If the mail server fails,
     * the exception bubbles up to the @Audit aspect for logging.
     */
    private void sendEmailWithException(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(jHipsterProperties.getMail().getFrom());
            message.setSubject(subject);
            message.setText(content, isHtml);

            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (MailException | MessagingException e) {
            log.error("Email failed for '{}': {}", to, e.getMessage());
            // WE THROW THE EXCEPTION HERE so @Audit knows it failed!
            throw new RuntimeException("Mail delivery failed", e);
        }
    }

    private void sendEmailFromTemplateWithException(User user, String templateName, String titleKey) {
        if (user.getEmail() == null) {
            throw new RuntimeException("Cannot send email: User has no email address");
        }
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable("user", user);
        context.setVariable("baseUrl", jHipsterProperties.getMail().getBaseUrl());

        String content = templateEngine.process(templateName, context);
        String subject = messageSource.getMessage(titleKey, null, locale);

        this.sendEmailWithException(user.getEmail(), subject, content, false, true);
    }
}
