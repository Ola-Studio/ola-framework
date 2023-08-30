package io.ola.rbac.rest;

import io.ola.common.http.R;
import io.ola.crud.query.annotation.Query;
import io.ola.crud.rest.BaseRESTAPI;
import io.ola.rbac.entity.User;
import io.ola.rbac.query.UserQuery;
import io.ola.rbac.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequiredArgsConstructor
public class UserController implements BaseRESTAPI<User> {

    private final UserService userService;

    @GetMapping("/current")
    public R<User> getCurrentUser() {
        return R.ok(userService.getCurrentUser());
    }
}
