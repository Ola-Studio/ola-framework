package io.ola.security.authenticate;

import io.ola.common.http.R;
import io.ola.common.utils.SpringUtils;
import io.ola.security.constants.SecurityConstants;
import io.ola.security.model.Authentication;
import io.ola.security.model.Token;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

/**
 * @author yiuman
 * @date 2023/8/8
 */
@SuppressWarnings("MVCPathVariableInspection")
public interface AuthenticateAPI {

    @RequestMapping("#{securityProperties.authenticateEndpoint}")
    default R<Token> authenticate(HttpServletRequest request) {
        Authentication authentication = AuthenticateUtils.authentication(request);
        TokenService tokenService = SpringUtils.getBean(TokenService.class);
        return R.ok(tokenService.create(authentication.getPrincipal(), new HashMap<>() {{
            put(SecurityConstants.GRANT_TYPE, authentication.getGrantType());
        }}));
    }

    @RequestMapping("#{securityProperties.logoutEndpoint}")
    default R<Void> logout(HttpServletRequest request) {
        Authentication authentication = AuthenticateUtils.resolve(request);
        AuthenticateService<?> authenticateService = AuthenticateUtils.getAuthenticateService(authentication.getGrantType());
        authenticateService.logout(authentication);
        return R.ok();
    }
}