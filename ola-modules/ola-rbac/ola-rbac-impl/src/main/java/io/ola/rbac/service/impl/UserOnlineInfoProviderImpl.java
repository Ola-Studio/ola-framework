package io.ola.rbac.service.impl;

import io.ola.common.utils.WebUtils;
import io.ola.rbac.entity.User;
import io.ola.rbac.mode.UserOnlineInfo;
import io.ola.rbac.service.ResourceService;
import io.ola.rbac.service.UserOnlineInfoProvider;
import io.ola.rbac.service.UserService;
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
public class UserOnlineInfoProviderImpl implements UserOnlineInfoProvider {

    private final UserService userService;
    private final ResourceService resourceService;

    @Override
    public UserOnlineInfo get() {
        UserOnlineInfo userOnlineInfo = new UserOnlineInfo();
        User currentUser = userService.getCurrentUser();
        if (Objects.nonNull(currentUser)) {
            userOnlineInfo.setUser(currentUser);
            userOnlineInfo.setRoles(currentUser.getRoles());
            HttpServletRequest request = WebUtils.getRequest();
            userOnlineInfo.setCurrentRequest(request);
            String requestMapping = WebUtils.getRequestMapping(request);
            userOnlineInfo.setCurrentResource(resourceService.findOneByURI(requestMapping));
            userOnlineInfo.setUserResources(resourceService.findListByUserId(currentUser.getId()));
        }
        return userOnlineInfo;
    }

}
