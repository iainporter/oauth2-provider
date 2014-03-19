package com.porterhead.user;

import com.porterhead.persistence.BaseEntity;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.UUID;

/**
 * A token that gives the user permission to carry out a specific task once within a determined time period.
 * An example would be a Lost Password token. The user receives the token embedded in a link.
 * The user sends the token back to the server by clicking the link and the action is processed
 *
 * @version 1.0
 * @author: Iain Porter
 * @since 14/05/2013
 */
@Document
public class VerificationToken extends BaseEntity {

    private static final int DEFAULT_EXPIRY_TIME_IN_MINS = 60 * 24; //24 hours

    private final String token;

    private Date expiryDate;

    private VerificationTokenType tokenType;

    private boolean verified;

    String userId;

    public VerificationToken() {
        super();
        this.token = UUID.randomUUID().toString();
        this.expiryDate = calculateExpiryDate(DEFAULT_EXPIRY_TIME_IN_MINS);
    }

    public VerificationToken(User user, VerificationTokenType tokenType, int expirationTimeInMinutes) {
        this();
        this.userId = user.getId();
        this.tokenType = tokenType;
        this.expiryDate = calculateExpiryDate(expirationTimeInMinutes);
    }

    public VerificationTokenType getTokenType() {
        return tokenType;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public String getToken() {
        return token;
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        DateTime now = new DateTime();
        return now.plusMinutes(expiryTimeInMinutes).toDate();
    }

    public boolean hasExpired() {
        DateTime tokenDate = new DateTime(getExpiryDate());
        return tokenDate.isBeforeNow();
    }

}

