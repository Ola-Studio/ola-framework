package io.ola.security.authenticate;

import io.ola.security.model.Token;

import java.util.Map;

/**
 * @author yiuman
 * @date 2023/8/8
 */
public interface TokenService {
    Token create(Object principal, Map<String, Object> claims);

    boolean validate(String token);

}