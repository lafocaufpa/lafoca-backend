package com.ufpa.lafocabackend.core.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/*
 * Classe para configurar cors para solicitar token do authorization code /oatuh/token
 * 22.19. Testando o fluxo Authorization Code com um client JavaScript
 * Nota sobre o allowCredentials
 * A configuração de allowCredentials como "true", combinada com allowedOrigins como "*",
 * não é suportada pelos navegadores. Recomendamos utilizar a opção allowCredentials como false,
 * caso o allowedOrigins seja "*".
 * Só é possível utilizar a opção allowCredentials como "true", caso sejam especificadas as Origins permitidas.
 *
 */
@Configuration
public class CorsConfig {

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterFilterRegistrationBean() {
        CorsConfiguration config = new CorsConfiguration();

        // Define se as credenciais (cookies, headers de autenticação) são permitidas em requisições CORS.
        config.setAllowCredentials(false);

        // Define quais origens são permitidas para fazer requisições CORS. O "*" permite todas as origens.
        List<String> allowedOrigins = Arrays.asList(
                "https://www.lafoca.com.br",
                "https://lafoca.com.br",
                "http://localhost:3000",
                "http://10.0.0.227:3000",
                "http://192.168.1.7:3000"
        );

        config.setAllowedOrigins(allowedOrigins);

//        config.addAllowedOrigin("*");
        // Define quais métodos HTTP são permitidos em requisições CORS. O "*" permite todos os métodos.
        config.setAllowedMethods(Collections.singletonList("*"));

        // Define quais cabeçalhos são permitidos em requisições CORS. O "*" permite todos os cabeçalhos.
        config.setAllowedHeaders(Collections.singletonList("*"));

        config.setExposedHeaders(Arrays.asList("Content-Disposition"));

        config.setMaxAge(90L);

        // Mapeia a configuração CORS para todos os endpoints da aplicação ("/**").
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        // Cria um bean de registro de filtro e configura a ordem de precedência do filtro.
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

}
