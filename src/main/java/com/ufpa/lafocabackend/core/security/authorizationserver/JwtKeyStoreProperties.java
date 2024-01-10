package com.ufpa.lafocabackend.core.security.authorizationserver;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("lafoca.jwt.keystore")
@Getter
@Setter
public class JwtKeyStoreProperties {

    private Resource jksLocation;
    
    private String password;
    
    private String keypairAlias;

}