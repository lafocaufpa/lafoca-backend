package com.ufpa.lafocabackend.core.security.authorizationserver;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class AuthUser extends User {

    private String userId;
    private String fullName;

    public AuthUser( com.ufpa.lafocabackend.domain.model.User user,
                    Collection<? extends GrantedAuthority> authorities) {
        super(user.getEmail(), user.getPassword(), authorities);

        this.fullName = user.getName();
        this.userId = user.getUserId();
    }

}
