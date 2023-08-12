package io.ola.security.authorize;

import cn.hutool.core.lang.Assert;
import io.ola.common.utils.WebUtils;
import io.ola.security.authenticate.AuthenticateUtils;
import io.ola.security.exception.NoPermissionException;
import io.ola.security.model.Authentication;
import io.ola.security.properties.SecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * 授权处理拦截器
 *
 * @author yiuman
 * @date 2023/8/8
 */
@RequiredArgsConstructor
public class AuthorizeHandlerInterceptor implements HandlerInterceptor {
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    private final SecurityProperties securityProperties;
    private final List<RequestAuthorizeHandler> requestAuthorizeHandlers;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (Arrays.stream(securityProperties.getExcludedUris())
                .anyMatch(uri -> ANT_PATH_MATCHER.match(uri, request.getRequestURI()))) {
            return true;
        }
        Authentication authentication = AuthenticateUtils.resolve(request);
        Assert.isTrue(requestAuthorizeHandlers.stream().allMatch(requestAuthorizeHandler -> requestAuthorizeHandler.hasPermission(authentication, request)), NoPermissionException::new);
        return true;
    }
}
