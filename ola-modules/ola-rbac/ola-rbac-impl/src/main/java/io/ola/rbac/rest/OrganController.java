package io.ola.rbac.rest;

import io.ola.crud.query.annotation.Query;
import io.ola.crud.rest.BaseRESTAPI;
import io.ola.rbac.entity.Organ;
import io.ola.rbac.query.OrganQuery;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 组织机构接口
 *
 * @author yiuman
 * @date 2023/9/4
 */
@RestController
@RequestMapping("/organs")
@Query(OrganQuery.class)
public class OrganController implements BaseRESTAPI<Organ> {
}
