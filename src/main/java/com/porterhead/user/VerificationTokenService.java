package com.porterhead.user;

import com.porterhead.user.api.LostPasswordRequest;
import com.porterhead.user.api.PasswordRequest;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 13/05/2013
 */
public interface VerificationTokenService {

    public VerificationToken sendEmailVerificationToken(String userId);

    public VerificationToken sendEmailRegistrationToken(String userId);

    public VerificationToken sendLostPasswordToken(LostPasswordRequest lostPasswordRequest);

    public VerificationToken verify(String base64EncodedToken);

    public VerificationToken generateEmailVerificationToken(String emailAddress);

    public VerificationToken resetPassword(String base64EncodedToken, PasswordRequest passwordRequest);
}
