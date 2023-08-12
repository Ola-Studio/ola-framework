package io.ola.security.authorize;

import io.ola.security.model.Authentication;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Objects;

/**
 * @author yiuman
 * @date 2023/8/9
 */
public interface RequestAuthorizeHandler extends AuthorizeHandler {

    class Default implements RequestAuthorizeHandler {

        @Override
        public boolean hasPermission(Authentication authentication, HttpServletRequest request) {
            return Objects.nonNull(authentication);
        }
    }
}