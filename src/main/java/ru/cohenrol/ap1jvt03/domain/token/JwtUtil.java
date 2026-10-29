package ru.cohenrol.ap1jvt03.domain.token;

import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import ru.cohenrol.ap1jvt03.domain.enums.Role;
import ru.cohenrol.ap1jvt03.domain.model.Authority;

import java.util.List;
import java.util.UUID;

@Component
public class JwtUtil {
    public JwtAuthentication createAuthentication(Claims claims) {
        UUID uuid = UUID.fromString(claims.get("uuid", String.class));

        List<String> roleNames = claims.get("roles", List.class);
        List<Authority> authorities = List.of();
        if (roleNames != null) {
            authorities = roleNames.stream()
                .map(r -> new Authority(Role.valueOf(r)))
                .toList();
        }

        return new JwtAuthentication(uuid, authorities);
    }
}