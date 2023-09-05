package io.ola.rbac.interceptor;

import io.ola.rbac.utils.RbacUtils;
import io.ola.security.authorize.AuthorizeHandlerInterceptor;
import io.ola.security.authorize.RequestAuthorizeHandler;
import io.ola.security.properties.SecurityProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yiuman
 * @date 2023/9/5
 */
@Component
public class RbacContextHandlerInterceptor extends AuthorizeHandlerInterceptor {

    public RbacContextHandlerInterceptor(SecurityProperties securityProperties, List<RequestAuthorizeHandler> requestAuthorizeHandlers) {
        super(securityProperties, requestAuthorizeHandlers);
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) throws Exception {
        RbacUtils.clear();
        super.afterCompletion(request, response, handler, ex);
    }
}
