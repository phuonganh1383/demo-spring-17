package com.example.demoSpring17.configuration;

import com.example.demoSpring17.enums.Role;
import com.nimbusds.jose.JWSAlgorithm;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  private final String[] PUBLIC_ENDPOINTS = {"/api/users", "/auth/login", "/auth/introspect"};

  @Value("${jwt.signerKey}")
  private String SECRET_KEY;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity.authorizeHttpRequests(request ->
            request.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
//                    .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/introspect").permitAll()
//                    .requestMatchers(HttpMethod.POST, "/auth/introspect").permitAll()
//                    .requestMatchers(HttpMethod.GET, "/api/users").hasAnyAuthority("SCOPE_ADMIN")
//                    .requestMatchers(HttpMethod.GET, "/api/users").hasAnyAuthority("ROLE_ADMIN")
                    .requestMatchers(HttpMethod.GET, "/api/users").hasRole(Role.ADMIN.name())
                    .anyRequest().authenticated());

    httpSecurity.oauth2ResourceServer(oauth2 ->
            oauth2.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())));

    httpSecurity.csrf(AbstractHttpConfigurer::disable);

    return httpSecurity.build();
  }

  @Bean
  JwtAuthenticationConverter jwtAuthenticationConverter () {
    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
    return converter;
  }

  @Bean
  JwtDecoder jwtDecoder() {
    SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(), "HS512");
    return NimbusJwtDecoder
            .withSecretKey(keySpec)
            .macAlgorithm(MacAlgorithm.HS512)
            .build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
  }

}
