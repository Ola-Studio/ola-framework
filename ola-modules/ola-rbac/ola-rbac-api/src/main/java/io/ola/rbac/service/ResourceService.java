package io.ola.rbac.service;

import io.ola.crud.service.CrudService;
import io.ola.rbac.entity.Resource;
import io.ola.rbac.query.ResourceQuery;

import java.util.List;

/**
 * @author yiuman
 * @date 2023/8/14
 */
public interface ResourceService extends CrudService<Resource> {
    default List<Resource> findListByUserId(String userId) {
        return findListByQuery(ResourceQuery.builder().userId(userId).build());
    }

    default Resource findOneByURI(String uri) {
        return findOneByQuery(ResourceQuery.builder().uri(uri).build());
    }

    List<Resource> findListByQuery(ResourceQuery resourceQuery);

    Resource findOneByQuery(ResourceQuery resourceQuery);

}