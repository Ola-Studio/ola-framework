package io.ola.rbac.service.impl;

import io.ola.crud.service.impl.BaseService;
import io.ola.rbac.entity.Role;
import io.ola.rbac.entity.User;
import io.ola.rbac.service.RoleService;
import io.ola.rbac.service.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author yiuman
 * @date 2023/8/31
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends BaseService<Role> implements RoleService {
    private final UserRoleService userRoleService;

    @Override
    public void bindUserRole(User user, Role role) {
        userRoleService.saveUserRole(user, role);
    }

}
