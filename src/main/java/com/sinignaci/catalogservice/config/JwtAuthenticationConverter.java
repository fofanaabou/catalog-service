package com.sinignaci.catalogservice.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtAuthenticationConverter implements Converter<Jwt, JwtAuthenticationToken> {

    public static final String ROLES_KEY = "roles";
    public static final String ROLE_PREFIX = "ROLE_";

    @Override
    public JwtAuthenticationToken convert(@NonNull Jwt source) {
        List<String> realmAccessArray = retrieveRolesFromJwt(source);
        List<GrantedAuthority> authorities = realmAccessArray.stream()
                .map(role -> ROLE_PREFIX + role)
                .map(role -> (GrantedAuthority) () -> role)
                .toList();
        return new JwtAuthenticationToken(source, authorities);
    }

    private static List<String> retrieveRolesFromJwt(Jwt source) {
        return source.getClaim(ROLES_KEY);
    }
}