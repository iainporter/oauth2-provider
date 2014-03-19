package com.porterhead.oauth2;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 22/05/2013
 */
public interface OAuth2AccessTokenRepository extends MongoRepository<OAuth2AuthenticationAccessToken, String> {

    public OAuth2AuthenticationAccessToken findByTokenId(String tokenId);

    public OAuth2AuthenticationAccessToken findByRefreshToken(String refreshToken);

    public OAuth2AuthenticationAccessToken findByAuthenticationId(String authenticationId);

    public List<OAuth2AuthenticationAccessToken> findByUserName(String userName);

    public List<OAuth2AuthenticationAccessToken> findByClientId(String clientId);
}
