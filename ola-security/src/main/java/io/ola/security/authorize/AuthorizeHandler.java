package io.ola.security.authorize;

import io.ola.security.model.Authentication;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author yiuman
 * @date 2023/8/8
 */
public interface AuthorizeHandler {

    boolean hasPermission(Authentication authentication, HttpServletRequest request);
}