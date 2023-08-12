package io.ola.security.authenticate;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.ola.security.model.Authentication;
import io.ola.security.model.RefreshTokenVM;
import lombok.RequiredArgsConstructor;

/**
 * 刷新token认证实现
 *
 * @author yiuman
 * @date 2023/8/11
 */
@SuppressWarnings("rawtypes")
@RequiredArgsConstructor
public class RefreshTokenAuthenticateServiceImpl implements AuthenticateService<RefreshTokenVM> {
    private final TokenService tokenService;

    @Override
    public Authentication authenticate(RefreshTokenVM authenticationVM) {
        Jwt<Header, Claims> jwt = tokenService.parse(authenticationVM.getRefreshToken());
        Claims body = jwt.getBody();
        return new Authentication.Default(body.get(JwtUtils.JWT_PROPERTIES.getIdentityKey()));
    }

    @Override
    public void logout(Authentication authentication) {

    }

    @Override
    public String grantType() {
        return RefreshTokenVM.GRANT_TYPE;
    }
}
