package io.ola.rbac.service;

import io.ola.rbac.entity.Role;
import io.ola.rbac.entity.User;

/**
 * @author yiuman
 * @date 2024/5/6
 */
public interface UserRoleService {

    void saveUserRole(User user, Role role);
}