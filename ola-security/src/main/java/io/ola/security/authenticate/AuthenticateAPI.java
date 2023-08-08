package io.ola.security.authenticate;

import io.ola.common.http.R;
import io.ola.common.utils.SpringUtils;
import io.ola.common.utils.WebUtils;
import io.ola.security.constants.SecurityConstants;
import io.ola.security.exception.AuthenticationException;
import io.ola.security.model.Authentication;
import io.ola.security.model.Token;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author yiuman
 * @date 2023/8/8
 */
@SuppressWarnings("MVCPathVariableInspection")
public interface AuthenticateAPI {

    @SuppressWarnings({"rawtypes", "unchecked"})
    @RequestMapping("#{ola.security.authenticateEndpoint}")
    default R<Token> authenticate(HttpServletRequest request) {
        String grantType = Optional.ofNullable(WebUtils.getParameter(request, SecurityConstants.GRANT_TYPE))
                .orElse(WebUtils.getRequestHeader(SecurityConstants.GRANT_TYPE));
        Map<String, AuthenticateService> authenticateServiceMap = SpringUtils.getBeansOfType(AuthenticateService.class);
        AuthenticateService authenticateService = authenticateServiceMap.get(grantType);
        if (Objects.isNull(authenticateService)) {
            authenticateService = authenticateServiceMap.values().stream().filter(
                            service -> Objects.equals(grantType, service.grantType())
                    ).findFirst()
                    .orElseThrow(() -> new AuthenticationException(String.format("The grantType '%s' is not supported", grantType)));
        }

        TokenService tokenService = SpringUtils.getBean(TokenService.class);

        Object authenticateVM = WebUtils.requestDataBind(authenticateService.getModelClass(), request);
        Authentication authenticate = authenticateService.authenticate(authenticateVM);
        return R.ok(tokenService.create(authenticate.getPrincipal(), Map.of(SecurityConstants.GRANT_TYPE, grantType)));
    }

    @RequestMapping("#{ola.security.logoutEndpoint}")
    default R<Void> logout(HttpServletRequest request) {
        return R.ok();
    }
}