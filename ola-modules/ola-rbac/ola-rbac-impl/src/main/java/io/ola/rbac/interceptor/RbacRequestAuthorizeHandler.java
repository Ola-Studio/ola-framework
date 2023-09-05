package io.ola.rbac.interceptor;

import io.ola.rbac.entity.Resource;
import io.ola.rbac.mode.UserOnlineInfo;
import io.ola.rbac.utils.RbacUtils;
import io.ola.security.authorize.RequestAuthorizeHandler;
import io.ola.security.model.Authentication;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author yiuman
 * @date 2023/9/5
 */
@RequiredArgsConstructor
@Component
public class RbacRequestAuthorizeHandler implements RequestAuthorizeHandler {

    @Override
    public boolean hasPermission(Authentication authentication, HttpServletRequest request) {
        UserOnlineInfo userOnlineInfo = RbacUtils.getUserOnlineInfo();
        Resource currentResource = userOnlineInfo.getCurrentResource();
        if (Objects.isNull(currentResource)) {
            return true;
        }
        if (Objects.isNull(userOnlineInfo.getUser())) {
            return false;
        }
        return userOnlineInfo.getUserResources().stream()
                .anyMatch(resource -> Objects.equals(currentResource.getId(), resource.getId()));
    }
}
