package io.ola.security.authenticate;

import cn.hutool.core.date.DateUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.ola.common.utils.SpringUtils;
import io.ola.security.constants.SecurityConstants;
import io.ola.security.model.Token;
import io.ola.security.properties.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author yiuman
 * @date 2023/8/9
 */
@Slf4j
public final class JwtUtils {
    public static final JwtProperties JWT_PROPERTIES = SpringUtils.getBean(JwtProperties.class);

    private JwtUtils() {
    }

    public static Token generateToken(String principal, Map<String, Object> claims, boolean buildRefreshToken) {
        long expires = JWT_PROPERTIES.getExpiresInSeconds() * 1000;
        claims.put(JWT_PROPERTIES.getIdentityKey(), principal);
        String token = Jwts.builder()
                .setSubject(principal)
                .setClaims(claims)
                .signWith(signKey(), JWT_PROPERTIES.getAlgorithm())
                .setExpiration(DateUtil.date(System.currentTimeMillis() + expires))
                .compact();
        String refreshToken = null;
        if (buildRefreshToken) {
            Map<String, Object> refreshTokenClaims = new HashMap<>(claims);
            refreshTokenClaims.put(SecurityConstants.GRANT_TYPE, "refreshToken");
            refreshToken = Jwts.builder()
                    .setSubject(principal)
                    .setClaims(refreshTokenClaims)
                    .signWith(signKey(), JWT_PROPERTIES.getAlgorithm())
                    .setExpiration(DateUtil.date(System.currentTimeMillis() + JWT_PROPERTIES.getRefreshToKenExpiresInSeconds() * 1000))
                    .compact();
        }
        return Token.builder()
                .token(token)
                .refreshToken(refreshToken)
                .expires(expires)
                .build();
    }

    public static Token generateToken(String principal, Map<String, Object> claims) {
        return generateToken(principal, claims, true);
    }

    public static boolean validateToken(String token) {
        try {
            parse(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return false;
    }

    public static Jws<Claims> parse(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
                .setSigningKey(signKey())
                .build();
        return jwtParser.parseClaimsJws(token);
    }

    public static Key signKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_PROPERTIES.getSecret()));
    }

    public static String resolveToken(HttpServletRequest request) {
        //获取token，若请求头中没有则从请求参数中取
        String bearerToken = Optional
                .ofNullable(request.getHeader(JWT_PROPERTIES.getTokenHeader()))
                .orElse(request.getParameter(JWT_PROPERTIES.getTokenParamName()));
        String tokenPrefix = JWT_PROPERTIES.getTokenPrefix();
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(tokenPrefix)) {
            return bearerToken.substring(tokenPrefix.length());
        }
        return null;
    }
}
