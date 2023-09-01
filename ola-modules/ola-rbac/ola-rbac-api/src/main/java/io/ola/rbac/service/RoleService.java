package io.ola.rbac.service;

import io.ola.crud.service.CRUDService;
import io.ola.rbac.entity.Role;
import io.ola.rbac.entity.User;

/**
 * @author yiuman
 * @date 2023/8/14
 */
public interface RoleService extends CRUDService<Role> {

    void bindUserRole(User user, Role role);
}