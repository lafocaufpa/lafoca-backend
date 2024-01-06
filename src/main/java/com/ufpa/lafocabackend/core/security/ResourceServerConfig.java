package com.ufpa.lafocabackend.core.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@EnableWebSecurity
public class ResourceServerConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(HttpMethod .POST, "/news/**").hasAuthority("ESCREVER_NOTICIAS")
                .antMatchers(HttpMethod.DELETE, "/news/**").hasAuthority("DELETAR_NOTICIAS")
                .antMatchers(HttpMethod.GET, "/news/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .cors().and()
                .oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter());
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter () {
        var jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter( jwt -> {
            List<String> authorities = jwt.getClaimAsStringList("authorities");

            if(authorities == null){
                authorities = Collections.emptyList();
            }

            return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        });

        return jwtAuthenticationConverter;
    }

}