package com.ufpa.lafocabackend.core.security;

import com.ufpa.lafocabackend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class LafocaSecurity {

    private final UserRepository userRepository;

    public LafocaSecurity(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public String getUserId(){

        Jwt jwt = (Jwt) getAuthentication().getPrincipal();

        return jwt.getClaimAsString("user_id");
    }

    public boolean userHimself(String userId){

        return getUserId().equals(userId);
    }
}
