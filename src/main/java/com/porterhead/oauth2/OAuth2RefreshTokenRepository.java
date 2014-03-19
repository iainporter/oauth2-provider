package com.porterhead.oauth2;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 23/05/2013
 */
public interface OAuth2RefreshTokenRepository extends MongoRepository<OAuth2AuthenticationRefreshToken, String> {

    public OAuth2AuthenticationRefreshToken findByTokenId(String tokenId);
}
