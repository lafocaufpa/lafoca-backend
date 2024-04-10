package com.ufpa.lafocabackend.core.security.authorizationserver.passowordFlow;

import org.springframework.security.oauth2.core.AuthorizationGrantType;

public class AuthorizationGrantTypePassword {
    public static final AuthorizationGrantType GRANT_PASSWORD =
        new AuthorizationGrantType("grant_password");
}