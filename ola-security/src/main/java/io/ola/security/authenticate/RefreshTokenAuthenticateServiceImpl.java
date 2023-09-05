package io.ola.security.authenticate;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.ola.security.model.Authentication;
import io.ola.security.model.RefreshTokenVM;
import lombok.RequiredArgsConstructor;

/**
 * 刷新token认证实现
 *
 * @author yiuman
 * @date 2023/8/11
 */
@RequiredArgsConstructor
public class RefreshTokenAuthenticateServiceImpl implements AuthenticateService<RefreshTokenVM> {
    private final TokenService tokenService;

    @Override
    public Authentication authenticate(RefreshTokenVM authenticationVM) {
        Jws<Claims> jws = tokenService.parse(authenticationVM.getRefreshToken());
        Claims body = jws.getBody();
        return new Authentication.Default(body.get(JwtUtils.JWT_PROPERTIES.getIdentityKey()), true);
    }

    @Override
    public String grantType() {
        return RefreshTokenVM.GRANT_TYPE;
    }
}
