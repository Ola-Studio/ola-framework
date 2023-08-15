package io.ola.security.authenticate;

import cn.hutool.core.util.TypeUtil;
import io.ola.security.model.Authentication;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @param <VM> 认证请求视图模型
 * @author yiuman
 * @date 2023/8/8
 */
public interface AuthenticateService<VM> {

    Authentication authenticate(VM authenticationVM);

    default Authentication resolve(HttpServletRequest request) {
        return AuthenticateUtils.resolve(request);
    }

    default void logout(Authentication authentication) {
    }

    String grantType();

    @SuppressWarnings("unchecked")
    default Class<VM> getModelClass() {
        return (Class<VM>) TypeUtil.getTypeArgument(getClass(), 0);
    }

}