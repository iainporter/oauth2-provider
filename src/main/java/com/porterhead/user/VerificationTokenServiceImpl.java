package com.porterhead.user;

import com.porterhead.mail.EmailServiceTokenModel;
import com.porterhead.mail.MailSenderService;
import com.porterhead.service.BaseService;
import com.porterhead.user.api.LostPasswordRequest;
import com.porterhead.user.api.PasswordRequest;
import com.porterhead.user.exception.*;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.validation.Validator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 13/05/2013
 */
@Service("verificationTokenService")
public class VerificationTokenServiceImpl extends BaseService implements VerificationTokenService {

    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}$");

    private VerificationTokenRepository tokenRepository;

    private MailSenderService mailSenderService;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    @Value("${token.emailVerification.timeToLive.inMinutes}")
    private int emailVerificationTokenExpiryTimeInMinutes;

    @Value("${token.emailRegistration.timeToLive.inMinutes}")
    private int emailRegistrationTokenExpiryTimeInMinutes;

    @Value("${token.lostPassword.timeToLive.inMinutes}")
    private int lostPasswordTokenExpiryTimeInMinutes;

    @Value("${hostName.url}")
    private String hostNameUrl;

    public VerificationTokenServiceImpl(Validator validator) {
        super(validator);
    }

    @Autowired
    public VerificationTokenServiceImpl(UserRepository userRepository, VerificationTokenRepository tokenRepository,
                                        MailSenderService mailSenderService, Validator validator, PasswordEncoder passwordEncoder) {
        this(validator);
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.mailSenderService = mailSenderService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Async
    public VerificationToken sendEmailVerificationToken(String userId) {
        User user = ensureUserIsLoaded(userId);
        return sendEmailVerificationToken(user);
    }

    private VerificationToken sendEmailVerificationToken(User user) {
        VerificationToken token = new VerificationToken(user,
                VerificationTokenType.emailVerification, emailVerificationTokenExpiryTimeInMinutes);
        tokenRepository.save(token);
        mailSenderService.sendVerificationEmail(new EmailServiceTokenModel(user,
                token, hostNameUrl));
        return token;
    }

    @Transactional
    @Async
    public VerificationToken sendEmailRegistrationToken(String userId) {
        User user = ensureUserIsLoaded(userId);
        VerificationToken token = new VerificationToken(user,
                VerificationTokenType.emailRegistration, emailRegistrationTokenExpiryTimeInMinutes);
        tokenRepository.save(token);
        mailSenderService.sendRegistrationEmail(new EmailServiceTokenModel(user,
                token, hostNameUrl));
        return token;
    }

    /**
     * generate token if user found otherwise do nothing
     *
     * @param lostPasswordRequest
     * @return a token or null if user not found
     */
    @Transactional
    @Async
    public VerificationToken sendLostPasswordToken(LostPasswordRequest lostPasswordRequest) {
        validate(lostPasswordRequest);
        VerificationToken token = null;
        User user = userRepository.findByEmailAddress(lostPasswordRequest.getEmailAddress());
        if (user != null) {
            List<VerificationToken> tokens = tokenRepository.findByUserIdAndTokenType(user.getId(), VerificationTokenType.lostPassword);
            token = getActiveToken(tokens);
            if (token == null) {
                token = new VerificationToken(user,
                VerificationTokenType.lostPassword, lostPasswordTokenExpiryTimeInMinutes);
                tokenRepository.save(token);
            }
            mailSenderService.sendLostPasswordEmail(new EmailServiceTokenModel(user, token, hostNameUrl));

        }

        return token;
    }

    @Transactional
    public VerificationToken verify(String base64EncodedToken) {
        VerificationToken token = loadToken(base64EncodedToken);
        User user = userRepository.findOne(token.getUserId());
        if (token.isVerified() || user.isVerified()) {
            throw new AlreadyVerifiedException();
        }
        token.setVerified(true);
        user.setVerified(true);
        userRepository.save(user);
        tokenRepository.save(token);
        return token;
    }

    @Transactional
    public VerificationToken generateEmailVerificationToken(String emailAddress) {
        Assert.notNull(emailAddress);
        User user = userRepository.findByEmailAddress(emailAddress);
        if (user == null) {
            throw new UserNotFoundException();
        }
        if (user.isVerified()) {
            throw new AlreadyVerifiedException();
        }
        //if token still active resend that
        VerificationToken token = getActiveToken(tokenRepository.findByUserIdAndTokenType(user.getId(), VerificationTokenType.emailVerification));
        if (token == null) {
            token = sendEmailVerificationToken(user);
        } else {
            mailSenderService.sendVerificationEmail(new EmailServiceTokenModel(user, token, hostNameUrl));
        }
        return token;
    }

    @Transactional
    public VerificationToken resetPassword(String base64EncodedToken, PasswordRequest passwordRequest) {
        Assert.notNull(base64EncodedToken);
        validate(passwordRequest);
        VerificationToken token = loadToken(base64EncodedToken);
        if (token.isVerified()) {
            throw new AlreadyVerifiedException();
        }
        token.setVerified(true);
        User user = userRepository.findOne(token.getUserId());
        try {
            user.setHashedPassword(passwordEncoder.encode(passwordRequest.getPassword()));
        } catch (Exception e) {
            throw new AuthenticationException();
        }
        //set user to verified if not already and authenticated role
        user.setVerified(true);
        userRepository.save(user);
        tokenRepository.save(token);
        return token;
    }

    private VerificationToken loadToken(String base64EncodedToken) {
        Assert.notNull(base64EncodedToken);
        String rawToken = new String(Base64.decodeBase64(base64EncodedToken.getBytes()));
        VerificationToken token = tokenRepository.findByToken(rawToken);
        if (token == null) {
            throw new TokenNotFoundException();
        }
        if (token.hasExpired()) {
            throw new TokenHasExpiredException();
        }
        return token;
    }


    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    private User ensureUserIsLoaded(String userIdentifier) {
        User user = null;
        if (isValidUuid(userIdentifier)) {
            user = userRepository.findOne(userIdentifier);
        } else {
            user = userRepository.findByEmailAddress(userIdentifier);
        }
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    private VerificationToken getActiveToken(List<VerificationToken> tokens) {
        VerificationToken activeToken = null;
        for (VerificationToken token : tokens) {
            if (!token.hasExpired() && !token.isVerified()) {
                activeToken = token;
                break;
            }
        }
        return activeToken;
    }

    private boolean isValidUuid(String uuid) {
        return UUID_PATTERN.matcher(uuid).matches();
    }

    public void setEmailVerificationTokenExpiryTimeInMinutes(int emailVerificationTokenExpiryTimeInMinutes) {
        this.emailVerificationTokenExpiryTimeInMinutes = emailVerificationTokenExpiryTimeInMinutes;
    }

    public void setEmailRegistrationTokenExpiryTimeInMinutes(int emailRegistrationTokenExpiryTimeInMinutes) {
        this.emailRegistrationTokenExpiryTimeInMinutes = emailRegistrationTokenExpiryTimeInMinutes;
    }

    public void setLostPasswordTokenExpiryTimeInMinutes(int lostPasswordTokenExpiryTimeInMinutes) {
        this.lostPasswordTokenExpiryTimeInMinutes = lostPasswordTokenExpiryTimeInMinutes;
    }

    public void setHostNameUrl(String hostNameUrl) {
        this.hostNameUrl = hostNameUrl;
    }
}
