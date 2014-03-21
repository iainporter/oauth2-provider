package com.porterhead.configuration;

import com.porterhead.mail.InMemoryJavaMailSender;
import com.porterhead.mail.MailSenderService;
import com.porterhead.mail.MailSenderServiceImpl;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 14/05/2013
 */
@Configuration
@Profile({"default", "test", "dev", "stage", "local"})
public class InMemoryMailServiceConfiguration {

    @Bean
    VelocityEngine velocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "class");
        velocityEngine.setProperty("class.resource.loader.class","org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return velocityEngine;
    }

    @Bean()
    JavaMailSender mailSender() {
         return new InMemoryJavaMailSender();
    }

    @Bean
    MailSenderService mailSenderService() {
        return new MailSenderServiceImpl(mailSender(), velocityEngine());
    }
}
