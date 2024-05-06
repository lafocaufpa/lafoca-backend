package com.ufpa.lafocabackend.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class LafocaSecurity {

    public final String ADMIN_LEVEL_1 = "ADMIN_LEVEL_1";
    public final String ADMIN_LEVEL_2 = "ADMIN_LEVEL_2";

    private Authentication getAuthentication()  {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getUserId(){

        final Object userAutheticated = getAuthentication().getPrincipal();
        if(userAutheticated.equals("anonymousUser"))
            return "anonymousUser";

        Jwt jwt = (Jwt) userAutheticated;

        return jwt.getClaimAsString("user_id");
    }

    public boolean userHimself(String userId){

        return getUserId().equals(userId);
    }

}
