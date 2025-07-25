package com.ms.security;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.AntPathMatcher;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Configuration
public class SecurityConfiguration
{
        private final RSAPublicKey publicKey;
        private final RSAPrivateKey privateKey;

        @Value("${api.endpoint.base-url}")
        private String baseUrl;

        private final CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;

        private final CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint;

        private final CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler;

        public SecurityConfiguration(CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint, CustomBearerTokenAuthenticationEntryPoint customBearerTokenAuthenticationEntryPoint, CustomBearerTokenAccessDeniedHandler customBearerTokenAccessDeniedHandler) throws NoSuchAlgorithmException {
            this.customBasicAuthenticationEntryPoint = customBasicAuthenticationEntryPoint;
            this.customBearerTokenAuthenticationEntryPoint = customBearerTokenAuthenticationEntryPoint;
            this.customBearerTokenAccessDeniedHandler = customBearerTokenAccessDeniedHandler;

            // generate a public / private key pair
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048);//The generated key will have the size of 2048 bits
                KeyPair keyPair = keyPairGenerator.generateKeyPair();

                publicKey = (RSAPublicKey) keyPair.getPublic();
                privateKey = (RSAPrivateKey) keyPair.getPrivate();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
        {
                return http
                        .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                                .requestMatchers(HttpMethod.GET,this.baseUrl + "/artifacts/**").permitAll()
                                .requestMatchers(HttpMethod.POST,this.baseUrl + "/artifacts/**").permitAll()
                                .requestMatchers(HttpMethod.GET,"/actuator/**").permitAll()
                                .requestMatchers(HttpMethod.GET,this.baseUrl + "/users/**").hasAuthority("ROLE_admin")
                                .requestMatchers(HttpMethod.POST,this.baseUrl + "/users").hasAuthority("ROLE_admin")
                                .requestMatchers(HttpMethod.PUT,this.baseUrl + "/users/**").hasAuthority("ROLE_admin")
                                .requestMatchers(HttpMethod.DELETE,this.baseUrl + "/users/**").hasAuthority("ROLE_admin")
                                .requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
                                .anyRequest().authenticated()
                        )
                        .headers(headers -> headers.frameOptions().disable())
                        .csrf(csrf -> csrf.disable())
                        .cors(Customizer.withDefaults())
                        .httpBasic(httpBasic -> httpBasic.authenticationEntryPoint(customBasicAuthenticationEntryPoint))
                        .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt().and()
                                .authenticationEntryPoint(customBearerTokenAuthenticationEntryPoint)
                                .accessDeniedHandler(customBearerTokenAccessDeniedHandler))
                        .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .build();
        }

        @Bean
        public PasswordEncoder passwordEncoder()
        {
            return new BCryptPasswordEncoder(12);
        }

        @Bean
        public JwtEncoder jwtEncoder()
        {
                JWK jwk = new RSAKey.Builder(publicKey).privateKey(privateKey).build();
                JWKSource<SecurityContext> jwkSet= new ImmutableJWKSet<>(new JWKSet(jwk));
                return new NimbusJwtEncoder(jwkSet);
        }

        @Bean
        public JwtDecoder jwtDecoder()
        {
                return NimbusJwtDecoder.withPublicKey(publicKey).build();
        }

        @Bean
        public JwtAuthenticationConverter jwtAuthenticationConverter()
        {
                JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
                jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities");

                jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

                JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
                jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
                return jwtAuthenticationConverter;
        }
}
