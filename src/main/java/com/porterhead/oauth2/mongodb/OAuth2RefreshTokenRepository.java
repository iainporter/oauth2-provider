package com.porterhead.oauth2.mongodb;

import com.porterhead.oauth2.OAuth2AuthenticationRefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 23/05/2013
 */
@Repository
public interface OAuth2RefreshTokenRepository extends MongoRepository<OAuth2AuthenticationRefreshToken, String> {

    public OAuth2AuthenticationRefreshToken findByTokenId(String tokenId);
}
