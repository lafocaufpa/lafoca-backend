package com.ufpa.lafocabackend.core.security.authorizationserver;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {


    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtKeyStoreProperties jwtKeyStoreProperties;

    private final DataSource dataSource;

    public AuthorizationServerConfig(AuthenticationManager authenticationManager, @Qualifier("jpaUserDetailsService") UserDetailsService userDetailsService, JwtKeyStoreProperties jwtKeyStoreProperties, DataSource dataSource) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtKeyStoreProperties = jwtKeyStoreProperties;
        this.dataSource = dataSource;
    }

    /*
        client se autentica no A.S com o clientId e secret
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);

//        clients.inMemory()
//                .withClient("lafoca-frontend")
//                .secret(passwordEncoder.encode("web123"))
//                .authorizedGrantTypes("password", "refresh_token")
//                .scopes("write", "read")
//                .accessTokenValiditySeconds(6 * 60 * 60)//6h, 1h tem 60m, 1m tem 60s
//                .refreshTokenValiditySeconds(60 * 24 * 60 * 60) // 60d, 1d tem 24h, 1h tem 60m, 1m tem 60s
//                .and()
//                .withClient("check-token")//apenas para Resource Server fazer o check token
//                .secret(passwordEncoder.encode("check-token"))
//                .and()
//                .withClient("faturamento")
//                .secret(passwordEncoder.encode("web123"))
//                .authorizedGrantTypes("client_credentials")
//                .scopes("read")
//                .and()
//                .withClient("lafoca-site")
//                .secret(passwordEncoder.encode("web123"))
//                .authorizedGrantTypes("authorization_code")
//                .scopes("write", "read")
//                .redirectUris("http://lafoca.com")
//                .and()
//                .withClient("lafoca-admin")
//                .authorizedGrantTypes("implicit")
//                .scopes("read", "write")
//                .redirectUris("http://lafoca.com");
    }

    /*
        Somente o fluxo password flow necessita, o bean é criado manualmente
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(new JwtCustomClaimsTokenEnhancer(), jwtAccessTokenConverter()));
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .reuseRefreshTokens(true)
                .tokenGranter(tokenGranter(endpoints))
                .accessTokenConverter(jwtAccessTokenConverter())
                .tokenEnhancer(tokenEnhancerChain);
    }

    @Override
        public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
//        security.checkTokenAccess("isAuthenticated()");
        security.checkTokenAccess("permitAll()")
                .tokenKeyAccess("permitAll()")
                .allowFormAuthenticationForClients();
    }

    private TokenGranter tokenGranter(AuthorizationServerEndpointsConfigurer endpoints) {
        var pkceAuthorizationCodeTokenGranter = new PkceAuthorizationCodeTokenGranter(endpoints.getTokenServices(),
                endpoints.getAuthorizationCodeServices(), endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory());

        var granters = Arrays.asList(
                pkceAuthorizationCodeTokenGranter, endpoints.getTokenGranter());

        return new CompositeTokenGranter(granters);
    }

    @Bean
    public JWKSet jwkSet () {
        RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) getKeyPair().getPublic())
                .keyUse(KeyUse.SIGNATURE)
                .algorithm(JWSAlgorithm.RS256)
                .keyID("lafoca-key-id");
        return new JWKSet(builder.build());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        final JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        //        jwtAccessTokenConverter.setSigningKey("mac-ufpa-lafoca-secret-key-authorization-server");
        jwtAccessTokenConverter.setKeyPair(getKeyPair());

        return jwtAccessTokenConverter;
    }

    private KeyPair getKeyPair() {
        final Resource jksLocation = jwtKeyStoreProperties.getJksLocation();
        String keyStorePass = jwtKeyStoreProperties.getPassword();
        String keyPairAlias = jwtKeyStoreProperties.getKeypairAlias();

        final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(jksLocation, keyStorePass.toCharArray());
        final KeyPair keyPair = keyStoreKeyFactory.getKeyPair(keyPairAlias);
        return keyPair;
    }
}
