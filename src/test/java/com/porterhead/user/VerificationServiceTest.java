package com.porterhead.user;

import com.porterhead.mail.EmailServiceTokenModel;
import com.porterhead.mail.MailSenderService;
import com.porterhead.user.api.LostPasswordRequest;
import com.porterhead.user.api.PasswordRequest;
import com.porterhead.user.exception.AlreadyVerifiedException;
import com.porterhead.user.exception.TokenHasExpiredException;
import com.porterhead.user.exception.TokenNotFoundException;
import com.porterhead.user.exception.UserNotFoundException;
import org.apache.commons.codec.binary.Base64;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 13/05/2013
 */
public class VerificationServiceTest {

    private MailSenderService mailSenderService;
    private UserRepository userRepository;
    private VerificationTokenRepository tokenRepository;
    private List<String> tokens;
    private VerificationTokenService verificationTokenService;
    private Validator validator;
    private PasswordEncoder passwordEncoder;

    @Before
    public void setUp() {
        tokens = new ArrayList<String>();

        mailSenderService = new MailSenderService() {
            @Override
            public EmailServiceTokenModel sendVerificationEmail(EmailServiceTokenModel emailServiceTokenModel) {
                tokens.add(emailServiceTokenModel.getToken());
                return emailServiceTokenModel;
            }

            @Override
            public EmailServiceTokenModel sendRegistrationEmail(EmailServiceTokenModel emailServiceTokenModel) {
                tokens.add(emailServiceTokenModel.getToken());
                return emailServiceTokenModel;
            }

            @Override
            public EmailServiceTokenModel sendLostPasswordEmail(EmailServiceTokenModel emailServiceTokenModel) {
                tokens.add(emailServiceTokenModel.getToken());
                return emailServiceTokenModel;
            }
        };
        validator = Validation.buildDefaultValidatorFactory().getValidator();

        userRepository = mock(UserRepository.class);
        tokenRepository = mock(VerificationTokenRepository.class);
        passwordEncoder = new StandardPasswordEncoder("");
        verificationTokenService = new VerificationTokenServiceImpl(userRepository, tokenRepository,
                mailSenderService, validator, passwordEncoder);
        ((VerificationTokenServiceImpl) verificationTokenService).setHostNameUrl("http://localhost:8080");
        ((VerificationTokenServiceImpl) verificationTokenService).setLostPasswordTokenExpiryTimeInMinutes(120);
        ((VerificationTokenServiceImpl) verificationTokenService).setEmailVerificationTokenExpiryTimeInMinutes(120);
        ((VerificationTokenServiceImpl) verificationTokenService).setEmailRegistrationTokenExpiryTimeInMinutes(120);

    }


    @Test
    public void sendLostPasswordToken() {
        User user = generateTestUser();
        when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(user);
        VerificationToken token = verificationTokenService.sendLostPasswordToken(new LostPasswordRequest(user.getEmailAddress()));
        when(tokenRepository.findByUserId(user.getId())).thenReturn(Arrays.asList(token));
        List<VerificationToken> savedTokens = tokenRepository.findByUserId(user.getId());
        assertThat(savedTokens.size(), is(1));
        assertThat(savedTokens.get(0), is(token));
        assertThat(token, is(not(Matchers.<Object>nullValue())));
        assertThat(tokens.size(), is(1));
        String sentToken = tokens.get(0);
        assertThat(sentToken, is(not(nullValue())));
        assertThat(sentToken, is(token.getToken()));
        assertThat(token.getTokenType(), is(VerificationTokenType.lostPassword));

    }

    @Test
    public void sendLostPasswordTokenAgain() {
        User user = generateTestUser();
        when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(user);
        VerificationToken token1 = verificationTokenService.sendLostPasswordToken(new LostPasswordRequest(user.getEmailAddress()));
        when(tokenRepository.findByUserIdAndTokenType(user.getId(), VerificationTokenType.lostPassword)).thenReturn(Arrays.asList(token1));
        VerificationToken token2 = verificationTokenService.sendLostPasswordToken(new LostPasswordRequest(user.getEmailAddress()));
        assertThat(token1.getId(), is(token2.getId()));
        verify(tokenRepository, times(1)).save(any(VerificationToken.class));
        assertThat(tokens.size(), is(2));  //gateway called twice

    }

