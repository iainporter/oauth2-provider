package com.porterhead.user;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 13/05/2013
 */
public interface VerificationTokenRepository extends MongoRepository<VerificationToken, Long> {

    VerificationToken findById(String id);

    VerificationToken findByToken(String token);

    List<VerificationToken> findByUserId(String userId);

    List<VerificationToken> findByUserIdAndTokenType(String userId, VerificationTokenType tokenType);
}
