package io.ola.security.authorize;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import io.ola.common.utils.SpringUtils;
import io.ola.common.utils.WebUtils;
import io.ola.security.authenticate.AuthenticateUtils;
import io.ola.security.exception.AuthenticationException;
import io.ola.security.exception.NoPermissionException;
import io.ola.security.model.Authentication;
import io.ola.security.properties.SecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 授权处理拦截器
 *
 * @author yiuman
 * @date 2023/8/8
 */
@RequiredArgsConstructor
@Slf4j
public class AuthorizeHandlerInterceptor implements HandlerInterceptor {
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    private final SecurityProperties securityProperties;
    private final List<RequestAuthorizeHandler> requestAuthorizeHandlers;

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (isExcludeRequest(CollUtil.newArrayList(securityProperties.getAuthenticateEndpoint(),
                securityProperties.getLogoutEndpoint(),
                securityProperties.getVerifyEndpointPrefix()), request)
        ) {
            return true;
        }
        if (CollUtil.isNotEmpty(securityProperties.getExcludedUris())
                && isExcludeRequest(securityProperties.getExcludedUris(), request)) {
            return true;
        }

        try {
            Authentication authentication = AuthenticateUtils.resolve(request);
            HandlerMethod requestHandlerMethod = WebUtils.getRequestHandlerMethod(request);
            Authorize authorize = Optional.ofNullable(
                    AnnotationUtil.getAnnotation(requestHandlerMethod.getMethod(), Authorize.class)
            ).orElse(AnnotationUtil.getAnnotation(requestHandlerMethod.getBeanType(), Authorize.class));
            if (Objects.nonNull(authorize)) {
                Class<? extends AuthorizeHandler> authorizeHandlerClass = authorize.value();
                AuthorizeHandler authorizeHandler = SpringUtils.getBean(authorizeHandlerClass, true);
                return authorizeHandler.hasPermission(authentication, request);
            }

            Assert.isTrue(authentication.isAuthenticated(), AuthenticationException::new);

            Assert.isTrue(requestAuthorizeHandlers.stream().allMatch(requestAuthorizeHandler -> requestAuthorizeHandler.hasPermission(authentication, request)), NoPermissionException::new);
            return true;

        } catch (Throwable throwable) {
            log.info("resolve Authentication happen error", throwable);
            throw new AuthenticationException();
        }


    }

    private boolean isExcludeRequest(List<String> excludedUris, HttpServletRequest request) {
        String realUri = request.getRequestURI().replaceAll(request.getContextPath(), "");
        return excludedUris.stream()
                .anyMatch(uri -> ANT_PATH_MATCHER.match(uri, realUri));
    }
}
