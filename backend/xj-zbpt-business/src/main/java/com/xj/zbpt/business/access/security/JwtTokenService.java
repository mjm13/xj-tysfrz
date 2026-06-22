package com.xj.zbpt.business.access.security;

import com.xj.zbpt.business.access.config.JwtProperties;
import com.xj.zbpt.business.access.domain.DataScope;
import com.xj.zbpt.business.access.domain.OperatorContext;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class JwtTokenService {

    private final JwtProperties properties;
    private final SecretKey key;

    public JwtTokenService(JwtProperties properties) {
        this.properties = properties;
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(OperatorContext context) {
        Instant now = Instant.now();
        Instant expiry = now.plus(properties.expirationHours(), ChronoUnit.HOURS);
        return Jwts.builder()
                .subject(context.platformUserId())
                .claim("username", context.username())
                .claim("roles", context.roles())
                .claim("permissions", context.permissions())
                .claim("dataScope", context.dataScope().name())
                .claim("deptCode", context.departmentCode())
                .claim("globalScope", context.isGlobal())
                .claim("scopedDeptCodes", context.scopedDeptCodes())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    public OperatorContext parseToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);
        @SuppressWarnings("unchecked")
        List<String> permissions = claims.get("permissions", List.class);
        @SuppressWarnings("unchecked")
        List<String> scopedDeptCodes = claims.get("scopedDeptCodes", List.class);

        return new OperatorContext(
                claims.getSubject(),
                claims.get("username", String.class),
                roles == null ? Set.of() : Set.copyOf(roles),
                permissions == null ? Set.of() : Set.copyOf(permissions),
                DataScope.valueOf(claims.get("dataScope", String.class)),
                claims.get("deptCode", String.class),
                scopedDeptCodes == null ? Set.of() : Set.copyOf(scopedDeptCodes),
                Boolean.TRUE.equals(claims.get("globalScope", Boolean.class))
        );
    }
}
