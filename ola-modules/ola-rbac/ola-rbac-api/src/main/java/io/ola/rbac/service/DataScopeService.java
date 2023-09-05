package io.ola.rbac.service;

import cn.hutool.core.collection.CollUtil;
import io.ola.crud.service.CrudService;
import io.ola.rbac.entity.DataScope;
import io.ola.rbac.model.DataScopeModel;
import io.ola.rbac.query.DataScopeQuery;

import java.util.Collection;
import java.util.List;

/**
 * @author yiuman
 * @date 2023/8/4
 */
public interface DataScopeService extends CrudService<DataScope> {
    DataScopeModel getReadDataScope(String resourceId, String userId);

    DataScopeModel getWriteDataScope(String resourceId, String userId);

    List<DataScope> findListByQuery(DataScopeQuery dataScopeQuery);

    default List<DataScope> findListByRoleIdAndResourceId(String roleId, String resourceId) {
        return findListByQuery(
                DataScopeQuery.builder().resourceId(resourceId)
                        .roleIds(CollUtil.newHashSet(roleId))
                        .build()
        );
    }

    default List<DataScope> findListByRoleIdsAndResourceId(Collection<String> roleIds, String resourceId) {
        return findListByQuery(
                DataScopeQuery.builder().resourceId(resourceId)
                        .roleIds(roleIds)
                        .build()
        );
    }

}