    @Test
    public void resetPassword() throws Exception {
        User user = generateTestUser();
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(user);
        when(userRepository.findOne(user.getId())).thenReturn(user);
        VerificationToken token = verificationTokenService.sendLostPasswordToken(new LostPasswordRequest(user.getEmailAddress()));
        when(tokenRepository.findByToken(token.getToken())).thenReturn(token);
        String encodedToken = new String(Base64.encodeBase64(token.getToken().getBytes()));
        VerificationToken verifiedToken = verificationTokenService.resetPassword(encodedToken, new PasswordRequest("newpassword"));
        assertThat(verifiedToken.isVerified(), is(true));
        assertTrue(passwordEncoder.matches("newpassword", user.getHashedPassword()));
        verify(tokenRepository, times(2)).save(any(VerificationToken.class));
        //user should also be verified
        assertThat(user.isVerified(), is(true));
    }

    @Test
    public void resetPasswordGetNewToken() {
        User user = generateTestUser();
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findOne(user.getId())).thenReturn(user);
        when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(user);
        VerificationToken token = verificationTokenService.sendLostPasswordToken(new LostPasswordRequest(user.getEmailAddress()));
        when(tokenRepository.findByToken(token.getToken())).thenReturn(token);
        String encodedToken = new String(Base64.encodeBase64(token.getToken().getBytes()));
        verificationTokenService.resetPassword(encodedToken, new PasswordRequest("newpassword"));
        VerificationToken token2 = verificationTokenService.sendLostPasswordToken(new LostPasswordRequest(user.getEmailAddress()));
        assertThat(token2.getToken(), is(not(token.getToken())));
        verify(tokenRepository, times(3)).save(any(VerificationToken.class));

    }

    @Test
    public void sendEmailToken() {
        User user = generateTestUser();
        when(userRepository.findOne(user.getId())).thenReturn(user);
        VerificationToken token = verificationTokenService.sendEmailVerificationToken(user.getId());
        assertThat(tokens.size(), is(1));
        String sentToken = tokens.get(0);
        assertThat(sentToken, is(not(nullValue())));
        assertThat(sentToken, is(token.getToken()));
        assertThat(token.getTokenType(), is(VerificationTokenType.emailVerification));
        verify(tokenRepository, times(1)).save(any(VerificationToken.class));
    }

    @Test
    public void sendRegistrationToken() {
        User user = generateTestUser();
        when(userRepository.findOne(user.getId())).thenReturn(user);
        VerificationToken token = verificationTokenService.sendEmailRegistrationToken(user.getId());
        assertThat(tokens.size(), is(1));
        String sentToken = tokens.get(0);
        assertThat(sentToken, is(not(nullValue())));
        assertThat(sentToken, is(token.getToken()));
        assertThat(token.getTokenType(), is(VerificationTokenType.emailRegistration));
        verify(tokenRepository, times(1)).save(any(VerificationToken.class));
    }

    @Test
    public void verifyValidToken() {
        User user = generateTestUser();
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.findOne(user.getId())).thenReturn(user);
        VerificationToken token = verificationTokenService.sendEmailVerificationToken(user.getId());
        when(tokenRepository.findByToken(token.getToken())).thenReturn(token);
        String encodedToken = new String(Base64.encodeBase64(token.getToken().getBytes()));
        VerificationToken verifiedToken = verificationTokenService.verify(encodedToken);
        assertThat(verifiedToken.isVerified(), is(true));
        assertThat(user.isVerified(), is(true));
        verify(tokenRepository, times(2)).save(any(VerificationToken.class));
    }

    @Test(expected = TokenHasExpiredException.class)
    public void tokenHasExpired() {
        User user = generateTestUser();
        VerificationToken token = mock(VerificationToken.class);
        when(token.getUserId()).thenReturn(user.getId());
        when(token.hasExpired()).thenReturn(true);
        when(token.getToken()).thenReturn(UUID.randomUUID().toString());
        when(userRepository.save(user)).thenReturn(user);
        when(tokenRepository.findByToken(token.getToken())).thenReturn(token);
        String encodedToken = new String(Base64.encodeBase64(token.getToken().getBytes()));
        verificationTokenService.verify(encodedToken);
    }

    @Test(expected = AlreadyVerifiedException.class)
    public void tokenAlreadyVerified() {
        User user = generateTestUser();
        VerificationToken token = mock(VerificationToken.class);
        when(token.getUserId()).thenReturn(user.getId());
        when(token.hasExpired()).thenReturn(false);
        when(token.isVerified()).thenReturn(true);
        when(token.getToken()).thenReturn(UUID.randomUUID().toString());
        when(userRepository.save(user)).thenReturn(user);
        when(tokenRepository.findByToken(token.getToken())).thenReturn(token);
        String encodedToken = new String(Base64.encodeBase64(token.getToken().getBytes()));
        verificationTokenService.verify(encodedToken);
    }

    @Test(expected = AlreadyVerifiedException.class)
    public void userAlreadyVerified() {
        User user = generateTestUser();
        user.setVerified(true);
        VerificationToken token = mock(VerificationToken.class);
        when(token.getUserId()).thenReturn(user.getId());
        when(token.hasExpired()).thenReturn(false);
        when(token.isVerified()).thenReturn(false);
        when(token.getToken()).thenReturn(UUID.randomUUID().toString());
        when(userRepository.save(user)).thenReturn(user);
        when(tokenRepository.findByToken(token.getToken())).thenReturn(token);
        when(userRepository.findOne(user.getId())).thenReturn(user);
        String encodedToken = new String(Base64.encodeBase64(token.getToken().getBytes()));
        verificationTokenService.verify(encodedToken);
    }

    @Test(expected = TokenNotFoundException.class)
    public void tokenNotFound() {
        VerificationToken token = new VerificationToken(new User(), VerificationTokenType.emailVerification, 120);
        when(tokenRepository.findByToken(token.getToken())).thenReturn(null);
        String encodedToken = new String(Base64.encodeBase64(token.getToken().getBytes()));
        verificationTokenService.verify(encodedToken);
    }

    @Test
    public void generateEmailToken() {
        User user = generateTestUser();
        when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        VerificationToken token = verificationTokenService.generateEmailVerificationToken(user.getEmailAddress());
        assertThat(token, is(not(Matchers.<Object>nullValue())));
        assertThat(tokens.size(), is(1));
        String sentToken = tokens.get(0);
        assertThat(sentToken, is(not(nullValue())));
        UUID.fromString(sentToken);
        assertThat(sentToken, is(token.getToken()));
        verify(tokenRepository, times(1)).save(any(VerificationToken.class));
    }

    private User generateTestUser() {
        User user = new User();
        user.setEmailAddress("test@example.com");
        return user;
    }

    @Test
    public void generateEmailTokenAlreadyActive() {
        User user = generateTestUser();
        when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        VerificationToken token = verificationTokenService.generateEmailVerificationToken(user.getEmailAddress());
        when(tokenRepository.findByUserIdAndTokenType(user.getId(), VerificationTokenType.emailVerification)).thenReturn(Arrays.asList(token));
        //request it again
        verificationTokenService.generateEmailVerificationToken(user.getEmailAddress());
        verify(tokenRepository, times(1)).save(any(VerificationToken.class));
        assertThat(tokens.size(), is(2)); //gateway invoked twice
    }

    @Test
    public void generateEmailTokenAfterExpired() {
        User user = generateTestUser();
        VerificationToken token = mock(VerificationToken.class);
        when(token.hasExpired()).thenReturn(true);
        when(token.getTokenType()).thenReturn(VerificationTokenType.emailVerification);
        when(token.getUserId()).thenReturn(user.getId());
        when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        VerificationToken generatedToken = verificationTokenService.generateEmailVerificationToken(user.getEmailAddress());
        assertThat(tokens.size(), is(1)); //gateway invoked once, as first token was manually added
        verify(tokenRepository, times(1)).save(any(VerificationToken.class));
    }

    @Test(expected = UserNotFoundException.class)
    public void emailAddressNotFound() {
        verificationTokenService.generateEmailVerificationToken("test@example.com");
    }

    @Test(expected = AlreadyVerifiedException.class)
    public void generateEmailTokenAlreadyVerified() {
        User user = new User();
        user.setEmailAddress("test@example.com");
        user.setVerified(true);
        VerificationToken token = mock(VerificationToken.class);
        when(userRepository.findByEmailAddress(user.getEmailAddress())).thenReturn(user);
        verificationTokenService.generateEmailVerificationToken(user.getEmailAddress());
    }

}
