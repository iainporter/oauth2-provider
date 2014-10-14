package com.porterhead.security;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.annotation.Jsr250SecurityConfig;
import org.springframework.security.access.annotation.Jsr250Voter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

/**
 * Created by iainporter on 14/10/2014.
 */
public class HierarchicalJsr250Voter extends Jsr250Voter {

    private RoleHierarchy roleHierarchy = null;

    public HierarchicalJsr250Voter(RoleHierarchy roleHierarchy) {
        Assert.notNull(roleHierarchy, "RoleHierarchy must not be null");
        this.roleHierarchy = roleHierarchy;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> definition) {
        boolean jsr250AttributeFound = false;

        for (ConfigAttribute attribute : definition) {
            if (Jsr250SecurityConfig.PERMIT_ALL_ATTRIBUTE.equals(attribute)) {
                return ACCESS_GRANTED;
            }

            if (Jsr250SecurityConfig.DENY_ALL_ATTRIBUTE.equals(attribute)) {
                return ACCESS_DENIED;
            }

            if (supports(attribute)) {
                jsr250AttributeFound = true;
                // Attempt to find a matching granted authority
                for (GrantedAuthority authority : extractAuthorities(authentication)) {
                    if (attribute.getAttribute().equals(authority.getAuthority())) {
                        return ACCESS_GRANTED;
                    }
                }
            }
        }

        return jsr250AttributeFound ? ACCESS_DENIED : ACCESS_ABSTAIN;
    }

    Collection<? extends GrantedAuthority> extractAuthorities(Authentication authentication) {
        return roleHierarchy.getReachableGrantedAuthorities(authentication.getAuthorities());
    }
}
