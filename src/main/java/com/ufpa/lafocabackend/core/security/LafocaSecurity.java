package com.ufpa.lafocabackend.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class LafocaSecurity {

    private Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getUserId(){

        Jwt jwt = (Jwt) getAuthentication().getPrincipal();

        return jwt.getClaimAsString("user_id");
    }
}
