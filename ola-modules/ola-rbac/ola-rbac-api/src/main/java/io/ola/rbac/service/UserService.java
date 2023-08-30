package io.ola.rbac.service;

import io.ola.crud.service.CRUDService;
import io.ola.rbac.entity.User;

/**
 * @author yiuman
 * @date 2023/8/4
 */
public interface UserService extends CRUDService<User> {
    User findByUsername(String username);

    User findByMobile(String mobile);

    User getCurrentUser();
}