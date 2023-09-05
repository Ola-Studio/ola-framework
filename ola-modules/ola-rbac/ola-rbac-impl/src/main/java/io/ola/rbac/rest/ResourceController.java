package io.ola.rbac.rest;

import io.ola.crud.query.annotation.Query;
import io.ola.crud.rest.BaseQueryAPI;
import io.ola.rbac.entity.Resource;
import io.ola.rbac.query.MenuQuery;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜单接口
 *
 * @author yiuman
 * @date 2023/9/4
 */
@RestController
@RequestMapping("/menus")
@Query(MenuQuery.class)
public class ResourceController implements BaseQueryAPI<Resource> {
}
