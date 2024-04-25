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

        return getSession(jwt);
    }

    private static Session getSession(Jwt jwt) {
        String tokenValue = jwt.getTokenValue();
        String userId = jwt.getClaim("user_id");
        String userName = jwt.getClaim("full_name");
        String photo = jwt.getClaim("photo");
        String userEmail = jwt.getSubject();
        String issuer = jwt.getClaim("iss");
        String created_at = String.valueOf(Objects.requireNonNull(jwt.getIssuedAt()).toEpochMilli());
        String exp_at = String.valueOf(Objects.requireNonNull(jwt.getExpiresAt()).toEpochMilli());
        String authorities = jwt.getClaimAsString("authorities");

        return Session.builder()
                .token(tokenValue)
                .user_id(userId)
                .full_name(userName)
                .user_email(userEmail)
                .photo(photo)
                .issuer(issuer)
                .exp_at(exp_at)
                .create_at(created_at)
                .authorities(authorities)
                .build();
    }

    public Session encodeJwtToken(User user) {


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
                .claim("photo", user.getUrlPhoto() == null ? "" : user.getUrlPhoto())
                .issuedAt(now)
                .expiresAt(expiresIn)
                .build();
        Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims));

        return getSession(jwt);
    }

    private Collection<GrantedAuthority> getAuthorities(User user) {
        return user.getGroups().stream()
                .flatMap(group -> group.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName().toUpperCase()))
                .collect(Collectors.toSet());
    }

}
