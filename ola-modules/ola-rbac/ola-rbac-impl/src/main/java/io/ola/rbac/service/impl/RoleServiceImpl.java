package io.ola.rbac.service.impl;

import cn.hutool.core.lang.Assert;
import io.ola.crud.CRUD;
import io.ola.crud.service.impl.BaseCrudService;
import io.ola.rbac.entity.Role;
import io.ola.rbac.entity.User;
import io.ola.rbac.entity.UserRole;
import io.ola.rbac.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author yiuman
 * @date 2023/8/31
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends BaseCrudService<Role> implements RoleService {

    @Override
    public void bindUserRole(User user, Role role) {
        Assert.notNull(user);
        Assert.notNull(role);
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getId());
        userRole.setRoleId(role.getId());
        CRUD.insertIfNotExists(userRole);
    }

}
