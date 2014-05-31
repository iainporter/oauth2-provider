package com.porterhead.oauth2.mongodb;

import com.porterhead.user.User;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.DefaultAuthorizationRequest;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.*;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 23/05/2013
 */
//Hackery to deserialize back into an OAuth2Authentication Object made necessary because Spring Mongo can't map clientAuthentication to authorizationRequest
@ReadingConverter
public class OAuth2AuthenticationReadConverter implements Converter<DBObject, OAuth2Authentication> {

    @Override
    public OAuth2Authentication convert(DBObject source) {
        DBObject clientAuthentication = (DBObject)source.get("clientAuthentication");
        Map<String, String> authParams = (Map)clientAuthentication.get("authorizationParameters");
        DefaultAuthorizationRequest authorizationRequest = new DefaultAuthorizationRequest(authParams,
			(Map)clientAuthentication.get("approvalParameters"), (String)authParams.get("client_id"), (Collection)clientAuthentication.get("scope"));
        authorizationRequest.setApproved((Boolean)clientAuthentication.get("approved"));
        DBObject userAuthorization = (DBObject)source.get("userAuthentication");
        Object principal = getPrincipalObject(userAuthorization.get("principal"));
        Authentication userAuthentication = new UsernamePasswordAuthenticationToken(principal,
                (String)userAuthorization.get("credentials"), getAuthorities((List) userAuthorization.get("authorities")));
        OAuth2Authentication authentication = new OAuth2Authentication(authorizationRequest,
                userAuthentication );
        return authentication;
    }

    private Object getPrincipalObject(Object principal) {
        if(principal instanceof DBObject) {
            DBObject principalDBObject = (DBObject)principal;
            User user = new User(principalDBObject);
            return user;
        } else {
            return principal;
        }
    }

    private Collection<GrantedAuthority> getAuthorities(List<Map<String, String>> authorities) {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>(authorities.size());
        for(Map<String, String> authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.get("role")));
        }
        return grantedAuthorities;
    }

}
