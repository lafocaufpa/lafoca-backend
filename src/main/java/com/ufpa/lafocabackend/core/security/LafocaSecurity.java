package com.ufpa.lafocabackend.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class LafocaSecurity {

    public final String ADMIN_LEVEL_1 = "ADMIN_LEVEL_1";
    public final String ADMIN_LEVEL_2 = "ADMIN_LEVEL_2";
    public static final String VIEW_CONTENT = "VIEW_CONTENT";
    public static final String EDIT_CONTENT = "EDIT_CONTENT";
    public static final String DELETE_CONTENT = "DELETE_CONTENT";
    public static final String MANAGE_USERS = "MANAGE_USERS";
    public static final String MANAGE_GROUPS = "MANAGE_GROUPS";
    public static final String FULL_ACCESS = "FULL_ACCESS";

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

    public String getEmail(){

        final Object userAutheticated = getAuthentication().getPrincipal();
        if(userAutheticated.equals("anonymousUser"))
            return "anonymousUser";

        Jwt jwt = (Jwt) userAutheticated;

        return jwt.getClaimAsString("sub");
    }

    public boolean userHimself(String userId, String userEmail){

        boolean equals = getUserId().equals(userId);
        if(!equals) {
            equals = userHimselfWithEmail(userEmail);
        }
        return equals;
    }

    public boolean userHimselfWithEmail(String email){
        return getEmail().equals(email);
    }

}
