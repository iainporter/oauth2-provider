package com.porterhead.user;

import com.porterhead.mail.EmailServiceTokenModel;
import com.porterhead.mail.InMemoryJavaMailSender;
import com.porterhead.mail.MailSenderServiceImpl;
import com.porterhead.user.api.ApiUser;
import org.apache.commons.codec.binary.Base64;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import javax.validation.Validation;
import javax.validation.Validator;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 13/05/2013
 */
public class MailSenderServiceTest {

    private MailSenderServiceImpl mailSenderService;
    private InMemoryJavaMailSender mailSender;
    protected String emailAddress = "foobar@example.com";
    protected String password = "password";
    protected final String id = "12345";
    protected UserRepository userRepository = Mockito.mock(UserRepository.class);
    protected Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    protected DefaultTokenServices tokenServices;
    protected UserService userService;
    protected TokenStore tokenStore;

    @Value("${hostName.url}")
    String hostNameUrl;

    @Before
    public void setUpServices() {
                reset(userRepository);
        tokenStore = mock(TokenStore.class);
        tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(tokenStore);
        userService = new UserServiceImpl(userRepository, validator, new StandardPasswordEncoder());
        final User user = new User(getUser(), password, Role.ROLE_USER);
        Mockito.when(userRepository.findByEmailAddress(emailAddress)).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return null;
            }
        });

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                return user;
            }
        });
        mailSender = new InMemoryJavaMailSender();
        mailSenderService = new MailSenderServiceImpl(mailSender, getVelocityEngine());
        mailSenderService.setEmailFromAddress("foo@example.com");
        mailSenderService.setEmailReplyToAddress("foo@example.com");
        mailSenderService.setEmailRegistrationSubjectText("Send Registration");
        mailSenderService.setEmailVerificationSubjectText("Send Verification");
        mailSenderService.setLostPasswordSubjectText("Send Lost Password Token");
        mailSender.clear();
    }

    private VelocityEngine getVelocityEngine() {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "class");
        velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        return velocityEngine;
    }


    @Test
    public void sendVerificationEmail() throws Exception {
        User user = new User();
        user.setEmailAddress("foo@example.com");
        VerificationToken token = new VerificationToken(user,
                VerificationTokenType.emailVerification, 120);
        mailSenderService.sendVerificationEmail(new EmailServiceTokenModel(user, token, hostNameUrl));
        assertOnMailResult(user, token);
    }

    @Test
    public void sendRegistrationEmail() throws Exception {
        User user = new User();
        user.setEmailAddress("foo@example.com");
        VerificationToken token = new VerificationToken(user,
                VerificationTokenType.emailRegistration, 120);
        mailSenderService.sendRegistrationEmail(new EmailServiceTokenModel(user, token, hostNameUrl));
        assertOnMailResult(user, token);
    }

    @Test
    public void sendLostPasswordEmail() throws Exception {
        User user = new User();
        user.setEmailAddress("foo@example.com");
        VerificationToken token = new VerificationToken(user,
                VerificationTokenType.lostPassword, 120);
        mailSenderService.sendLostPasswordEmail(new EmailServiceTokenModel(user, token, hostNameUrl));
        assertOnMailResult(user, token);
    }

    private void assertOnMailResult(User user, VerificationToken token) throws MessagingException, IOException {
        List<MimeMessage> messages = mailSender.getMessages();
        assertThat(messages.size(), is(1));
        MimeMessage message = messages.get(0);
        assertThat(message.getAllRecipients()[0].toString(), is((user.getEmailAddress())));
        Multipart multipart = (Multipart)message.getContent();
        String content = (String)multipart.getBodyPart(0).getContent();
        assertThat(content, containsString(new String(Base64.encodeBase64(token.getToken().getBytes()))));
    }

    private  ApiUser getUser() {
        ApiUser user = new ApiUser();
        user.setEmailAddress(emailAddress);
        return user;
    }


}
