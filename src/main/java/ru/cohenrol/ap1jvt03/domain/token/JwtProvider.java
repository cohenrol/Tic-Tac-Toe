package ru.cohenrol.ap1jvt03.domain.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.cohenrol.ap1jvt03.datasource.model.UserEntity;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.*;

@Component
public class JwtProvider {

    private final PrivateKey privateKey;

    @Getter
    private final PublicKey publicKey;

    @Value("${app.jwt.users.expiration-ms}")
    private long usersJwtExpirationMs;

    @Value("${app.jwt.users.refresh-expiration-ms}")
    private long usersRefreshExpirationMs;

    @Value("${app.jwt.common.key-id}")
    private String jwtKeyId;

    @Value("${app.jwt.common.issuer}")
    private String issuer;

    @Value("${app.jwt.users.access-key-audience}")
    private List<String> usersAccessKeyAudience;

    public JwtProvider(
        @Value("${app.jwt.common.keystore.location}") Resource storeLocation,
        @Value("${app.jwt.common.keystore.password}") String storePassword,
        @Value("${app.jwt.common.keystore.alias}") String keyAlias
    ) {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (InputStream is = storeLocation.getInputStream()) {
                keyStore.load(is, storePassword.toCharArray());
            }
            this.privateKey = (PrivateKey) keyStore.getKey(keyAlias, storePassword.toCharArray());
            this.publicKey = keyStore.getCertificate(keyAlias).getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки KeyStore", e);
        }
    }

    public String generateAccessToken(UserEntity user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + usersJwtExpirationMs);

        return Jwts.builder()
            .header().keyId(jwtKeyId).and()
            .issuer(issuer)
            .audience().add(usersAccessKeyAudience).and()
            .subject(user.getUuid().toString())
            .claims(Map.of(
                "uuid", user.getUuid().toString(),
                "roles", user.getRoles().stream().map(Enum::name).toList(),
                "type", "access"
            ))
            .issuedAt(now)
            .expiration(expiry)
            .signWith(privateKey, Jwts.SIG.ES256)
            .compact();
    }

    public String generateRefreshToken(UserEntity user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + usersRefreshExpirationMs);

        return Jwts.builder()
            .header().keyId(jwtKeyId).and()
            .issuer(issuer)
            .audience().add(usersAccessKeyAudience).and()
            .subject(user.getUuid().toString())
            .claims(Map.of(
                "uuid", user.getUuid().toString(),
                "type", "refresh"
            ))
            .issuedAt(now)
            .expiration(expiry)
            .signWith(privateKey, Jwts.SIG.ES256)
            .compact();
    }

    public boolean validateAccessToken(String token) {
        try {
            Claims c = getClaims(token);
            System.out.println(c);
            if (!"access".equals(c.get("type", String.class)))
                return false;
            if (c.get("uuid") == null)
                return false;
            if (isExpired(c))
                return false;
            if (!hasIssuer(c))
                return false;
            if (!hasAudience(c))
                return false;

            List<?> roles = c.get("roles", List.class);
            return roles != null && roles.contains("USER");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            Claims c = getClaims(token);
            if (!"refresh".equals(c.get("type", String.class))) return false;
            if (c.get("uuid") == null) return false;
            if (isExpired(c)) return false;
            if (!hasIssuer(c)) return false;
            if (!hasAudience(c)) return false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(this.publicKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private boolean isExpired(Claims c) {
        Date exp = c.getExpiration();
        return exp == null || exp.before(new Date());
    }

    private boolean hasIssuer(Claims c) {
        return issuer != null && issuer.equals(c.getIssuer());
    }

    private boolean hasAudience(Claims c) {
        Set<String> aud = c.getAudience();
        return aud != null && !Collections.disjoint(aud, usersAccessKeyAudience);
    }
}