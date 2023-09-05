package io.ola.security.authenticate;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.ola.common.utils.SpringUtils;
import io.ola.common.utils.WebUtils;
import io.ola.security.constants.SecurityConstants;
import io.ola.security.exception.AuthenticationException;
import io.ola.security.model.Authentication;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author yiuman
 * @date 2023/8/9
 */
public final class AuthenticateUtils {
    private static final ThreadLocal<Authentication> AUTHENTICATION_THREAD_LOCAL
            = ThreadUtil.createThreadLocal(true);

    private AuthenticateUtils() {
    }

    public static <VM> Authentication authentication(HttpServletRequest request) {
        String grantType = Optional.ofNullable(WebUtils.getParameter(request, SecurityConstants.GRANT_TYPE))
                .orElse(WebUtils.getRequestHeader(SecurityConstants.GRANT_TYPE));
        Assert.notBlank(grantType, () -> new ValidateException("The grantType parameter cannot be empty"));
        AuthenticateService<VM> authenticateService = getAuthenticateService(grantType);
        VM authenticateVM = WebUtils.requestDataBind(authenticateService.getModelClass(), request);
        Authentication authenticate = authenticateService.authenticate(authenticateVM);
        authenticate.setGrantType(grantType);
        authenticate.setAuthenticated(true);
        return authenticate;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <VM> AuthenticateService<VM> getAuthenticateService(String grantType) {
        Map<String, AuthenticateService> authenticateServiceMap = SpringUtils.getBeansOfType(AuthenticateService.class);
        AuthenticateService<VM> authenticateService = authenticateServiceMap.get(grantType);
        if (Objects.isNull(authenticateService)) {
            authenticateService = authenticateServiceMap.values().stream().filter(
                            service -> Objects.equals(grantType, service.grantType())
                    ).findFirst()
                    .orElseThrow(() -> new AuthenticationException(String.format("The grantType '%s' is not supported", grantType)));
        }
        return authenticateService;
    }

    public static Authentication resolve(HttpServletRequest request) {
        TokenService tokenService = SpringUtils.getBean(TokenService.class);
        String token = tokenService.getToken(request);
        if (StrUtil.isBlank(token)) {
            return Authentication.ANONYMOUS;
        }
        Jws<Claims> jws = tokenService.parse(token);
        Claims claims = jws.getBody();
        String grantType = (String) claims.get(SecurityConstants.GRANT_TYPE);
        Authentication.Default authentication = new Authentication.Default(
                claims.get(JwtUtils.JWT_PROPERTIES.getIdentityKey()),
                token
        );
        authentication.setDetails(claims);
        authentication.setGrantType(grantType);
        setAuthentication(authentication);
        return authentication;
    }

    public static Authentication getAuthentication() {
        return AUTHENTICATION_THREAD_LOCAL.get();
    }

    public static void setAuthentication(Authentication authentication) {
        AUTHENTICATION_THREAD_LOCAL.set(authentication);
    }

}
