package com.porterhead.mail;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 13/05/2013
 */
public class InMemoryJavaMailSender implements JavaMailSender {

    List<MimeMessage> messages = new ArrayList<MimeMessage>();

    public MimeMessage createMimeMessage() {
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        return message;
    }

    public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void send(MimeMessage mimeMessage) throws MailException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void send(MimeMessage[] mimeMessages) throws MailException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
                try {
                    MimeMessage mimeMessage = createMimeMessage();
				    mimeMessagePreparator.prepare(mimeMessage);
                    messages.add(mimeMessage);
                } catch(Exception e) {
                    System.out.println("Exception while preparing Mail Message" + e);
                    throw new RuntimeException(e);
                }


    }

    public void send(MimeMessagePreparator[] mimeMessagePreparators) throws MailException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void send(SimpleMailMessage simpleMessage) throws MailException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void send(SimpleMailMessage[] simpleMessages) throws MailException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<MimeMessage> getMessages() {
        return messages;
    }

    public void clear() {
        messages.clear();
    }
}
