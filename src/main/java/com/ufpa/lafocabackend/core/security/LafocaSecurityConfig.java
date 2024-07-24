package com.ufpa.lafocabackend.core.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableMethodSecurity()
@EnableWebSecurity
public class LafocaSecurityConfig {

    @Bean
    public SecurityFilterChain ResourceServerSecurityFilterChain (HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/check-token").permitAll()
                                .requestMatchers(HttpMethod.GET, "/members/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/projects/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/tccs/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/functions-member/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/articles/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/skills/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/year-classes/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/info/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/lines-of-research/**").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/users/reset-password").permitAll()
                                .requestMatchers(HttpMethod.GET, "/news/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/record-count/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/host-check/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/hello-world").permitAll()
                                .anyRequest().authenticated()
                )
                        .csrf(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer( oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .sessionManagement(session -> session.sessionCreationPolicy((SessionCreationPolicy.STATELESS)));

        return httpSecurity.build();
    }

    /**
     * Um converter customizado para ler authorities do Authorization Server.
     * O Resource Server precisa saber lidar com as custom claims do Auth, para isso
     * é necessário converte-las em um tipo que o mesmo consiga identificar e garantir o acesso
     * aos endpoints e métodos protegidos.
     */
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> authorities = jwt.getClaimAsStringList("authorities");

            if (authorities == null) {
                return new ArrayList<>(Collections.emptyList());
            }

            JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
            Collection<GrantedAuthority> grantedAuthorities = authoritiesConverter.convert(jwt);

            grantedAuthorities.addAll(authorities
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList());

            return grantedAuthorities;
        });

        return converter;
    }

    @Bean
    public JwtEncoder jwtEncoder(JwtKeyStoreProperties jwtKeyStoreProperties) throws Exception {

        final char[] keyStorePass = jwtKeyStoreProperties.getPassword().toCharArray();
        final String keypairAlias = jwtKeyStoreProperties.getKeypairAlias();

        final Resource jksLocation = jwtKeyStoreProperties.getJksLocation();
        final InputStream inputStream = jksLocation.getInputStream();
        final KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(inputStream, keyStorePass);

        final RSAKey rsaKey = RSAKey.load(keyStore, keypairAlias, keyStorePass);
        ImmutableJWKSet<SecurityContext> jwkSet = new ImmutableJWKSet<>(new JWKSet(rsaKey));

        return new NimbusJwtEncoder(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder (JwtKeyStoreProperties jwtKeyStoreProperties) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, JOSEException {
        final char[] keyStorePass = jwtKeyStoreProperties.getPassword().toCharArray();
        final String keypairAlias = jwtKeyStoreProperties.getKeypairAlias();

        final Resource jksLocation = jwtKeyStoreProperties.getJksLocation();
        final InputStream inputStream = jksLocation.getInputStream();
        final KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(inputStream, keyStorePass);

        final RSAKey rsaKey = RSAKey.load(keyStore, keypairAlias, keyStorePass);
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();
    }

}