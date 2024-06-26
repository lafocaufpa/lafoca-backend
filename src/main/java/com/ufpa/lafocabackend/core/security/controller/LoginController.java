package com.ufpa.lafocabackend.core.security.controller;

import com.ufpa.lafocabackend.core.security.dto.JwtUtil;
import com.ufpa.lafocabackend.core.security.dto.LoginRequest;
import com.ufpa.lafocabackend.core.security.dto.Session;
import com.ufpa.lafocabackend.core.security.dto.Token;
import com.ufpa.lafocabackend.core.utils.LafocaCacheUtil;
import com.ufpa.lafocabackend.domain.enums.ErrorMessage;
import com.ufpa.lafocabackend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginController(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<Session> login(@RequestBody LoginRequest loginRequest) {

        var user = userRepository.findByEmail(loginRequest.email());

        if (user.isEmpty() || !(user.get().isLoginCorrect(loginRequest.password(), passwordEncoder))) {
            throw new BadCredentialsException(ErrorMessage.ACESSO_NEGADO.get());
        }

        Session session = jwtUtil.encodeJwtToken(user.get());

        return LafocaCacheUtil.createCachedResponseLoginSession(session);
    }

    @PostMapping("/check-token")
    public ResponseEntity<Session> decodeToken(@RequestBody Token token) {

        Session session = jwtUtil.decodeJwtToken(token.token());

        return LafocaCacheUtil.createCachedResponseLoginSession(session);
    }

}
