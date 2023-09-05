package io.ola.rbac.authenticate;

import cn.hutool.core.lang.Assert;
import io.ola.rbac.entity.User;
import io.ola.rbac.service.PasswordEncoder;
import io.ola.rbac.service.UserService;
import io.ola.security.authenticate.AuthenticateService;
import io.ola.security.exception.AuthenticationException;
import io.ola.security.model.Authentication;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 账号密码登录
 *
 * @author yiuman
 * @date 2023/8/15
 */
@Service
@RequiredArgsConstructor
public class UsernamePasswordAuthenticateServiceImpl implements AuthenticateService<PasswordLoginVM> {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private static final AuthenticationException PASSWORD_ERROR = new AuthenticationException("用户名或密码错误");

    @Override
    public Authentication authenticate(PasswordLoginVM authenticationVM) {
        User user = Optional.ofNullable(userService.findByUsername(authenticationVM.getLoginId()))
                .orElse(userService.findByMobile(authenticationVM.getLoginId()));
        Assert.notNull(user, () -> PASSWORD_ERROR);
        Assert.equals(user.getPassword(), passwordEncoder.encode(authenticationVM.getPassword()), () -> PASSWORD_ERROR);
        return new Authentication.Default(user.getId(), true);
    }


    @Override
    public String grantType() {
        return "password";
    }
}
