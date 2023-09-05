package io.ola.security.authenticate;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.ola.security.model.Token;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * @author yiuman
 * @date 2023/8/8
 */
public interface TokenService {
    default Token create(Object principal, Map<String, Object> claims) {
        return JwtUtils.generateToken(principal.toString(), claims);
    }

    default boolean validate(String token) {
        return JwtUtils.validateToken(token);
    }

    default String getToken(HttpServletRequest request) {
        return JwtUtils.resolveToken(request);
    }

    default Jws<Claims> parse(String token) {
        return JwtUtils.parse(token);
    }

}