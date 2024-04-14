package com.ufpa.lafocabackend.core.security.controller;

import com.ufpa.lafocabackend.core.security.dto.LoginRequest;
import com.ufpa.lafocabackend.core.security.dto.LoginResponse;
import com.ufpa.lafocabackend.domain.model.User;
import com.ufpa.lafocabackend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;

    public LoginController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {

        var user = userRepository.findByEmail(loginRequest.username());

        if(user.isEmpty() || user.get().isLoginCorrect(loginRequest, passwordEncoder)){
            throw new BadCredentialsException("Invalid username or password!");
        }

        var now = Instant.now();
        var expiresIn = 300L;

        Collection<GrantedAuthority> authorities = getAuthorities(user.get());
        Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        var claims = JwtClaimsSet.builder()
                .issuer("lafoca-backend")
                .subject(user.get().getEmail())
                .claim("full_name", user.get().getName())
                .claim("user_id", user.get().getUserId())
                .claim("authorities", roles)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .build();
        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return ResponseEntity.ok(new LoginResponse(jwtValue));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestBody LoginRequest loginRequest) {

        var user = userRepository.findByEmail(loginRequest.username());

        if(user.isEmpty() || user.get().isLoginCorrect(loginRequest, passwordEncoder)){
            throw new BadCredentialsException("Invalid username or password!");
        }

        var now = Instant.now();
        var expiresIn = 300L;

        Collection<GrantedAuthority> authorities = getAuthorities(user.get());
        Set<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        var claims = JwtClaimsSet.builder()
                .issuer("lafoca-backend")
                .subject(user.get().getEmail())
                .claim("full_name", user.get().getName())
                .claim("user_id", user.get().getUserId())
                .claim("authorities", roles)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .build();
        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return ResponseEntity.ok(new LoginResponse(jwtValue));
    }

    private Collection<GrantedAuthority> getAuthorities (User user){
        return user.getGroups().stream()
                .flatMap(group -> group.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName().toUpperCase()))
                .collect(Collectors.toSet());
    }
}
