package io.ola.rbac.service.impl;

import cn.hutool.core.lang.Assert;
import io.ola.crud.service.impl.BaseService;
import io.ola.rbac.entity.Role;
import io.ola.rbac.entity.User;
import io.ola.rbac.entity.UserRole;
import io.ola.rbac.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * @author yiuman
 * @date 2024/5/6
 */
@Service
public class UserRoleServiceImpl extends BaseService<UserRole> implements UserRoleService {

    @Override
    public void saveUserRole(User user, Role role) {
        Assert.notNull(user);
        Assert.notNull(role);
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        save(userRole);
    }
}
