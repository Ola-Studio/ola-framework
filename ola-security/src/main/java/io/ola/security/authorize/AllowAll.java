package io.ola.security.authorize;

import io.ola.security.model.Authentication;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author yiuman
 * @date 2023/8/8
 */
public class AllowAll implements AuthorizeHandler {

    @Override
    public boolean hasPermission(Authentication authentication, HttpServletRequest request) {
        return true;
    }
}
