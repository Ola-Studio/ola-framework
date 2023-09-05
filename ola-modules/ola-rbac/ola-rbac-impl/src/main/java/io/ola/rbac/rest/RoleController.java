package io.ola.rbac.rest;

import io.ola.crud.query.annotation.Query;
import io.ola.crud.rest.BaseRESTAPI;
import io.ola.rbac.entity.Role;
import io.ola.rbac.query.RoleQuery;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 角色接口
 *
 * @author yiuman
 * @date 2023/9/4
 */
@RestController
@RequestMapping("/roles")
@Query(RoleQuery.class)
public class RoleController implements BaseRESTAPI<Role> {
}
