package com.ufpa.lafocabackend.core.security.authorizationserver;

import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;

public class JwtCustomClaimsTokenEnhancer implements TokenEnhancer {
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication oAuth2Authentication) {

        if (oAuth2Authentication.getPrincipal() instanceof AuthUser authUser){

            final HashMap<String, Object> claims = new HashMap<>();
            claims.put("full_name", authUser.getFullName());
            claims.put("user_id", authUser.getUserId());
            var accessToken = (DefaultOAuth2AccessToken) oAuth2AccessToken;

            accessToken.setAdditionalInformation(claims);
        }

        return oAuth2AccessToken;
    }
}
