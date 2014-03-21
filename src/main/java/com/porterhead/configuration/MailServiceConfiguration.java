package com.porterhead.configuration;

import com.porterhead.mail.MailSenderService;
import com.porterhead.mail.MailSenderServiceImpl;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 14/05/2013
 */
@Configuration
@Profile({"production"})
public class MailServiceConfiguration {

    @Value("${email.services.sender.host}")
    private String mailSenderHost;

    @Value("${email.services.sender.port}")
    private int mailSenderPort;

    @Value("${email.services.sender.username}")
    private String mailSenderUsername;

    @Value("${email.services.sender.password}")
    private String mailSenderPassword;

    @Bean
    VelocityEngine velocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "class");
        velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return velocityEngine;
    }

    @Bean()
    JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailSenderHost);
        mailSender.setPort(mailSenderPort);
        mailSender.setUsername(mailSenderUsername);
        mailSender.setPassword(mailSenderPassword);
        Properties javaMailProperties = new Properties();
        javaMailProperties.setProperty("mail.debug", "false");
        javaMailProperties.setProperty("mail.smtp.auth", "true");
        javaMailProperties.setProperty("mail.smtp.starttls.enable", "true");
        mailSender.setJavaMailProperties(javaMailProperties);
        return mailSender;
    }

    @Bean
    MailSenderService mailSenderService() {
        return new MailSenderServiceImpl(mailSender(), velocityEngine());
    }
}
