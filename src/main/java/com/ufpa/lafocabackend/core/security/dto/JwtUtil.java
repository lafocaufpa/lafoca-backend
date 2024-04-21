package com.ufpa.lafocabackend.core.security.dto;

import com.ufpa.lafocabackend.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${lafoca.jwt.token-expiration-time}")
    private long tokenExpirationTime;

    private final JwtDecoder jwtDecoder;
    private final JwtEncoder jwtEncoder;

    @Autowired
    public JwtUtil(JwtDecoder jwtDecoder, JwtEncoder jwtEncoder) {
        this.jwtDecoder = jwtDecoder;
        this.jwtEncoder = jwtEncoder;
    }

    public Session decodeJwtToken(String token) {

        Jwt jwt = jwtDecoder.decode(token);

        String tokenValue = jwt.getTokenValue();
        String userId = jwt.getClaim("user_id");
        String userName = jwt.getClaim("full_name");
        String userEmail = jwt.getSubject();
        String issuer = jwt.getClaim("iss");
        String created_at = Objects.requireNonNull(jwt.getIssuedAt()).toString();
        String exp_at = Objects.requireNonNull(jwt.getExpiresAt()).toString();
        String authorities = jwt.getClaimAsString("authorities");

        return Session.builder()
                .token(tokenValue)
                .user_id(userId)
                .full_name(userName)
                .user_email(userEmail)
                .issuer(issuer)
                .exp_at(exp_at)
                .create_at(created_at)
                .authorities(authorities)
                .build();
    }

    public Token encodeJwtToken(User user) {


        var now = Instant.now();
        var expiresIn = now.plusSeconds(tokenExpirationTime); //15d * 24h * 60m * 60s = 1296000s/15 dias

        Collection<GrantedAuthority> authorities = getAuthorities(user);
        Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        var claims = JwtClaimsSet.builder()
                .issuer("lafoca-backend")
                .subject(user.getEmail())
                .claim("full_name", user.getName())
                .claim("user_id", user.getUserId())
                .claim("authorities", roles)
                .issuedAt(now)
                .expiresAt(expiresIn)
                .build();
        String jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return new Token(jwtValue);
    }

    private Collection<GrantedAuthority> getAuthorities(User user) {
        return user.getGroups().stream()
                .flatMap(group -> group.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName().toUpperCase()))
                .collect(Collectors.toSet());
    }

}
