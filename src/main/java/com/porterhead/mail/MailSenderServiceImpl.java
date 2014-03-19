package com.porterhead.mail;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 13/05/2013
 */
@Service("mailSenderService")
public class MailSenderServiceImpl implements MailSenderService {

    private static Logger LOG = LoggerFactory.getLogger(MailSenderServiceImpl.class);

    private final JavaMailSender mailSender;
    private final VelocityEngine velocityEngine;

    @Value("${email.services.emailVerificationSubjectText}")
    private String emailVerificationSubjectText;

    @Value("${email.services.emailRegistrationSubjectText}")
    private String emailRegistrationSubjectText;

    @Value("${email.services.lostPasswordSubjectText}")
    private String lostPasswordSubjectText;

    @Value("${email.services.fromAddress}")
    private String emailFromAddress;

    @Value("${email.services.replyToAddress}")
    private String emailReplyToAddress;

    @Autowired
    public MailSenderServiceImpl(JavaMailSender mailSender, VelocityEngine velocityEngine) {
        this.mailSender = mailSender;
        this.velocityEngine = velocityEngine;
    }


    public EmailServiceTokenModel sendVerificationEmail(final EmailServiceTokenModel emailVerificationModel) {
        Map<String, String> resources = new HashMap<String, String>();
          return sendVerificationEmail(emailVerificationModel, emailVerificationSubjectText,
                  "META-INF/velocity/VerifyEmail.vm", resources);
    }

    public EmailServiceTokenModel sendRegistrationEmail(final EmailServiceTokenModel emailVerificationModel) {
        Map<String, String> resources = new HashMap<String, String>();
          return sendVerificationEmail(emailVerificationModel, emailRegistrationSubjectText,
                  "META-INF/velocity/RegistrationEmail.vm", resources);
    }

    public EmailServiceTokenModel sendLostPasswordEmail(final EmailServiceTokenModel emailServiceTokenModel) {
        Map<String, String> resources = new HashMap<String, String>();
         return sendVerificationEmail(emailServiceTokenModel, lostPasswordSubjectText,
                 "META-INF/velocity/LostPasswordEmail.vm", resources);
    }


    private void addInlineResource(MimeMessageHelper messageHelper, String resourcePath, String resourceIdentifier) throws MessagingException {
        Resource resource = new ClassPathResource(resourcePath);
        messageHelper.addInline(resourceIdentifier, resource);
    }

    private EmailServiceTokenModel sendVerificationEmail(final EmailServiceTokenModel emailVerificationModel, final String emailSubject,
                                                         final String velocityModel, final Map<String, String> resources) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, "UTF-8");
                messageHelper.setTo(emailVerificationModel.getEmailAddress());
                messageHelper.setFrom(emailFromAddress);
                messageHelper.setReplyTo(emailReplyToAddress);
                messageHelper.setSubject(emailSubject);
                Map model = new HashMap();
                model.put("model", emailVerificationModel);
                String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, velocityModel, model);
                messageHelper.setText(new String(text.getBytes(), "UTF-8"), true);
                      for(String resourceIdentifier: resources.keySet()) {
                   addInlineResource(messageHelper, resources.get(resourceIdentifier), resourceIdentifier);
                }
            }
        };
        LOG.debug("Sending {} token to : {}",emailVerificationModel.getTokenType().toString(), emailVerificationModel.getEmailAddress());
        this.mailSender.send(preparator);
        return emailVerificationModel;
    }

    public void setEmailVerificationSubjectText(String emailVerificationSubjectText) {
        this.emailVerificationSubjectText = emailVerificationSubjectText;
    }

    public void setEmailRegistrationSubjectText(String emailRegistrationSubjectText) {
        this.emailRegistrationSubjectText = emailRegistrationSubjectText;
    }

    public void setLostPasswordSubjectText(String lostPasswordSubjectText) {
        this.lostPasswordSubjectText = lostPasswordSubjectText;
    }

    public void setEmailFromAddress(String emailFromAddress) {
        this.emailFromAddress = emailFromAddress;
    }

    public void setEmailReplyToAddress(String emailReplyToAddress) {
        this.emailReplyToAddress = emailReplyToAddress;
    }
}
