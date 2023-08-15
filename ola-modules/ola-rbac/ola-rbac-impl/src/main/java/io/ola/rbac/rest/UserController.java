package io.ola.rbac.rest;

import io.ola.crud.query.annotation.Query;
import io.ola.crud.rest.BaseRESTAPI;
import io.ola.rbac.entity.User;
import io.ola.rbac.query.UserQuery;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口
 *
 * @author yiuman
 * @date 2023/8/15
 */
@RestController
@RequestMapping("/users")
@Query(UserQuery.class)
public class UserController implements BaseRESTAPI<User> {
}
