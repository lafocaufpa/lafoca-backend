package com.ufpa.lafocabackend.core.security.authorizationserver;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.ufpa.lafocabackend.core.security.authorizationserver.passowordFlow.AuthorizationGrantTypePassword;
import com.ufpa.lafocabackend.core.security.authorizationserver.passowordFlow.GrantPasswordAuthenticationProvider;
import com.ufpa.lafocabackend.core.security.authorizationserver.passowordFlow.OAuth2GrantPasswordAuthenticationConverter;
import com.ufpa.lafocabackend.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.SecurityFilterChain;

import java.io.InputStream;
import java.security.KeyStore;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Configuration
public class AuthorizationServerConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   GrantPasswordAuthenticationProvider grantPasswordAuthenticationProvider,
                                                   DaoAuthenticationProvider daoAuthenticationProvider) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        http
                .getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .tokenEndpoint(tokenEndpoint ->
                        tokenEndpoint
                                .accessTokenRequestConverter(new OAuth2GrantPasswordAuthenticationConverter())
                                .authenticationProvider(grantPasswordAuthenticationProvider)
                                .authenticationProvider(daoAuthenticationProvider)
                );
        return http.formLogin(Customizer.withDefaults()).build();
    }

    @Bean
    public AuthorizationServerSettings providerSettings(LafocaSecurityProperties properties) {
        return AuthorizationServerSettings.builder()
                .issuer(properties.getProviderUrl())
                .build();
    }


    @Bean
    public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder) {
        RegisteredClient demoClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientName("La Foca UFPA")
                .clientId("lafoca-web")
                .clientSecret(passwordEncoder.encode("lafoca123"))
                .redirectUri("http://localhost:8080/auth")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantTypePassword.GRANT_PASSWORD)
                .tokenSettings(
                        TokenSettings.builder()
                                .accessTokenFormat(OAuth2TokenFormat.REFERENCE)
                                .accessTokenTimeToLive(Duration.ofMinutes(300))
                                .refreshTokenTimeToLive(Duration.ofMinutes(600))
                                .authorizationCodeTimeToLive(Duration.ofMinutes(20))
                                .reuseRefreshTokens(false)
                                .build()
                )
                .build();

        return new InMemoryRegisteredClientRepository(demoClient);
    }


//    @Bean
//    public RegisteredClientRepository registeredClientRepository(PasswordEncoder passwordEncoder, JdbcOperations jdbcOperations) {
//
////        RegisteredClient lafocaweb = RegisteredClient
////                .withId("1")
////                .clientId("lafoca-web")
////                .clientSecret(passwordEncoder.encode("lafoca123"))
////                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
////                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
////                .authorizationGrantType(AuthorizationGrantTypePassword.GRANT_PASSWORD)
////                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
////                .scope("READ")
////                .scope("WRITE")
////                .tokenSettings(TokenSettings.builder()
////                        .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED)
////                        .accessTokenTimeToLive(Duration.ofDays(15))
////                        .reuseRefreshTokens(false)
////                        .refreshTokenTimeToLive(Duration.ofDays(30))
////                        .build())
////                .redirectUri("http://127.0.0.1:8080/authorized")
////                .clientSettings(ClientSettings.builder()
////                        .requireAuthorizationConsent(false)
////                        .build())
////                .build();
////
////        final JdbcRegisteredClientRepository clientRepository = new JdbcRegisteredClientRepository(jdbcOperations);
////        clientRepository.save(lafocaweb);
////        return clientRepository;
//        return new JdbcRegisteredClientRepository(jdbcOperations);
//
//    }

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService(JdbcOperations jdbcOperations,
                                                                 RegisteredClientRepository registeredClientRepository) {

        return new JdbcOAuth2AuthorizationService(jdbcOperations, registeredClientRepository);

    }

    @Bean
    public JWKSource<SecurityContext> securityContextJWKSource(JwtKeyStoreProperties jwtKeyStoreProperties) throws Exception {

        final char[] keyStorePass = jwtKeyStoreProperties.getPassword().toCharArray();
        final String keypairAlias = jwtKeyStoreProperties.getKeypairAlias();

        final Resource jksLocation = jwtKeyStoreProperties.getJksLocation();
        final InputStream inputStream = jksLocation.getInputStream();
        final KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(inputStream, keyStorePass);

        final RSAKey rsaKey = RSAKey.load(keyStore, keypairAlias, keyStorePass);

        return new ImmutableJWKSet<>(new JWKSet(rsaKey));
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtEncodingContextOAuth2TokenCustomizer(UserRepository userRepository) {
        return context -> {
            final Authentication authentication = context.getPrincipal();

            //authorization code
            if (authentication.getPrincipal() instanceof User userAuth) {

                com.ufpa.lafocabackend.domain.model.User user = userRepository.findByEmail(userAuth.getUsername()).orElseThrow();

                Set<String> authorities = new HashSet<>();
                for (GrantedAuthority authority : userAuth.getAuthorities()) {
                    authorities.add(authority.getAuthority());
                }

                context.getClaims().claim("user_id", user.getUserId());
                context.getClaims().claim("authorities", authorities);
            }
        };
    }

    /**
     * Implementação de um Fluxo Customizado
     * **/

    @Bean
    public GrantPasswordAuthenticationProvider grantPasswordAuthenticationProvider(
            UserDetailsService userDetailsService, OAuth2TokenGenerator<?> jwtTokenCustomizer,
            OAuth2AuthorizationService authorizationService, PasswordEncoder passwordEncoder
    ) {
        return new GrantPasswordAuthenticationProvider(
                authorizationService, jwtTokenCustomizer, userDetailsService, passwordEncoder
        );
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider (
            PasswordEncoder passwordEncoder, UserDetailsService userDetailsService
    ) {
        DaoAuthenticationProvider  daoAuthenticationProvider  =  new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
        return daoAuthenticationProvider;
    }

//    @Bean
//    public OAuth2AuthorizationService authorizationService() {
//        return new InMemoryOAuth2AuthorizationService();
//    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    }
}
